package fintech.task

import fintech.TimeMachine
import fintech.task.command.AddTaskCommand
import fintech.task.command.AssignTaskCommand
import fintech.task.command.CancelTaskCommand
import fintech.task.command.CompleteTaskCommand
import fintech.task.command.ReopenTaskCommand
import fintech.task.db.Entities
import fintech.task.db.TaskLogEntity
import fintech.task.db.TaskLogRepository
import fintech.task.db.TaskRepository
import fintech.task.model.TaskLog
import fintech.task.model.TaskStatus
import fintech.task.spi.TaskDefinitionBuilder
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime

import static fintech.DateUtils.dateTime

class TaskHierarchyIntegrationTest extends BaseSpecification {

    @Autowired
    private TaskLogRepository taskLogRepository

    @Autowired
    private TaskRepository taskRepository

    def setup() {
        def parentTaskDefinition = new TaskDefinitionBuilder("parentTask")
            .group("Test")
            .resolution("OK").onCompleted(MockTaskListener.class, "arg").add()
            .resolution("EXPIRED").add()
            .defaultExpireResolution("EXPIRED")
            .build()
        def childTaskDefinition1 = new TaskDefinitionBuilder("childTask1")
            .group("Test")
            .dependsOnTask("parentTask")
            .resolution("OK").onCompleted(MockTaskListener.class, "arg").add()
            .resolution("POSTPONED").asPostpone().onPostponed(MockTaskListener.class, "arg").add()
            .resolution("NOT_OK").asPostpone().add()
            .resolution("EXPIRED").add()
            .defaultExpireResolution("EXPIRED")
            .build()
        def childTaskDefinition2 = new TaskDefinitionBuilder("childTask2")
            .group("Test")
            .dependsOnTask("parentTask")
            .resolution("OK").onCompleted(MockTaskListener.class, "arg").add()
            .resolution("POSTPONED").asPostpone().onPostponed(MockTaskListener.class, "arg").add()
            .resolution("NOT_OK").asPostpone().add()
            .resolution("EXPIRED").add()
            .defaultExpireResolution("EXPIRED")
            .build()
        taskRegistry.addDefinition { parentTaskDefinition }
        taskRegistry.addDefinition { childTaskDefinition1 }
        taskRegistry.addDefinition { childTaskDefinition2 }
    }

    def "Add children task without parent"() {
        when:
        taskService.addTask(new AddTaskCommand(clientId: 1, type: "childTask1", expiresAt: LocalDateTime.now().plusDays(30)))

        then:
        thrown(IllegalArgumentException)
    }

    def "Add children task with parent"() {
        when:
        def parentTaskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "parentTask", expiresAt: LocalDateTime.now().plusDays(30)))
        def childTaskId1 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "childTask1", expiresAt: LocalDateTime.now().plusDays(30), parentTaskId: parentTaskId))
        def childTaskId2 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "childTask2", expiresAt: LocalDateTime.now().plusDays(30), parentTaskId: parentTaskId))

        then:
        with(taskRepository.getRequired(childTaskId1)) {
            it.parentTaskId == parentTaskId
            log(it.id).count { it.operation == TaskLog.Operation.CREATED } == 1
        }
        with(taskRepository.getRequired(childTaskId2)) {
            it.parentTaskId == parentTaskId
            log(it.id).count { it.operation == TaskLog.Operation.CREATED } == 1
        }
    }

    def "Autoassign children tasks"() {
        when:
        def parentTaskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "parentTask", expiresAt: LocalDateTime.now().plusDays(30)))
        def childTaskId1 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "childTask1", expiresAt: LocalDateTime.now().plusDays(30), parentTaskId: parentTaskId))
        taskService.assignTask(new AssignTaskCommand(taskId: parentTaskId, agent: "james.bond"))
        def childTaskId2 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "childTask2", expiresAt: LocalDateTime.now().plusDays(30), parentTaskId: parentTaskId))

        then:
        with(taskRepository.getRequired(childTaskId1)) {
            it.agent == "james.bond"
            log(it.id).count { it.operation == TaskLog.Operation.ASSIGNED } == 1
        }
        with(taskRepository.getRequired(childTaskId2)) {
            it.agent == "james.bond"
            log(it.id).count { it.operation == TaskLog.Operation.ASSIGNED } == 1
        }
    }

    def "Complete parent task"() {
        when:
        def parentTaskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "parentTask", expiresAt: LocalDateTime.now().plusDays(30)))
        def childTaskId1 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "childTask1", expiresAt: LocalDateTime.now().plusDays(30), parentTaskId: parentTaskId))
        def childTaskId2 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "childTask2", expiresAt: LocalDateTime.now().plusDays(30), parentTaskId: parentTaskId))

        taskService.completeTask(new CompleteTaskCommand(taskId: parentTaskId, resolution: "OK"))

        then:
        thrown(IllegalArgumentException)

        when:
        taskService.completeTask(new CompleteTaskCommand(taskId: childTaskId1, resolution: "OK"))
        taskService.completeTask(new CompleteTaskCommand(taskId: childTaskId2, resolution: "OK"))
        taskService.completeTask(new CompleteTaskCommand(taskId: parentTaskId, resolution: "OK"))

        then:
        with(taskRepository.getRequired(parentTaskId)) {
            it.status == TaskStatus.COMPLETED
            it.resolution == "OK"
            log(it.id).count { it.operation == TaskLog.Operation.COMPLETED } == 1
        }
    }

    def "Cancel children tasks"() {
        when:
        def parentTaskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "parentTask", expiresAt: LocalDateTime.now().plusDays(30)))
        def childTaskId1 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "childTask1", expiresAt: LocalDateTime.now().plusDays(30), parentTaskId: parentTaskId))
        def childTaskId2 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "childTask2", expiresAt: LocalDateTime.now().plusDays(30), parentTaskId: parentTaskId))
        taskService.cancelTask(new CancelTaskCommand(taskId: parentTaskId))

        then:
        with(taskRepository.getRequired(parentTaskId)) {
            it.status == TaskStatus.CANCELLED
            log(it.id).count { it.operation == TaskLog.Operation.CREATED } == 1
            log(it.id).count { it.operation == TaskLog.Operation.CANCELLED } == 1
        }
        with(taskRepository.getRequired(childTaskId1)) {
            it.status == TaskStatus.CANCELLED
            log(it.id).count { it.operation == TaskLog.Operation.CREATED } == 1
            log(it.id).count { it.operation == TaskLog.Operation.CANCELLED } == 1
        }
        with(taskRepository.getRequired(childTaskId2)) {
            it.status == TaskStatus.CANCELLED
            log(it.id).count { it.operation == TaskLog.Operation.CREATED } == 1
            log(it.id).count { it.operation == TaskLog.Operation.CANCELLED } == 1
        }
    }

    def "Don't reopen children tasks"() {
        when:
        def parentTaskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "parentTask", expiresAt: LocalDateTime.now().plusDays(30)))
        def childTaskId1 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "childTask1", expiresAt: LocalDateTime.now().plusDays(30), parentTaskId: parentTaskId))
        def childTaskId2 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "childTask2", expiresAt: LocalDateTime.now().plusDays(30), parentTaskId: parentTaskId))
        taskService.cancelTask(new CancelTaskCommand(taskId: parentTaskId))

        then:
        with(taskRepository.getRequired(parentTaskId)) {
            it.status == TaskStatus.CANCELLED
        }
        with(taskRepository.getRequired(childTaskId1)) {
            it.status == TaskStatus.CANCELLED
        }
        with(taskRepository.getRequired(childTaskId2)) {
            it.status == TaskStatus.CANCELLED
        }

        when:
        taskService.reopenTask(new ReopenTaskCommand(taskId: parentTaskId, dueAt: TimeMachine.now().plusHours(24), expiresAt: TimeMachine.now().plusDays(5)))

        then:
        with(taskRepository.getRequired(parentTaskId)) {
            it.status == TaskStatus.OPEN
            log(it.id).count { it.operation == TaskLog.Operation.REOPENED } == 1
        }
        with(taskRepository.getRequired(childTaskId1)) {
            it.status == TaskStatus.CANCELLED
            log(it.id).count { it.operation == TaskLog.Operation.REOPENED } == 0
        }
        with(taskRepository.getRequired(childTaskId2)) {
            it.status == TaskStatus.CANCELLED
            log(it.id).count { it.operation == TaskLog.Operation.REOPENED } == 0
        }
    }

    def "Expire children tasks with default resolution"() {
        given:
        def parentTaskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "parentTask", expiresAt: dateTime("2016-01-02 00:00:00")))
        def childTaskId1 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "childTask1", expiresAt: dateTime("2016-01-02 00:00:00"), parentTaskId: parentTaskId))
        def childTaskId2 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "childTask2", expiresAt: dateTime("2016-01-02 00:00:00"), parentTaskId: parentTaskId))

        when:
        expiredTaskConsumer.consume(dateTime("2016-02-01 00:00:01")) == 1

        then:
        with(taskRepository.getRequired(parentTaskId)) {
            it.status == TaskStatus.COMPLETED
            it.resolution == "EXPIRED"
            it.agent == "SYSTEM"
            log(it.id).count { it.operation == TaskLog.Operation.COMPLETED } == 1
        }
        with(taskRepository.getRequired(childTaskId1)) {
            it.status == TaskStatus.COMPLETED
            it.resolution == "EXPIRED"
            it.agent == "SYSTEM"
            log(it.id).count { it.operation == TaskLog.Operation.COMPLETED } == 1
        }
        with(taskRepository.getRequired(childTaskId2)) {
            it.status == TaskStatus.COMPLETED
            it.resolution == "EXPIRED"
            it.agent == "SYSTEM"
            log(it.id).count { it.operation == TaskLog.Operation.COMPLETED } == 1
        }
    }

    private List<TaskLogEntity> log(long taskId) {
        taskLogRepository.findAll(Entities.taskLog.taskId.eq(taskId), Entities.taskLog.id.asc())
    }
}

package fintech.task

import fintech.task.command.*
import fintech.task.db.Entities
import fintech.task.db.TaskLogEntity
import fintech.task.db.TaskLogRepository
import fintech.task.model.Task
import fintech.task.model.TaskLog
import fintech.task.model.TaskQuery
import fintech.task.model.TaskStatus
import fintech.task.spi.TaskDefinitionBuilder
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime

import static fintech.DateUtils.dateTime

class TaskIntegrationTest extends BaseSpecification {

    @Autowired
    private TaskLogRepository taskLogRepository

    def setup() {
        def definition = new TaskDefinitionBuilder("testTask")
            .group("Test")
            .resolution("OK").onCompleted(MockTaskListener.class, "arg").add()
            .resolution("POSTPONED").onPostponed(MockTaskListener.class, "arg").add()
            .resolution("NOT_OK").asPostpone().add()
            .resolution("EXPIRED").add()
            .defaultExpireResolution("EXPIRED")
            .build()
        taskRegistry.addDefinition { definition }
    }

    def "Add task"() {
        expect:
        taskService.addTask(new AddTaskCommand(clientId: 1, type: "testTask", expiresAt: LocalDateTime.now().plusDays(30))) != null
    }

    def "Assign task"() {
        given:
        def taskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "testTask", dueAt: dateTime("2016-01-01 00:00:00"), expiresAt: dateTime("2016-02-01 00:00:00")))

        when:
        taskService.assignTask(new AssignTaskCommand(taskId: taskId, agent: "james.bond", when: dateTime("2016-01-01 00:01:00")))

        then:
        def task = taskService.get(taskId)
        task.agent == "james.bond"
        log(task).count { it.operation == TaskLog.Operation.ASSIGNED } == 1

        when: "Assign again to same agent very shortly"
        taskService.assignTask(new AssignTaskCommand(taskId: taskId, agent: "james.bond", when: dateTime("2016-01-01 00:30:00")))
        task = taskService.get(taskId)

        then: "No log added"
        log(task).count { it.operation == TaskLog.Operation.ASSIGNED } == 1

        when: "Assign again to same agent after longer time period"
        taskService.assignTask(new AssignTaskCommand(taskId: taskId, agent: "james.bond", when: dateTime("2016-01-02 00:30:00")))
        task = taskService.get(taskId)

        then: "Log added"
        log(task).count { it.operation == TaskLog.Operation.ASSIGNED } == 2
    }

    def "Postpone task"() {
        given:
        def taskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "testTask", expiresAt: LocalDateTime.now().plusDays(30)))

        when:
        taskService.postponeTask(new PostponeTaskCommand(taskId: taskId, postponeTo: dateTime("2050-01-01 15:00:00"), resolution: "NOT_OK", expiresAt: LocalDateTime.now().plusDays(30)))

        then:
        def task = taskService.get(taskId)
        task.dueAt == dateTime("2050-01-01 15:00:00")
        task.timesPostponed == 1L
        task.resolution == "NOT_OK"
        log(task).count { it.operation == TaskLog.Operation.POSTPONED } == 1

        and:
        MockTaskListener.executed == 0
    }

    def "Task listener executes on postponed"() {
        given:
        def taskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "testTask", expiresAt: LocalDateTime.now().plusDays(30)))

        when:
        taskService.postponeTask(new PostponeTaskCommand(taskId: taskId, postponeTo: dateTime("2050-01-01 15:00:00"), resolution: "POSTPONED", expiresAt: LocalDateTime.now().plusDays(30)))

        then:
        MockTaskListener.executed == 1
    }

    def "Complete task"() {
        given:
        def taskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "testTask", expiresAt: LocalDateTime.now().plusDays(30)))

        when:
        taskService.completeTask(new CompleteTaskCommand(taskId: taskId, resolution: "OK"))
        def task = taskService.get(taskId)

        then:
        task.status == TaskStatus.COMPLETED
        task.resolution == "OK"
        log(task).count { it.operation == TaskLog.Operation.COMPLETED } == 1

        and:
        MockTaskListener.executed == 1
    }

    def "Expire task with default resolution"() {
        given:
        def taskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "testTask", expiresAt: dateTime("2016-01-02 00:00:00")))

        expect:
        expiredTaskConsumer.consume(dateTime("2016-01-01 23:59:29")) == 0

        when:
        expiredTaskConsumer.consume(dateTime("2016-02-01 00:00:01")) == 1
        def task = taskService.get(taskId)

        then:
        task.status == TaskStatus.COMPLETED
        task.resolution == "EXPIRED"
        task.agent == "SYSTEM"
        log(task).count { it.operation == TaskLog.Operation.COMPLETED } == 1

        and:
        MockTaskListener.executed == 0
    }

    def "Expire task with default resolution even if task has existing resolution"() {
        given:
        def taskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "testTask", expiresAt: dateTime("2016-01-02 00:00:00")))
        taskService.postponeTask(new PostponeTaskCommand(taskId: taskId, postponeTo: dateTime("2016-01-03 00:00:00"), resolution: "NOT_OK", expiresAt: dateTime("2016-01-03 00:00:00")))

        when:
        expiredTaskConsumer.consume(dateTime("2016-02-03 00:00:01")) == 1
        def task = taskService.get(taskId)

        then:
        task.status == TaskStatus.COMPLETED
        task.resolution == "EXPIRED"
        task.agent == "SYSTEM"
        log(task).count { it.operation == TaskLog.Operation.COMPLETED } == 1

        and:
        MockTaskListener.executed == 0
    }


    def "Cancel task"() {
        given:
        def taskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "testTask", expiresAt: LocalDateTime.now().plusDays(30)))

        when:
        taskService.cancelTask(new CancelTaskCommand(taskId: taskId, reason: "Expired"))
        def task = taskService.get(taskId)

        then:
        task.status == TaskStatus.CANCELLED
        task.resolution == "Expired"
        log(task).count { it.operation == TaskLog.Operation.CANCELLED } == 1

        and:
        MockTaskListener.executed == 0
    }


    def "Reopen task"() {
        given:
        def taskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "testTask", expiresAt: LocalDateTime.now().plusDays(30), attributes: [key: "value"]))
        taskService.completeTask(new CompleteTaskCommand(taskId: taskId, resolution: "OK"))

        expect:
        taskService.get(taskId).attributes.key == "value"

        when:
        taskService.reopenTask(new ReopenTaskCommand(taskId: taskId, reason: "do it", dueAt: LocalDateTime.now(), expiresAt: LocalDateTime.now().plusDays(30), attributes: [key: "updated value"]))
        def task = taskService.get(taskId)

        then:
        task.status == TaskStatus.OPEN
        task.timesReopened == 1L
        task.attributes.key == "updated value"
        log(task).count { it.operation == TaskLog.Operation.REOPENED } == 1
    }

    def "Find by activity id"() {
        given:
        def taskA = taskService.addTask(new AddTaskCommand(clientId: 1, activityId: 1, type: "testTask", expiresAt: dateTime("2016-01-02 00:00:00")))
        taskService.addTask(new AddTaskCommand(clientId: 1, activityId: 2, type: "testTask", expiresAt: dateTime("2016-01-02 00:00:00")))

        when:
        def activityTasks = taskService.findTasks(TaskQuery.byActivityId(1))

        then:
        activityTasks.size() == 1
        activityTasks[0].id == taskA
    }

    def "Add task with attributes"() {
        when:
        def id = taskService.addTask(new AddTaskCommand(clientId: 1, type: "testTask", expiresAt: LocalDateTime.now().plusDays(30), attributes: ["a": "Value A", "b": "Value B"]))

        then:
        with(taskService.get(id)) {
            attributes["a"] == "Value A"
            attributes["b"] == "Value B"
        }
    }

    def "Add task attributes"() {
        given:
        def task1Id = taskService.addTask(new AddTaskCommand(clientId: 1, type: "testTask", expiresAt: LocalDateTime.now().plusDays(30), attributes: ["a": "Value A", "b": "Value B"]))
        assert taskService.get(task1Id).attributes.size() == 2
        def task2Id = taskService.addTask(new AddTaskCommand(clientId: 1, type: "testTask", expiresAt: LocalDateTime.now().plusDays(30)))
        assert taskService.get(task2Id).attributes.isEmpty()

        when:
        taskService.addTaskAttributes(new AddTaskAttributesCommand(task1Id, ["c": "Value C"]))

        then:
        with(taskService.get(task1Id)) {
            attributes["a"] == "Value A"
            attributes["b"] == "Value B"
            attributes["c"] == "Value C"
        }

        when:
        taskService.addTaskAttributes(new AddTaskAttributesCommand(task2Id, ["1": "Value 1"]))

        then:
        with(taskService.get(task2Id)) {
            attributes["1"] == "Value 1"
        }

        and:
        with(taskService.get(task1Id)) {
            attributes["a"] == "Value A"
            attributes["b"] == "Value B"
            attributes["c"] == "Value C"
        }
    }

    private List<TaskLogEntity> log(Task task) {
        taskLogRepository.findAll(Entities.taskLog.taskId.eq(task.id), Entities.taskLog.id.asc())
    }
}

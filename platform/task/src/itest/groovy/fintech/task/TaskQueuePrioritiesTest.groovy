package fintech.task

import fintech.task.command.AddAgentCommand
import fintech.task.command.AddTaskCommand
import fintech.task.command.PostponeTaskCommand
import fintech.task.spi.TaskDefinitionBuilder

import static fintech.DateUtils.dateTime

class TaskQueuePrioritiesTest extends BaseSpecification {



    def setup() {
        taskRegistry.addDefinition { new TaskDefinitionBuilder("A").group("Test").priority(10).priorityAfterPostpone(9).resolution("OK").asPostpone().add().defaultExpireResolution("OK").build() }

        agentService.addAgent(new AddAgentCommand(email: "super.agent@mail.com", taskTypes: ["*"]))
    }

    def "postponed tasks have lower priority"() {
        given:
        def task1 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "A", dueAt: dateTime("2016-01-01 10:00:00"), expiresAt: dateTime("2016-02-01 00:00:00")))
        def task2 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "A", dueAt: dateTime("2016-01-01 12:00:00"), expiresAt: dateTime("2016-02-01 00:00:00")))

        expect:
        taskService.get(task1).priority == 10L
        taskService.get(task2).priority == 10L
        taskQueue.assignNextTask("super.agent@mail.com", dateTime("2016-01-01 12:00:01")).get().id == task1

        when:
        taskService.postponeTask(new PostponeTaskCommand(taskId: task1, postponeTo: dateTime("2016-01-01 11:00:00"), resolution: "OK"))

        then:
        taskService.get(task1).priority == 9L
        taskService.get(task2).priority == 10L
        taskQueue.assignNextTask("super.agent@mail.com", dateTime("2016-01-01 12:00:01")).get().id == task2

        when:
        taskService.postponeTask(new PostponeTaskCommand(taskId: task2, postponeTo: dateTime("2016-01-01 11:01:00"), resolution: "OK"))

        then:
        taskQueue.assignNextTask("super.agent@mail.com", dateTime("2016-01-01 12:00:01")).get().id == task1
    }
}

package fintech.task

import fintech.task.command.AddAgentCommand
import fintech.task.command.AddTaskCommand
import fintech.task.command.AssignTaskCommand
import fintech.task.command.CompleteTaskCommand
import fintech.task.command.DisableAgentCommand
import fintech.task.spi.TaskDefinitionBuilder

import static fintech.DateUtils.dateTime

class TaskQueueTest extends BaseSpecification {

    Long task1, task2, task3, task4

    def setup() {
        taskRegistry.addDefinition { new TaskDefinitionBuilder("A").group("Test").priority(0).resolution("OK").add().defaultExpireResolution("OK").build() }
        taskRegistry.addDefinition { new TaskDefinitionBuilder("B").group("Test").priority(1).resolution("OK").add().defaultExpireResolution("OK").build() }
        taskRegistry.addDefinition { new TaskDefinitionBuilder("C").group("Other").priority(0).resolution("OK").add().defaultExpireResolution("OK").build() }

        agentService.addAgent(new AddAgentCommand(email: "a.agent@mail.com", taskTypes: ["A"]))
        agentService.addAgent(new AddAgentCommand(email: "a2.agent@mail.com", taskTypes: ["A"]))
        agentService.addAgent(new AddAgentCommand(email: "b.agent@mail.com", taskTypes: ["B"]))
        agentService.addAgent(new AddAgentCommand(email: "b2.agent@mail.com", taskTypes: ["B"]))
        agentService.addAgent(new AddAgentCommand(email: "super.agent@mail.com", taskTypes: ["*"]))
        agentService.addAgent(new AddAgentCommand(email: "no.agent@mail.com", taskTypes: []))

        task1 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "A", dueAt: dateTime("2016-01-01 10:00:00"), expiresAt: dateTime("2016-02-01 00:00:00")))
        task2 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "A", dueAt: dateTime("2016-01-01 11:00:00"), expiresAt: dateTime("2016-02-01 00:00:00")))
        task3 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "B", dueAt: dateTime("2016-01-01 10:00:00"), expiresAt: dateTime("2016-02-01 00:00:00")))
        task4 = taskService.addTask(new AddTaskCommand(clientId: 1, type: "C", dueAt: dateTime("2016-01-01 10:00:00"), expiresAt: dateTime("2016-02-01 00:00:00")))
    }

    def "Count"() {
        expect:
        taskQueue.count("a.agent@mail.com", dateTime("2016-01-01 10:00:00")).tasksDue == 0
        taskQueue.count("a.agent@mail.com", dateTime("2016-01-01 10:00:01")).tasksDue == 1
        taskQueue.count("a.agent@mail.com", dateTime("2016-01-01 11:00:01")).tasksDue == 2
        taskQueue.count("b.agent@mail.com", dateTime("2016-01-01 10:00:01")).tasksDue == 1
        taskQueue.count("super.agent@mail.com", dateTime("2016-01-01 11:00:01")).tasksDue == 4
        taskQueue.count("unknown.agent@mail.com", dateTime("2016-01-01 11:00:01")).tasksDue == 0

        when:
        taskQueue.assignNextTask("super.agent@mail.com", dateTime("2016-01-01 13:01:00"))

        then:
        taskQueue.count("super.agent@mail.com", dateTime("2016-01-01 13:00:01")).tasksDue == 4
    }

    def "Same task is assigned repeatedly"() {
        expect:
        taskQueue.assignNextTask("a.agent@mail.com", dateTime("2016-01-01 11:00:01")).get().id == task1
        taskQueue.assignNextTask("a.agent@mail.com", dateTime("2016-01-01 11:00:01")).get().id == task1
        taskQueue.assignNextTask("a2.agent@mail.com", dateTime("2016-01-01 11:00:01")).get().id == task2

        and: "After some time other agent can take over the task"
        taskQueue.assignNextTask("a2.agent@mail.com", dateTime("2016-01-02 13:01:00")).get().id == task1
    }

    def "Assign next task by priority"() {
        expect:
        taskQueue.assignNextTask("super.agent@mail.com", dateTime("2016-01-01 11:00:01")).get().id == task3
    }

    def "Ignore unknown agent"() {
        expect:
        !taskQueue.assignNextTask("unknown.agent@mail.com", dateTime("2016-01-01 11:00:01")).isPresent()
    }

    def "Re-assign task if it's waiting too long"() {
        expect:
        taskQueue.assignNextTask("b.agent@mail.com", dateTime("2016-01-01 11:00:01")).get().id == task3
        !taskQueue.assignNextTask("b2.agent@mail.com", dateTime("2016-01-01 11:01:00")).isPresent()
        taskQueue.assignNextTask("b2.agent@mail.com", dateTime("2016-01-02 13:01:00")).get().id == task3
        !taskQueue.assignNextTask("b.agent@mail.com", dateTime("2016-01-02 13:01:01")).isPresent()
    }

    def "No task assigned for agent with no task types"() {
        expect:
        taskQueue.count("no.agent@mail.com", dateTime("2016-01-01 13:00:00")).tasksDue == 0
    }


    def "Expired tasks are not included"() {
        expect:
        taskQueue.count("super.agent@mail.com", dateTime("2016-01-31 23:00:00")).tasksDue == 4
        taskQueue.count("super.agent@mail.com", dateTime("2016-02-01 00:00:01")).tasksDue == 0
    }

    def "Completed tasks are not included"() {
        expect:
        taskQueue.count("super.agent@mail.com", dateTime("2016-01-31 23:00:00")).tasksDue == 4

        when:
        taskService.completeTask(new CompleteTaskCommand(taskId: task1, resolution: "OK"))

        then:
        taskQueue.count("super.agent@mail.com", dateTime("2016-01-31 23:00:00")).tasksDue == 3
    }

    def "Disabled agent can't take next task"() {
        expect:
        taskQueue.count("a.agent@mail.com", dateTime("2016-01-01 10:00:01")).tasksDue == 1

        when:
        agentService.disableAgent(new DisableAgentCommand(email: "a.agent@mail.com"))

        then:
        taskQueue.count("a.agent@mail.com", dateTime("2016-01-01 10:00:01")).tasksDue == 0

        and:
        !taskQueue.assignNextTask("a.agent@mail.com", dateTime("2016-01-01 10:00:01")).isPresent()
    }

    def "Directly assigned tasks are in queue even if no task type configured"() {
        expect:
        taskQueue.count("super.agent@mail.com", dateTime("2016-01-01 13:00:01")).tasksDue == 4
        taskQueue.count("no.agent@mail.com", dateTime("2016-01-01 13:00:01")).tasksDue == 0

        when:
        def task = taskQueue.assignNextTask("super.agent@mail.com", dateTime("2016-01-01 13:00:01")).get()
        taskService.assignTask(new AssignTaskCommand(taskId: task.id, agent: "no.agent@mail.com"))

        then:
        taskQueue.count("super.agent@mail.com", dateTime("2016-01-01 13:00:01")).tasksDue == 3
        taskQueue.count("no.agent@mail.com", dateTime("2016-01-01 13:00:01")).tasksDue == 1
    }
}

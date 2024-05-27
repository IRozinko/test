package fintech.task

import fintech.task.command.AddAgentCommand
import fintech.task.command.AddTaskCommand
import fintech.task.spi.TaskDefinitionBuilder

import static fintech.DateUtils.dateTime

class TaskQueueIssuesTest extends BaseSpecification {

    def setup() {
        taskRegistry.addDefinition { new TaskDefinitionBuilder("A").group("Test").priority(0).resolution("OK").add().defaultExpireResolution("OK").build() }

        agentService.addAgent(new AddAgentCommand(email: "john@mail.com", taskTypes: ["*"]))
        agentService.addAgent(new AddAgentCommand(email: "grant@mail.com", taskTypes: ["*"]))
    }

    def "Bug fix - agents were able to 'take over' other agent task"() {
        given:
        def taskId = taskService.addTask(new AddTaskCommand(clientId: 1, type: "A", dueAt: dateTime("2016-01-01 10:00:00"), expiresAt: dateTime("2016-02-01 00:00:00")))

        when:
        taskQueue.assignNextTask("john@mail.com", dateTime("2016-01-01 11:00:00"))

        and: "After few hours assign again"
        taskQueue.assignNextTask("john@mail.com", dateTime("2016-01-02 15:00:00"))

        then:
        taskService.get(taskId).agent == "john@mail.com"

        when: "Another agent tries to take same task in few minutes"
        taskQueue.assignNextTask("grant@mail.com", dateTime("2016-01-02 15:01:00"))

        then: "Task shouldn't be reassigned"
        taskService.get(taskId).agent == "john@mail.com"

        when: "Another agent tries to take same task after an hour"
        taskQueue.assignNextTask("grant@mail.com", dateTime("2016-01-03 16:01:00"))

        then: "Task should be reassigned now"
        taskService.get(taskId).agent == "grant@mail.com"
    }
}

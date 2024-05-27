package fintech.task

import fintech.task.command.AddAgentCommand
import fintech.task.command.DisableAgentCommand

class AgentTest extends BaseSpecification {

    def "Add and disable agent"() {
        when:
        agentService.addAgent(new AddAgentCommand(email: " John@mail.com", taskTypes: ["DocumentCheck", "DocumentCall"]))

        then:
        with(agentService.findByEmail("john@mail.com").get()) {
            email == "john@mail.com"
            taskTypes == ["DocumentCheck", "DocumentCall"]
            assert !disabled
        }

        and:
        !agentService.findByEmail("another.john@mail.com").isPresent()

        when:
        agentService.disableAgent(new DisableAgentCommand(email: "john@mail.com"))

        then:
        agentService.findByEmail("john@mail.com").get().disabled

        when: "Add again"
        agentService.addAgent(new AddAgentCommand(email: " John@mail.com", taskTypes: ["DocumentCheck"]))

        then: "Existing agent is enabled"
        with(agentService.findByEmail("john@mail.com").get()) {
            taskTypes == ["DocumentCheck"]
            assert !disabled
        }
    }

}

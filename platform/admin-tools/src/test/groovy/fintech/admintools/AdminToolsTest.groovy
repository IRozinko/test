package fintech.admintools

import spock.util.concurrent.PollingConditions

class AdminToolsTest extends BaseSpecification {

    def conditions = new PollingConditions(timeout: 5)

    def "ok action"() {
        when:
        def id = service.execute(new ExecuteAdminActionCommand(name: "OkAction", params: "test"))

        then:
        conditions.eventually {
            with(logRepository.getRequired(id)) {
                status == AdminActionStatus.COMPLETED
                params == "test"
                message == "OK"
            }
        }
    }

    def "failing action"() {
        when:
        def id = service.execute(new ExecuteAdminActionCommand(name: "FailingAction"))

        then:
        conditions.eventually {
            with(logRepository.getRequired(id)) {
                status == AdminActionStatus.FAILED
                params == null
                error == "I'm failing"
            }
        }
    }
}

package fintech.activity

import fintech.activity.commands.AddActivityCommand

class ActivityTest extends BaseSpecification {

    def "add activity"() {
        expect:
        repository.count() == 0

        when:
        service.addActivity(new AddActivityCommand(clientId: 1L, action: "OutgoingCall", resolution: "NoAnswer", comments: "Beep beep"))

        then:
        repository.count() == 1
        with(repository.findAll()[0]) {
            clientId == 1L
            action == "OutgoingCall"
            resolution == "NoAnswer"
            comments == "Beep beep"
        }
    }

    def "add activity with build actions"() {
        expect:
        NoopActionHandler.executed == 0

        when:
        service.addActivity(new AddActivityCommand(
            clientId: 1L,
            action: "OutgoingCall",
            bulkActions: [new AddActivityCommand.BulkAction(type: "Noop", params: ["test": "test"])]
        ))

        then:
        NoopActionHandler.executed == 1
    }
}

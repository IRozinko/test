package fintech.instantor.model

import fintech.IbanUtils
import spock.lang.Specification

class InstantorResponseTest extends Specification {

    def "get accounts"() {
        when:
        def response = new InstantorResponse(
            accounts: []
        )

        then:
        response.getAccountsWithTransactions().isEmpty()
        response.getValidAccounts().isEmpty()

        when:
        response.accounts = []

        then:
        response.getAccountsWithTransactions().isEmpty()
        response.getValidAccounts().isEmpty()

        when:
        response.accounts = [new InstantorAccount(iban: "ES9029326382468617504398")]

        then:
        response.getAccountsWithTransactions().isEmpty()
        !response.getValidAccounts().isEmpty()

        when:
        response.accounts = [new InstantorAccount(iban: " "), new InstantorAccount(iban: "ES9029326382468617504398"), new InstantorAccount(iban: "ES490940662722509771 2024", transactionCount: 1, transactionList: [new InstantorTransaction()])]

        then:
        response.getAccountsWithTransactions().size() == 1
        response.getAccountsWithTransactions()[0].iban == "ES490940662722509771 2024"
        response.getValidAccounts().size() == 1
        response.getValidAccounts()[0].iban == "ES490940662722509771 2024"
        response.getAccounts().stream().anyMatch { a -> IbanUtils.equals(a.iban, "ES4909406627225097712024") }
        !response.getAccounts().stream().anyMatch { a -> IbanUtils.equals(a.iban, "ES4909406627225097712025") }
    }
}

package fintech.payments

import fintech.payments.commands.AddInstitutionCommand
import fintech.payments.commands.UpdateInstitutionCommand

class InstitutionTest extends BaseSpecification {

    def "Add institution"() {
        when:
        def id = institutionService.addInstitution(new fintech.payments.commands.AddInstitutionCommand(code: "Barclays", name: "Barclays Bank", institutionType: "bank", primary: true,
                accounts: [
                        new fintech.payments.commands.AddInstitutionCommand.Account(accountNumber: "123-new-primary", accountingAccountCode: "2601", primary: true),
                        new fintech.payments.commands.AddInstitutionCommand.Account(accountNumber: "456", accountingAccountCode: "2601")
                ]))

        then:
        def institution = institutionService.getInstitution(id)
        institution.institutionType == "bank"
        institution.name == "Barclays Bank"
        institution.accounts.size() == 2
        with (institution.accounts.find { it.accountNumber == "123-new-primary" }) {
            accountNumber == "123-new-primary"
            accountingAccountCode == "2601"
        }

        and:
        institutionCount() == old(institutionCount()) + 1

        expect:
        institutionService.getPrimaryInstitution().getPrimaryAccount().accountNumber == "123-new-primary"
    }


    def "Find account by number"() {
        when:
        def accountFound = institutionService.findAccountByNumber("456")
        def accountNotFound = institutionService.findAccountByNumber("NOT-EXISTING")

        then:
        assert accountFound.isPresent()
        assert accountFound.get().accountingAccountCode == "2600"
        assert !accountNotFound.isPresent()
    }

    def "update institution"() {
        given:
        def id2 = institutionService.addInstitution(new AddInstitutionCommand(code: "JP morgan", name: "JP morgan bank", institutionType: "bank", primary: false,
            accounts: [
                new AddInstitutionCommand.Account(accountNumber: "1234-new-primary", accountingAccountCode: "1601", primary: true),
                new AddInstitutionCommand.Account(accountNumber: "2222", accountingAccountCode: "2611")
            ]))

        when:
        def institution = institutionService.getInstitution(id2)
        def updateCommand = new UpdateInstitutionCommand(
            institutionId: institution.id,
            name: 'new bank',
            primary: true,
            disabled: false,
            statementExportFormat: "exp",
            statementImportFormat: "imp",
            statementExportParamsJson: "{}"
        )
        institutionService.updateInstitution(updateCommand)

        then:
        with (institutionService.getInstitution(institution.getId())) {
            assert name == updateCommand.name
            assert primary == updateCommand.primary
            assert disabled == updateCommand.disabled
            assert statementImportFormat == updateCommand.statementImportFormat
            assert statementExportFormat == updateCommand.statementExportFormat
            assert statementExportParamsJson == updateCommand.statementExportParamsJson
        }

        when:
        updateCommand.disabled = true
        institutionService.updateInstitution(updateCommand)

        then:
        def ex = thrown(IllegalArgumentException)

        when:
        updateCommand.disabled = false
        updateCommand.primary = false
        institutionService.updateInstitution(updateCommand)

        then:
        ex = thrown(IllegalArgumentException)
    }

    private int institutionCount() {
        institutionService.getAllInstitutions().size()
    }
}

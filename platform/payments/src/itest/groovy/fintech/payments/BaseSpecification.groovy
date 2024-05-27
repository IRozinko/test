package fintech.payments

import fintech.payments.commands.AddInstitutionCommand
import fintech.payments.impl.UnnaxDisbursementProcessorBean
import fintech.payments.model.Institution
import fintech.payments.settigs.PaymentsSettingsService
import fintech.payments.spi.DisbursementProcessorRegistry
import fintech.payments.spi.StatementProcessorRegistry
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    InstitutionService institutionService

    @Autowired
    PaymentService paymentService

    @Autowired
    StatementProcessorRegistry statementParserRegistry

    @Autowired
    DisbursementProcessorRegistry disbursementProcessorRegistry

    @Autowired
    PaymentsSettingsService paymentsSettingsService
    
    Institution institution

    Institution unnaxIxnstitution

    def setup() {
        testDatabase.cleanDb()
        setupInstitutions()
        setupStatementParsers()
        paymentsSettingsService.setup()
    }

    def setupInstitutions() {
        def institutionId = institutionService.addInstitution(new AddInstitutionCommand(
            name: "Bank X",
            institutionType: "bank",
            primary: true,
            statementImportFormat: MockStatementParser.IMPORT_FORMAT_NAME,
            statementExportFormat: MockDisbursementFileExporter.NAME,
            accounts: [
                    new AddInstitutionCommand.Account(accountNumber: "123-primary", accountingAccountCode: "2600", primary: true),
                    new AddInstitutionCommand.Account(accountNumber: "456", accountingAccountCode: "2600")
            ]))
        institution = institutionService.getInstitution(institutionId)

        def unnaxInstitutionId = institutionService.addInstitution(new AddInstitutionCommand(
            name: "Unnax",
            institutionType: "Unnax",
            primary: true,
            statementApiExporter: UnnaxDisbursementProcessorBean.UNNAX_EXPORTER,
            accounts: [
                new AddInstitutionCommand.Account(accountNumber: "Unnax", accountingAccountCode: "123123123", primary: true),
            ]))
        unnaxIxnstitution = institutionService.getInstitution(unnaxInstitutionId)
    }


    def setupStatementParsers() {
        statementParserRegistry.add(MockStatementParser.IMPORT_FORMAT_NAME, MockStatementParser.class)
    }

}

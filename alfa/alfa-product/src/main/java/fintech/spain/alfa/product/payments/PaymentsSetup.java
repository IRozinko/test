package fintech.spain.alfa.product.payments;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fintech.JsonUtils;
import fintech.payments.InstitutionService;
import fintech.payments.commands.AddInstitutionCommand;
import fintech.payments.commands.UpdateInstitutionCommand;
import fintech.payments.model.Institution;
import fintech.payments.spi.PaymentAutoProcessorRegistry;
import fintech.payments.spi.StatementProcessorRegistry;
import fintech.spain.payments.exporters.SepaExporter;
import fintech.spain.payments.exporters.SepaExporterParams;
import fintech.spain.payments.statements.*;
import fintech.spain.alfa.product.accounting.Accounts;
import fintech.spain.alfa.product.payments.processors.DisbursementProcessor;
import fintech.spain.alfa.product.payments.processors.InterCompanyProcessor;
import fintech.spain.alfa.product.payments.processors.LoanExtensionProcessor;
import fintech.spain.alfa.product.payments.processors.LoanRepaymentProcessor;
import fintech.spain.alfa.product.payments.processors.SuggestedTxTypeProcessor;
import fintech.spain.alfa.product.payments.statements.AlfaPaytpvStatementParser;
import fintech.spain.alfa.product.payments.statements.AlfaUnnaxPayInStatementParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.payments.impl.UnnaxDisbursementProcessorBean.UNNAX_EXPORTER;

@Component
public class PaymentsSetup {

    public static final String TYPE_BANK = "Bank";
    public static final String TYPE_VIRTUAL = "Virtual";

    public static final String INSTITUTION_ING = "ING";
    public static final String BANK_ACCOUNT_ING = "ES7814650120391900214751";

    public static final String INSTITUTION_BANKIA = "Bankia";
    public static final String BANK_ACCOUNT_BANKIA = "ES9820389261996000253613";

    public static final String INSTITUTION_SABADELL = "Sabadell";
    public static final String BANK_ACCOUNT_SABADELL = "ES2500815029140002457055";

    public static final String INSTITUTION_BBVA = "BBVA";
    public static final String BANK_ACCOUNT_BBVA = "ES3501821797310203745924";

    public static final String INSTITUTION_PAY_TPV = "Pay Tpv";
    public static final String BANK_ACCOUNT_PAY_TPV = "PAY_TPV";

    public static final String INSTITUTION_CAIXA = "Caixa";
    public static final String BANK_ACCOUNT_CAIXA = "ES6821000844240200657804";

    public static final String INSTITUTION_UNNAX = "Unnax";
    public static final String BANK_ACCOUNT_UNNAX = "UNNAX";

    public static final String INSTITUTION_BJS = "BJS";
    public static final String BANK_ACCOUNT_BJS = "BJS";

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private StatementProcessorRegistry statementProcessorRegistry;

    @Autowired
    private PaymentAutoProcessorRegistry autoProcessorRegistry;

    @Autowired
    private DisbursementProcessor disbursementProcessor;

    @Autowired
    private InterCompanyProcessor interCompanyProcessor;

    @Autowired
    private LoanRepaymentProcessor loanRepaymentProcessor;

    @Autowired
    private LoanExtensionProcessor loanExtensionProcessor;

    @Autowired
    private SuggestedTxTypeProcessor suggestedTxTypeProcessor;

    public void setUp() {
        autoProcessors();
        statementParsers();

//        ing();
//        payTpv();
//        bbva();
//        bankia();
//        sabadell();
//        caixa();
//        unnax();
        bjs();
    }

    private void autoProcessors() {
        autoProcessorRegistry.addProcessor(suggestedTxTypeProcessor);
        autoProcessorRegistry.addProcessor(interCompanyProcessor);
        autoProcessorRegistry.addProcessor(disbursementProcessor);
        autoProcessorRegistry.addProcessor(loanExtensionProcessor);
        autoProcessorRegistry.addProcessor(loanRepaymentProcessor);
    }

    private void statementParsers() {
        statementProcessorRegistry.add(NoopStatementParser.FORMAT_NAME, NoopStatementParser.class);
        statementProcessorRegistry.add(BbvaStatementParser.FORMAT_NAME, BbvaStatementParser.class);
        statementProcessorRegistry.add(BankiaStatementParser.FORMAT_NAME, BankiaStatementParser.class);
        statementProcessorRegistry.add(IngStatementParser.FORMAT_NAME, IngStatementParser.class);
        statementProcessorRegistry.add(SabadellStatementParser.FORMAT_NAME, SabadellStatementParser.class);
        statementProcessorRegistry.add(CaixaStatementParser.FORMAT_NAME, CaixaStatementParser.class);
        statementProcessorRegistry.add(PayTpvCsvStatementParser.FORMAT_NAME, AlfaPaytpvStatementParser.class);
        statementProcessorRegistry.add(UnnaxPayInStatementParser.FORMAT_NAME, AlfaUnnaxPayInStatementParser.class);
        statementProcessorRegistry.add(BjsStatementParser.FORMAT_NAME, BjsStatementParser.class);
    }

    private void payTpv() {
        AddInstitutionCommand.Account account = new AddInstitutionCommand.Account();
        account.setAccountNumber(BANK_ACCOUNT_PAY_TPV);
        account.setAccountingAccountCode(Accounts.BANK_PAY_TPV);
        account.setPrimary(true);

        AddInstitutionCommand command = new AddInstitutionCommand();
        command.setName("PayTpv gateway");
        command.setCode(INSTITUTION_PAY_TPV);
        command.setInstitutionType(TYPE_VIRTUAL);
        command.setStatementImportFormat(PayTpvCsvStatementParser.FORMAT_NAME);
        command.setAccounts(ImmutableList.of(account));
        command.setPaymentMethods(Lists.newArrayList());
        command.setPrimary(false);
        addInstitution(command);
    }

    private void ing() {
        SepaExporterParams exporterParams = new SepaExporterParams();
        exporterParams.setAccountOwnerName("ALFA");
        exporterParams.setAccountOwnerBic("INGDESMM");
        exporterParams.setAccountOwnerOrgId("B98378201000");

        AddInstitutionCommand.Account account = new AddInstitutionCommand.Account();
        account.setAccountNumber(BANK_ACCOUNT_ING);
        account.setAccountingAccountCode(Accounts.BANK_ING);
        account.setPrimary(true);

        AddInstitutionCommand command = new AddInstitutionCommand();
        command.setName("ING bank");
        command.setCode(INSTITUTION_ING);
        command.setInstitutionType(TYPE_BANK);
        command.setAccounts(ImmutableList.of(account));
        command.setPaymentMethods(Lists.newArrayList());
        command.setPrimary(true);
        command.setStatementImportFormat(IngStatementParser.FORMAT_NAME);
        command.setStatementExportFormat(SepaExporter.EXPORTER_NAME);
        command.setStatementExportParamsJson(JsonUtils.writeValueAsString(exporterParams));
        addInstitution(command);
    }

    private void bbva() {
        SepaExporterParams exporterParams = new SepaExporterParams();
        exporterParams.setAccountOwnerName("VIA SMS MINICREDIT SL");
        exporterParams.setAccountOwnerBic("BBVAESMM");
        exporterParams.setAccountOwnerOrgId("B98378201000");

        AddInstitutionCommand.Account account = new AddInstitutionCommand.Account();
        account.setAccountNumber(BANK_ACCOUNT_BBVA);
        account.setAccountingAccountCode(Accounts.BANK_BBVA);
        account.setPrimary(true);

        AddInstitutionCommand command = new AddInstitutionCommand();
        command.setName("BBVA bank");
        command.setCode(INSTITUTION_BBVA);
        command.setInstitutionType(TYPE_BANK);
        command.setAccounts(ImmutableList.of(account));
        command.setPaymentMethods(Lists.newArrayList());
        command.setPrimary(false);
        command.setStatementImportFormat(BbvaStatementParser.FORMAT_NAME);
        command.setStatementExportFormat(SepaExporter.EXPORTER_NAME);
        command.setStatementExportParamsJson(JsonUtils.writeValueAsString(exporterParams));
        addInstitution(command);
    }

    private void sabadell() {
        SepaExporterParams exporterParams = new SepaExporterParams();
        exporterParams.setAccountOwnerName("ALFA");
        exporterParams.setAccountOwnerBic("BSABESBB");
        exporterParams.setAccountOwnerOrgId("B98378201");

        AddInstitutionCommand.Account account = new AddInstitutionCommand.Account();
        account.setAccountNumber(BANK_ACCOUNT_SABADELL);
        account.setAccountingAccountCode(Accounts.BANK_SABADELL);
        account.setPrimary(true);

        AddInstitutionCommand command = new AddInstitutionCommand();
        command.setName("Sabadell bank");
        command.setCode(INSTITUTION_SABADELL);
        command.setInstitutionType(TYPE_BANK);
        command.setAccounts(ImmutableList.of(account));
        command.setPaymentMethods(Lists.newArrayList());
        command.setPrimary(false);
        command.setStatementImportFormat(SabadellStatementParser.FORMAT_NAME);
        command.setStatementExportFormat(SepaExporter.EXPORTER_NAME);
        command.setStatementExportParamsJson(JsonUtils.writeValueAsString(exporterParams));
        addInstitution(command);
    }

    private void bankia() {
        SepaExporterParams exporterParams = new SepaExporterParams();
        exporterParams.setAccountOwnerName("Alfa");
        exporterParams.setAccountOwnerBic("CAHMESMM");
        exporterParams.setAccountOwnerOrgId("B98378201");

        AddInstitutionCommand.Account account = new AddInstitutionCommand.Account();
        account.setAccountNumber(BANK_ACCOUNT_BANKIA);
        account.setAccountingAccountCode(Accounts.BANK_BANKIA);
        account.setPrimary(true);

        AddInstitutionCommand command = new AddInstitutionCommand();
        command.setName("Bankia bank");
        command.setCode(INSTITUTION_BANKIA);
        command.setInstitutionType(TYPE_BANK);
        command.setAccounts(ImmutableList.of(account));
        command.setPaymentMethods(Lists.newArrayList());
        command.setPrimary(false);
        command.setStatementImportFormat(BankiaStatementParser.FORMAT_NAME);
        command.setStatementExportFormat(SepaExporter.EXPORTER_NAME);
        command.setStatementExportParamsJson(JsonUtils.writeValueAsString(exporterParams));
        addInstitution(command);
    }

    private void caixa() {
        SepaExporterParams exporterParams = new SepaExporterParams();
        exporterParams.setAccountOwnerName("ALFA");
        exporterParams.setAccountOwnerBic("CAIXESBBXXX");
        exporterParams.setAccountOwnerOrgId("B98378201");

        AddInstitutionCommand.Account account = new AddInstitutionCommand.Account();
        account.setAccountNumber(BANK_ACCOUNT_CAIXA);
        account.setAccountingAccountCode(Accounts.BANK_CAIXA);
        account.setPrimary(true);

        AddInstitutionCommand command = new AddInstitutionCommand();
        command.setName("Caixa bank");
        command.setCode(INSTITUTION_CAIXA);
        command.setInstitutionType(TYPE_BANK);
        command.setAccounts(ImmutableList.of(account));
        command.setPaymentMethods(Lists.newArrayList());
        command.setPrimary(false);
        command.setDisabled(false);
        command.setStatementImportFormat(CaixaStatementParser.FORMAT_NAME);
        command.setStatementExportFormat(SepaExporter.EXPORTER_NAME);
        command.setStatementExportParamsJson(JsonUtils.writeValueAsString(exporterParams));
        addInstitution(command);
    }

    private void unnax() {
        AddInstitutionCommand.Account account = new AddInstitutionCommand.Account();
        account.setAccountNumber(BANK_ACCOUNT_UNNAX);
        account.setAccountingAccountCode(Accounts.UNNAX);
        account.setPrimary(true);

        AddInstitutionCommand command = new AddInstitutionCommand();
        command.setName("Unnax");
        command.setCode(INSTITUTION_UNNAX);
        command.setInstitutionType(TYPE_VIRTUAL);
        command.setAccounts(ImmutableList.of(account));
        command.setPaymentMethods(Lists.newArrayList());
        command.setPrimary(false);
        command.setDisabled(false);
        command.setStatementApiExporter(UNNAX_EXPORTER);
        command.setStatementImportFormat(UnnaxPayInStatementParser.FORMAT_NAME);
        addInstitution(command);
    }

    private void bjs() {
        AddInstitutionCommand.Account account = new AddInstitutionCommand.Account();
        account.setAccountNumber(BANK_ACCOUNT_BJS);
        account.setAccountingAccountCode(Accounts.BJS);
        account.setPrimary(true);

        AddInstitutionCommand command = new AddInstitutionCommand();
        command.setName("BJS");
        command.setCode(INSTITUTION_BJS);
        command.setInstitutionType(TYPE_BANK);
        command.setAccounts(ImmutableList.of(account));
        command.setPaymentMethods(Lists.newArrayList());
        command.setPrimary(true);
        command.setDisabled(false);
        command.setStatementImportFormat(BjsStatementParser.FORMAT_NAME);
        addInstitution(command);
    }

    private long addInstitution(AddInstitutionCommand command) {
        Institution existing = institutionService.getInstitution(command.getCode());
        if (existing == null) {
            return institutionService.addInstitution(command);
        } else {
            UpdateInstitutionCommand updateCommand = new UpdateInstitutionCommand();
            updateCommand.setInstitutionId(existing.getId());
            updateCommand.setName(command.getName());
            updateCommand.setPrimary(command.isPrimary());
            updateCommand.setDisabled(command.isDisabled());
            updateCommand.setStatementImportFormat(command.getStatementImportFormat());
            updateCommand.setStatementExportFormat(command.getStatementExportFormat());
            updateCommand.setStatementExportParamsJson(command.getStatementExportParamsJson());
            institutionService.updateInstitution(updateCommand);
            return existing.getId();
        }
    }
}

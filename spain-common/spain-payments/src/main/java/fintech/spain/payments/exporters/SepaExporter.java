package fintech.spain.payments.exporters;

import com.google.common.collect.ImmutableList;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.DelegatingLoader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.crm.bankaccount.ClientBankAccount;
import fintech.crm.bankaccount.ClientBankAccountService;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.payments.model.Disbursement;
import fintech.payments.model.DisbursementExportParams;
import fintech.payments.model.Institution;
import fintech.payments.model.InstitutionAccount;
import fintech.payments.spi.DisbursementFileExporter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class SepaExporter implements DisbursementFileExporter {

    public static final String EXPORTER_NAME = "SEPA";

    private static final int MAX_CREDITOR_NAME_LENGTH = 70;

    private static final PebbleEngine engine = newEngine();

    private static PebbleEngine newEngine() {
        return new PebbleEngine.Builder().autoEscaping(false).loader(new DelegatingLoader(ImmutableList.of(new ClasspathLoader(), new StringLoader()))).build();
    }

    @Autowired
    private LoanService loanService;

    @Autowired
    private ClientBankAccountService clientBankAccountService;

    @Autowired
    private ClientService clientService;

    @Override
    public String exporterName() {
        return EXPORTER_NAME;
    }

    @SneakyThrows
    @Override
    public ExportedFileInfo exportDisbursements(DisbursementExportParams params, OutputStream outputStream) {
        Map<String, Object> context = new HashMap<>();
        SepaModel model = toModel(params);
        context.put("model", model);
        String content = render("sepa/sepa-template.xml", context);
        IOUtils.write(content, outputStream, StandardCharsets.UTF_8);
        return new ExportedFileInfo(model.getFileName(), "text/xml");
    }

    public SepaModel toModel(DisbursementExportParams params) {
        Institution institution = params.getInstitution();
        InstitutionAccount account = params.getInstitutionAccount();
        Validate.notBlank(institution.getStatementExportParamsJson(), "Exporter params JSON is empty");
        SepaExporterParams exporterParams = JsonUtils.readValue(institution.getStatementExportParamsJson(), SepaExporterParams.class);
        LocalDateTime exportDateTime = TimeMachine.now();
        String msgId = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(exportDateTime) + "_" + RandomStringUtils.randomAlphanumeric(3).toUpperCase();

        SepaModel model = new SepaModel();
        model.setFileName(exporterParams.getAccountOwnerBic() + "_" + msgId + ".xml");
        model.setAccountOwnerBic(exporterParams.getAccountOwnerBic());
        model.setAccountOwnerName(exporterParams.getAccountOwnerName());
        model.setAccountOwnerOrgId(exporterParams.getAccountOwnerOrgId());
        model.setAccountOwnerIban(account.getAccountNumber());
        model.setNumberOfTransactions(params.getDisbursements().size());
        model.setMsgId(msgId);
        model.setExportDateTime(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(exportDateTime));
        model.setExecutionDate(DateTimeFormatter.ISO_DATE.format(exportDateTime));

        for (Disbursement disbursement : params.getDisbursements()) {
            Loan loan = loanService.getLoan(disbursement.getLoanId());
            ClientBankAccount clientBankAccount = clientBankAccountService.findPrimaryByClientId(loan.getClientId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Client with id [%s] has no primary bank account", loan.getClientId())));
            Client client = clientService.get(loan.getClientId());
            String creditorName = StringUtils.left(client.getFirstAndLastName(), MAX_CREDITOR_NAME_LENGTH);
            SepaModel.Payment payment = new SepaModel.Payment();
            payment.setId(disbursement.getReference());
            payment.setAmount(disbursement.getAmount());
            payment.setEndToEndId(disbursement.getReference());
            payment.setReference("LOAN " + loan.getNumber());
            payment.setCreditorIban(clientBankAccount.getAccountNumber());
            payment.setCreditorName(creditorName);
            model.getPayments().add(payment);
        }
        return model;
    }

    @SneakyThrows
    public static String render(String template, Map<String, Object> context) {
        StringWriter sw = new StringWriter();
        engine.getTemplate(template).evaluate(sw, context);
        String output = sw.toString();
        return output;
    }
}

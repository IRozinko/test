package fintech.spain.alfa.product.workflow.dormants.handler;

import fintech.JsonUtils;
import fintech.retrofit.RetrofitHelper;
import fintech.TimeMachine;
import fintech.cms.Pdf;
import fintech.crm.address.ClientAddress;
import fintech.crm.address.ClientAddressService;
import fintech.crm.attachments.AddAttachmentCommand;
import fintech.crm.attachments.AttachmentStatus;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.commands.LoanApplicationOfferCommand;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.presto.api.LineOfCreditCrossApiClient;
import fintech.spain.alfa.product.presto.api.MockLineOfCreditCrossApiClient;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.spain.alfa.product.utils.SpainAddressUtils;
import fintech.web.api.models.AmortizationPreviewResponse;
import fintech.web.api.models.ContractAgreementRequest;
import fintech.web.api.models.WithdrawalRequest;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import retrofit2.Call;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static fintech.crm.attachments.AttachmentConstants.ATTACHMENT_TYPE_LOAN_AGREEMENT;
import static fintech.crm.attachments.AttachmentConstants.ATTACHMENT_TYPE_STANDARD_INFORMATION;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class LocLoanOfferDataPreparationHandler implements ActivityHandler {

    @Resource(name = "${loc.cross.api:" + MockLineOfCreditCrossApiClient.NAME + "}")
    private LineOfCreditCrossApiClient lineOfCreditCrossApiClient;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ClientAttachmentService attachmentService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientAddressService clientAddressService;

    @Override
    public ActivityResult handle(ActivityContext context) {
        try {
            doHandle(context);
            return ActivityResult.resolution(Resolutions.OK, "");
        } catch (IOException e) {
            log.error("", e);
            return ActivityResult.fail(ExceptionUtils.getMessage(e));
        }
    }

    private void doHandle(ActivityContext context) throws IOException {
        LoanApplication application = loanApplicationService.get(context.getWorkflow().getApplicationId());
        generatePrestoOffer(context, application);
        generateAgreementDocument(context, application);
        generateStandardInfoDocument(context, application);
    }

    private void generatePrestoOffer(ActivityContext context, LoanApplication loanApplication) {
        Optional<AmortizationPreviewResponse> responseOptional = RetrofitHelper.syncCall(lineOfCreditCrossApiClient.amortizationPreview(new WithdrawalRequest(loanApplication.getCreditLimit())));

        if(!responseOptional.isPresent()) {
            throw new RuntimeException("Can't obtain response from Presto");
        }
        AmortizationPreviewResponse response = responseOptional.get();

        LoanApplicationOfferCommand command = new LoanApplicationOfferCommand();
        command.setId(loanApplication.getId());
        command.setPrincipal(loanApplication.getRequestedPrincipal());
        command.setOfferDate(TimeMachine.today());

        command.setNominalApr(response.getNominalApr());
        command.setEffectiveApr(response.getEffectiveApr());

        command.setPeriodCount(loanApplication.getRequestedPeriodCount());
        command.setPeriodUnit(loanApplication.getRequestedPeriodUnit());
        loanApplicationService.updateOffer(command);

        context.setAttribute(Attributes.LOC_OFFER, JsonUtils.writeValueAsString(response));
    }

    private void generateAgreementDocument(ActivityContext context, LoanApplication loanApplication) throws IOException {
        Call<ResponseBody> bodyCall = lineOfCreditCrossApiClient.generateAgreementDocument(agreementRequest(loanApplication));
        context.setAttribute(Attributes.AGREEMENT_ATTACHMENT_ID, generateDocument(bodyCall, loanApplication, AttachmentStatus.WAITING_APPROVAL, ATTACHMENT_TYPE_LOAN_AGREEMENT).toString());
    }

    private ContractAgreementRequest agreementRequest(LoanApplication loanApplication) {
        Client client = clientService.get(loanApplication.getClientId());
        Optional<ClientAddress> clientAddress = clientAddressService.getClientPrimaryAddress(client.getId(), "ACTUAL");

        ContractAgreementRequest.Client clientRequest = new ContractAgreementRequest.Client()
            .setFirstName(client.getFirstName())
            .setLastName(client.getLastName())
            .setNumber(client.getNumber())
            .setDocumentNumber(client.getDocumentNumber())
            .setEmail(client.getEmail())
            .setPhoneNumber(client.getPhone())
            .setIban(client.getAccountNumber());
        clientAddress.ifPresent(e -> {
            clientRequest.setAddressLine1(SpainAddressUtils.addressLine1(e));
            clientRequest.setAddressLine2(SpainAddressUtils.addressLine2(e));
        });

        ContractAgreementRequest.Application applicationRequest = new ContractAgreementRequest.Application()
            .setDate(loanApplication.getSubmittedAt().toLocalDate())
            .setNumber(loanApplication.getNumber())
            .setOfferedPrincipal(loanApplication.getCreditLimit());
        return new ContractAgreementRequest().setClient(clientRequest).setApplication(applicationRequest);
    }

    private void generateStandardInfoDocument(ActivityContext context, LoanApplication loanApplication) throws IOException {
        Call<ResponseBody> bodyCall = lineOfCreditCrossApiClient.generateStandardInfoDocument();
        context.setAttribute(Attributes.STANDARD_INFORMATON_ATTACHMENT_ID, generateDocument(bodyCall, loanApplication, AttachmentStatus.OK, ATTACHMENT_TYPE_STANDARD_INFORMATION).toString());
    }

    private Long generateDocument(Call<ResponseBody> callResponse, LoanApplication loanApplication, String saveStatus, String saveType) throws IOException {
        Optional<Pair<Headers, ResponseBody>> response = RetrofitHelper.syncCallWithHeaders(callResponse);
        if (response.isPresent()) {
            Pair<Headers, ResponseBody> content = response.get();
            String fileName = getFileName(content.getLeft());
            byte[] fileContent = content.getRight().bytes();
            CloudFile cloudFile = saveAgreementContent(new Pdf(fileName, fileContent));
            return saveClientAttachment(loanApplication, cloudFile, saveStatus, saveType);
        } else {
            throw new IOException("No generation file response received!");
        }
    }

    private String getFileName(Headers headers) {
        String fileHeader = headers.get("Content-Disposition");
        if (fileHeader != null) {
            return fileHeader.replaceAll("attachment; filename=", StringUtils.EMPTY);
        } else {
            return UUID.randomUUID().toString();
        }
    }

    private CloudFile saveAgreementContent(Pdf pdf) {
        SaveFileCommand savePdf = new SaveFileCommand();
        savePdf.setContentType(SaveFileCommand.CONTENT_TYPE_PDF);
        savePdf.setDirectory(AlfaConstants.FILE_DIRECTORY_AGREEMENTS);
        savePdf.setInputStream(new ByteArrayInputStream(pdf.getContent()));
        savePdf.setOriginalFileName(pdf.getName());
        return fileStorageService.save(savePdf);
    }

    private Long saveClientAttachment(LoanApplication application, CloudFile pdfFile, String status, String type) {
        AddAttachmentCommand addAttachment = new AddAttachmentCommand();
        addAttachment.setLoanId(application.getLoanId());
        addAttachment.setClientId(application.getClientId());
        addAttachment.setApplicationId(application.getId());
        addAttachment.setFileId(pdfFile.getFileId());
        addAttachment.setStatus(status);
        addAttachment.setAttachmentType(type);
        addAttachment.setName(pdfFile.getOriginalFileName());
        return attachmentService.addAttachment(addAttachment);
    }
}

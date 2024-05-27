package fintech.crm.client.model;

import fintech.IoUtils;
import fintech.crm.address.ClientAddressService;
import fintech.crm.attachments.Attachment;
import fintech.crm.attachments.AttachmentConstants;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.crm.bankaccount.ClientBankAccountService;
import fintech.crm.client.Client;
import fintech.crm.contacts.PhoneContactService;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.spi.FileContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

@Component
@Slf4j
public class DormantsClientConverter implements Converter<Client, DormantsClientData> {

    public static final String ADDRESS_TYPE_ACTUAL = "ACTUAL";

    @Autowired
    private PhoneContactService phoneContactService;

    @Autowired
    private ClientAddressService addressService;

    @Autowired
    private ClientBankAccountService bankAccountService;

    @Autowired
    private ClientAttachmentService clientAttachmentService;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public DormantsClientData convert(Client source) {
        DormantsClientData data = new DormantsClientData();
        data.setId(source.getId());
        data.setNumber(source.getNumber());
        data.setEmail(source.getEmail());

        data.setFirstName(source.getFirstName());
        data.setSecondFirstName(source.getSecondFirstName());

        data.setLastName(source.getLastName());
        data.setSecondLastName(source.getSecondLastName());
        data.setMaidenName(source.getMaidenName());

        data.setDocumentNumber(source.getDocumentNumber());

        bankAccountService.findPrimaryByClientId(source.getId()).ifPresent(data::setBankAccount);

        data.setMobilePhone(source.getPhone());
        phoneContactService.findActualAdditionalPhone(source.getId())
            .ifPresent(phone -> data.setOtherPhone(phone.getPhoneNumber()));

        Optional.ofNullable(source.getGender()).ifPresent(
            gender -> data.setGender(gender.toString())
        );

        data.setDateOfBirth(source.getDateOfBirth());
        data.setAttributes(source.getAttributes());

        data.setAcceptTerms(source.isAcceptTerms());
        data.setAcceptMarketing(source.isAcceptMarketing());
        data.setAcceptVerification(source.isAcceptVerification());
        data.setAcceptPrivacyPolicy(source.isAcceptPrivacyPolicy());

        addressService.getClientPrimaryAddress(source.getId(), ADDRESS_TYPE_ACTUAL)
            .ifPresent(data::setAddress);
        addAttachments(source, data);
        return data;
    }

    private void addAttachments(Client client, DormantsClientData data) {
        Optional<Attachment> agreementFile = clientAttachmentService.findAttachments(ClientAttachmentService.AttachmentQuery.byClient(client.getId(), AttachmentConstants.ATTACHMENT_TYPE_LOAN_AGREEMENT)).stream().findFirst();
        agreementFile.map(this::readFileContent).ifPresent(data::setLoanAgreementAttachment);

        Optional<Attachment> standardInfoFile = clientAttachmentService.findAttachments(ClientAttachmentService.AttachmentQuery.byClient(client.getId(), AttachmentConstants.ATTACHMENT_TYPE_STANDARD_INFORMATION)).stream().findFirst();
        standardInfoFile.map(this::readFileContent).ifPresent(data::setStandardInfoAttachment);
    }

    private FileContent readFileContent(Attachment attachment) {
        Function<InputStream, byte[]> readInputStream = IoUtils::copyToByteArray;
        byte[] content = fileStorageService.readContents(attachment.getFileId(), readInputStream);
        return new FileContent().setName(attachment.getName()).setContent(content);
    }
}

package fintech.spain.alfa.product.crm;

import fintech.crm.attachments.AttachmentConstants;
import fintech.crm.attachments.AttachmentStatus;
import fintech.crm.attachments.spi.AttachmentBuilder;
import fintech.crm.attachments.spi.AttachmentDefinition;
import fintech.crm.attachments.spi.ClientAttachmentRegistry;
import fintech.spain.alfa.product.AlfaConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.crm.attachments.AttachmentConstants.ATTACHMENT_TYPE_BANK_STATEMENT;

@Component
public class CrmSetup {

    @Autowired
    private ClientAttachmentRegistry attachmentRegistry;

    public void setUp() {
        AttachmentDefinition loanAgreement = new AttachmentBuilder(AlfaConstants.ATTACHMENT_GROUP_AGREEMENTS, AttachmentConstants.ATTACHMENT_TYPE_LOAN_AGREEMENT)
            .statuses(AttachmentStatus.WAITING_APPROVAL, AttachmentStatus.APPROVED).build();
        attachmentRegistry.addDefinition(loanAgreement);

        AttachmentDefinition standardInformation = new AttachmentBuilder(AlfaConstants.ATTACHMENT_GROUP_AGREEMENTS, AttachmentConstants.ATTACHMENT_TYPE_STANDARD_INFORMATION)
            .statuses(AttachmentStatus.OK).build();
        attachmentRegistry.addDefinition(standardInformation);

        AttachmentDefinition upsellAgreement = new AttachmentBuilder(AlfaConstants.ATTACHMENT_GROUP_AGREEMENTS, AlfaConstants.ATTACHMENT_TYPE_UPSELL_AGREEMENT)
            .statuses(AttachmentStatus.OK).build();
        attachmentRegistry.addDefinition(upsellAgreement);

        AttachmentDefinition other = new AttachmentBuilder(AlfaConstants.ATTACHMENT_GROUP_UPLOADS, AlfaConstants.ATTACHMENT_TYPE_OTHER).build();
        attachmentRegistry.addDefinition(other);

        AttachmentDefinition payroll = new AttachmentBuilder(AlfaConstants.ATTACHMENT_GROUP_UPLOADS, AlfaConstants.ATTACHMENT_TYPE_BANK_ACC_OWNERSHIP).build();
        attachmentRegistry.addDefinition(payroll);

        AttachmentDefinition idCard = new AttachmentBuilder(AlfaConstants.ATTACHMENT_GROUP_UPLOADS, AlfaConstants.ATTACHMENT_TYPE_ID_DOCUMENT).build();
        attachmentRegistry.addDefinition(idCard);

        AttachmentDefinition clientUpload = new AttachmentBuilder(AlfaConstants.ATTACHMENT_GROUP_UPLOADS, AlfaConstants.ATTACHMENT_TYPE_CLIENT_UPLOAD).build();
        attachmentRegistry.addDefinition(clientUpload);

        AttachmentDefinition invoice = new AttachmentBuilder(AlfaConstants.ATTACHMENT_GROUP_LENDING, AlfaConstants.ATTACHMENT_TYPE_INVOICE).build();
        attachmentRegistry.addDefinition(invoice);

        AttachmentDefinition privacyPolicy = new AttachmentBuilder(AlfaConstants.ATTACHMENT_GROUP_LENDING, AlfaConstants.ATTACHMENT_TYPE_PRIVACY_POLICY).build();
        attachmentRegistry.addDefinition(privacyPolicy);

        AttachmentDefinition reschedulingToc = new AttachmentBuilder(AlfaConstants.ATTACHMENT_GROUP_LENDING, AlfaConstants.ATTACHMENT_TYPE_RESCHEDULING_TOC).build();
        attachmentRegistry.addDefinition(reschedulingToc);

        AttachmentDefinition bankStatements = new AttachmentBuilder(AlfaConstants.ATTACHMENT_GROUP_LENDING, ATTACHMENT_TYPE_BANK_STATEMENT).build();
        attachmentRegistry.addDefinition(bankStatements);
    }
}

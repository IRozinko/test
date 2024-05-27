package fintech.crm.db;

import fintech.crm.address.db.QClientAddressEntity;
import fintech.crm.attachments.db.QClientAttachmentEntity;
import fintech.crm.bankaccount.db.QClientBankAccountEntity;
import fintech.crm.client.db.QClientEntity;
import fintech.crm.contacts.db.QEmailContactEntity;
import fintech.crm.contacts.db.QPhoneContactEntity;
import fintech.crm.contacts.db.QPhoneVerificationEntity;
import fintech.crm.country.db.QCountryEntity;
import fintech.crm.documents.db.QIdentityDocumentEntity;
import fintech.crm.logins.db.QEmailLoginEntity;
import fintech.crm.logins.db.QResetPasswordTokenEntity;
import fintech.crm.logins.db.QVerifyEmailTokenEntity;
import fintech.crm.security.db.QOneTimeTokenEntity;

public class Entities {

    public static final String SCHEMA = "crm";

    public static final QClientEntity client = QClientEntity.clientEntity;

    public static final QEmailContactEntity emailContact = QEmailContactEntity.emailContactEntity;

    public static final QPhoneContactEntity phoneContact = QPhoneContactEntity.phoneContactEntity;

    public static final QPhoneVerificationEntity phoneVerification = QPhoneVerificationEntity.phoneVerificationEntity;

    public static final QEmailLoginEntity emailLogin = QEmailLoginEntity.emailLoginEntity;

    public static final QClientAttachmentEntity clientAttachment = QClientAttachmentEntity.clientAttachmentEntity;

    public static final QClientBankAccountEntity clientBankAccount = QClientBankAccountEntity.clientBankAccountEntity;

    public static final QIdentityDocumentEntity identityDocument = QIdentityDocumentEntity.identityDocumentEntity;

    public static final QResetPasswordTokenEntity resetPasswordToken = QResetPasswordTokenEntity.resetPasswordTokenEntity;

    public static final QVerifyEmailTokenEntity verifyEmailToken = QVerifyEmailTokenEntity.verifyEmailTokenEntity;

    public static final QClientAddressEntity clientAddress = QClientAddressEntity.clientAddressEntity;

    public static final QCountryEntity country = QCountryEntity.countryEntity;

    public static final QOneTimeTokenEntity oneTimeToken = QOneTimeTokenEntity.oneTimeTokenEntity;


}

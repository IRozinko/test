package fintech.spain.crm.client;

public interface ClientFacade {

    void blacklistDocument(Long clientId, String docType, String comment);

    boolean isDocumentBlacklisted(Long clientId, String docType);

    void blacklistEmail(Long clientId, String comment);

    boolean isEmailBlacklisted(Long clientId);

    void blacklistPhone(Long clientId, String comment);

    boolean isPhoneBlacklisted(Long clientId);
}

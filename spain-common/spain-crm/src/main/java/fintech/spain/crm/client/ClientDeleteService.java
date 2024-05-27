package fintech.spain.crm.client;

public interface ClientDeleteService {

    void softDelete(Long clientId);

    void hardDelete(Long clientId);

    void partialDelete(Long clientId);
}

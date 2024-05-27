package fintech.nordigen.impl;

public interface NordigenProvider {

    NordigenResponse request(Long clientId, String requestBody);
}

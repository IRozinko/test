package fintech.bo.spain.alfa.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

@Configuration
public class AlfaApiClients {

    @Autowired
    private Retrofit retrofit;

    @Bean
    AddressApiClient addressApiClient() {
        return retrofit.create(AddressApiClient.class);
    }

    @Bean
    LoanApplicationApiClient loanApplicationApiClient() {
        return retrofit.create(LoanApplicationApiClient.class);
    }

    @Bean
    LoanCertificateApiClient loanCertificateApiClient() {
        return retrofit.create(LoanCertificateApiClient.class);
    }

    @Bean
    AlfaApiClient alfaApiClient() {
        return retrofit.create(AlfaApiClient.class);
    }

    @Bean
    ClientApiClient clientApiClient() {
        return retrofit.create(ClientApiClient.class);
    }

    @Bean
    LocBatchApiClient locBatchApiClient() {
        return retrofit.create(LocBatchApiClient.class);
    }

    @Bean
    DormantsLocFacadeApiClient dormantsLocFacadeApiClient() {
        return retrofit.create(DormantsLocFacadeApiClient.class);
    }

    @Bean
    PhoneContactApiClient phoneContactApiClient() {
        return retrofit.create(PhoneContactApiClient.class);
    }

    @Bean
    MarketingConsentApiClient marketingConsentApiClient() {
        return retrofit.create(MarketingConsentApiClient.class);
    }

    @Bean
    ExtensionDiscountApiClient extensionDiscountApiClient() {
        return retrofit.create(ExtensionDiscountApiClient.class);
    }

}

package fintech.cms;

import java.util.Map;

public interface CmsModels {

    Map<String, Object> applicationContext(Long applicationId);

    Map<String, Object> debtContext(Long debtId);

    Map<String, Object> loanContext(Long loanId);

    Map<String, Object> clientContext(Long clientId);

    Map<String, Object> testClientContext();

    String getWebBaseUrl();

    String getApiBaseUrl();
}

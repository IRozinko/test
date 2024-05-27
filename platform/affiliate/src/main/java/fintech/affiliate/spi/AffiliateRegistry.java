package fintech.affiliate.spi;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AffiliateRegistry {

    private AffiliateReportUrlTemplateContextProvider contextProvider;

    public Optional<AffiliateReportUrlTemplateContextProvider> getContextProvider() {
        return Optional.ofNullable(contextProvider);
    }

    public void setContextProvider(AffiliateReportUrlTemplateContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }
}

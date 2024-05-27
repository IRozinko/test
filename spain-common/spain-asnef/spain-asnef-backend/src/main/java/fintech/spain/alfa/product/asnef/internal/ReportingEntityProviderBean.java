package fintech.spain.alfa.product.asnef.internal;

import fintech.spain.asnef.ReportingEntityProvider;
import org.springframework.stereotype.Component;

@Component
class ReportingEntityProviderBean implements ReportingEntityProvider {

    @Override
    public String getRpNotificaReportingEntity() {
        return "B573";
    }

    @Override
    public String getFotoaltasReportingEntity() {
        return "B573";
    }
}

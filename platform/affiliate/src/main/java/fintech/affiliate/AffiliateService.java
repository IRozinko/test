package fintech.affiliate;

import fintech.affiliate.model.*;

import java.util.Optional;

public interface AffiliateService {

    Long savePartner(SavePartnerCommand command);

    Optional<Long> addLead(AddLeadCommand command);

    Optional<Long> reportEvent(ReportEventCommand command);

    Optional<LeadReport> findLeadReportByClientId(Long clientId);

    Optional<LeadReport> findLeadReportByClientIdAndApplicationId(Long clientId, Long applicationId);

    Optional<AffiliatePartner> findActivePartnerByApiKey(String apiKey);

    Long saveAffiliateRequest(SaveAffiliateRequestCommand command);
}

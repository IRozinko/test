package fintech.spain.alfa.product.affiliate;

import fintech.affiliate.AffiliateService;
import fintech.affiliate.db.AffiliatePartnerRepository;
import fintech.affiliate.model.SavePartnerCommand;
import fintech.affiliate.spi.AffiliateRegistry;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AffiliatesSetup {

    @Autowired
    private AffiliateService affiliateService;

    @Autowired
    private AlfaAffiliateReportUrlTemplateContextProvider reportUrlTemplateContextProvider;

    @Autowired
    private AffiliateRegistry affiliateRegistry;

    @Autowired
    private AffiliatePartnerRepository partnerRepository;

    public void setUp() {
        if (partnerRepository.count() == 0) {
            testPartner();
        }
        affiliateRegistry.setContextProvider(reportUrlTemplateContextProvider);
    }

    private void testPartner() {
        SavePartnerCommand command = new SavePartnerCommand();
        command.setLeadReportUrl("http://putsreq.com/8hazxicZdBgGu3wSzhGM?event=LEAD&affiliate_id={{AFFILIATE_LEAD_ID}}&gclid={{webEvent.gclid}}");
        command.setActionReportUrl("http://putsreq.com/8hazxicZdBgGu3wSzhGM?event=ACTION&affiliate_id={{AFFILIATE_LEAD_ID}}&loan_number={{loan.number}}&utm_source={{webEvent.utmSource}}&tm_campaign={{webEvent.utmCampaign}}&gclid={{webEvent.gclid}}&client_number={{client.number}}&application_number={{application.number}}");
        command.setActive(true);
        command.setName(AlfaConstants.TEST_AFFILIATE_NAME);
        command.setLeadConditionWorkflowActivityName(UnderwritingWorkflows.Activities.APPLICATION_FORM);
        command.setLeadConditionWorkflowActivityResolution(Resolutions.OK);
        command.setApiKey(AlfaConstants.TEST_AFFILIATE_API_KEY);
        affiliateService.savePartner(command);
    }
}

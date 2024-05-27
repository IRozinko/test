package fintech.spain.alfa.product.referral;

import fintech.cms.StringRenderer;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationType;
import fintech.lending.core.application.events.AbstractLoanApplicationEvent;
import fintech.lending.core.application.events.LoanApplicationRejectedEvent;
import fintech.lending.core.application.events.LoanApplicationRetriedEvent;
import fintech.lending.core.application.events.LoanApplicationSubmittedEvent;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.settings.AlfaSettings;
import fintech.spain.alfa.product.web.model.PopupInfo;
import fintech.spain.alfa.product.web.model.PopupType;
import fintech.spain.alfa.product.web.spi.PopupService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ReferralLendingCompanyPopup {
    private final static String REFERRAL_POPUP_ID = "ReferralPopupId";

    private final PopupService popupService;
    private final LoanApplicationService loanApplicationService;
    private final SettingsService settingsService;
    private final StringRenderer linkRenderer;
    private final AlfaCmsModels cmsModels;

    public ReferralLendingCompanyPopup(PopupService popupService, LoanApplicationService loanApplicationService,
                                       SettingsService settingsService, StringRenderer linkRenderer, AlfaCmsModels cmsModels) {
        this.popupService = popupService;
        this.loanApplicationService = loanApplicationService;
        this.settingsService = settingsService;
        this.linkRenderer = linkRenderer;
        this.cmsModels = cmsModels;
    }

    @EventListener
    public void showReferralPopup(LoanApplicationRejectedEvent event) {
        LoanApplication loanApplication = event.getLoanApplication();
        if (loanApplication.getType() == LoanApplicationType.NEW_LOAN) {
            ReferralLendingCompanySettings settings = settingsService.getJson(AlfaSettings.REFERRAL_LENDING_COMPANY_SETTINGS, ReferralLendingCompanySettings.class);
            Map<String, String> attributes = new HashMap<>();
            if (!settings.getExcludeTraffic().contains(loanApplication.getSourceName())) {
                Map<String, Object> context = cmsModels.applicationContext(loanApplication.getId());
                String link = linkRenderer.render(settings.getLink(), context);
                attributes.put("name", settings.getName());
                attributes.put("link", link);
            }
            PopupInfo popup = popupService.show(loanApplication.getClientId(), PopupType.REFERRAL_LENDING_COMPANY, null, attributes);
            if (popup != null) {
                loanApplicationService.addAttribute(loanApplication.getId(), REFERRAL_POPUP_ID, String.valueOf(popup.getId()));
            }
        }
    }

    @EventListener
    public void hideReferralPopup(AbstractLoanApplicationEvent event) {
        if (event instanceof LoanApplicationSubmittedEvent || event instanceof LoanApplicationRetriedEvent) {
            LoanApplication loanApplication = event.getLoanApplication();
            if (loanApplication.getType() == LoanApplicationType.NEW_LOAN) {
                popupService.markAsExhausted(loanApplication.getClientId(), PopupType.REFERRAL_LENDING_COMPANY);
            }
        }
    }

}

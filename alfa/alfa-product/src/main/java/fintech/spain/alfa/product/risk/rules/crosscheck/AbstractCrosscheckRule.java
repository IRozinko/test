//package fintech.spain.alfa.product.risk.rules.crosscheck;
//
//import fintech.Validate;
//import fintech.rules.model.Rule;
//import fintech.rules.model.RuleContext;
//import fintech.rules.model.RuleResult;
//import fintech.rules.model.RuleResultBuilder;
//import fintech.settings.SettingsService;
//import fintech.spain.alfa.product.AlfaConstants;
//import fintech.spain.alfa.product.settings.SettingContext;
//import fintech.spain.alfa.product.settings.AlfaSettings;
//import fintech.spain.alfa.product.workflow.common.Attributes;
//import fintech.spain.crosscheck.SpainCrosscheckService;
//import fintech.spain.crosscheck.model.SpainCrosscheckResult;
//import fintech.spain.crosscheck.model.SpainCrosscheckStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Map;
//
//public abstract class AbstractCrosscheckRule implements Rule {
//
//    @Autowired
//    protected SpainCrosscheckService crosscheckService;
//
//    @Autowired
//    protected SettingsService settingsService;
//
//    @Override
//    public RuleResult execute(RuleContext context, RuleResultBuilder builder) {
//        if (!context.hasAttribute(Attributes.CROSSCHECK_RESPONSE_ID)) {
//            return builder.reject(AlfaConstants.REJECT_REASON_NO_CROSSCHECK_RESPONSE);
//        }
//        SpainCrosscheckResult result = crosscheckService.get(Long.valueOf(context.getAttribute(Attributes.CROSSCHECK_RESPONSE_ID)));
//        Validate.isTrue(result.getStatus() != SpainCrosscheckStatus.ERROR, "Got crosscheck result in ERROR status");
//        Map<String,String> settingContext = SettingContext.getContextByWorkflowName(context.getWorkflowName());
//        AlfaSettings.CrosscheckRuleSettings settings = settingsService.getJson(settingContext.get(AlfaSettings.LENDING_RULES_CROSSCHECK), AlfaSettings.CrosscheckRuleSettings.class);
//        return doExecute(context, builder, settings, result);
//    }
//
//    protected abstract RuleResult doExecute(RuleContext context, RuleResultBuilder builder, AlfaSettings.CrosscheckRuleSettings settings, SpainCrosscheckResult result);
//
//}

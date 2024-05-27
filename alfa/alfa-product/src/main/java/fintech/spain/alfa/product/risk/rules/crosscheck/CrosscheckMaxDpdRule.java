//package fintech.spain.alfa.product.risk.rules.crosscheck;
//
//import fintech.rules.RuleBean;
//import fintech.rules.model.RuleContext;
//import fintech.rules.model.RuleResult;
//import fintech.rules.model.RuleResultBuilder;
//import fintech.spain.alfa.product.AlfaConstants;
//import fintech.spain.alfa.product.settings.AlfaSettings;
//import fintech.spain.crosscheck.model.SpainCrosscheckResult;
//
//@RuleBean
//public class CrosscheckMaxDpdRule extends AbstractCrosscheckRule {
//
//    @Override
//    protected RuleResult doExecute(RuleContext context, RuleResultBuilder builder, AlfaSettings.CrosscheckRuleSettings settings, SpainCrosscheckResult result) {
//        builder.addCheck("MaxDpd", settings.getMaxDpd(), result.getMaxDpd());
//        if (result.getMaxDpd() > settings.getMaxDpd()) {
//            return builder.reject(AlfaConstants.REJECT_REASON_CROSSCHECK_MAX_DPD);
//        }
//        return builder.approve();
//    }
//
//    @Override
//    public String getName() {
//        return "CrosscheckMaxDpd";
//    }
//}

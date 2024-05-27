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
//public class CrosscheckActiveLoanRule extends AbstractCrosscheckRule {
//
//    @Override
//    protected RuleResult doExecute(RuleContext context, RuleResultBuilder builder, AlfaSettings.CrosscheckRuleSettings settings, SpainCrosscheckResult result) {
//        builder.addCheck("OpenLoans", 0, result.getOpenLoans());
//        if (settings.isRejectOnActiveLoan() && result.getOpenLoans() > 0) {
//            return builder.cancel(AlfaConstants.REJECT_REASON_CROSSCHECK_ACTIVE_LOAN);
//        }
//        return builder.approve();
//    }
//
//    @Override
//    public String getName() {
//        return "CrosscheckActiveLoan";
//    }
//}

package fintech.dc.impl;

import fintech.dc.db.DebtEntity;
import fintech.dc.model.DcSettings;
import fintech.dc.model.Debt;
import fintech.dc.spi.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Triggers {

    private DcRegistry dcRegistry;
    private final DcSettings dcSettings;


    public Triggers(DcRegistry dcRegistry, DcSettings dcSettings) {
        this.dcRegistry = dcRegistry;
        this.dcSettings = dcSettings;
    }

    public List<DcSettings.Trigger> findTriggers(DebtEntity debt) {
        DcSettings.Portfolio portfolio = dcSettings.findPortfolio(debt.getPortfolio());
        return resolveTriggers(portfolio.getTriggers());
    }

    public List<DcSettings.Trigger> findTriggersOnVoidTransaction() {
        return resolveTriggers(dcSettings.getTriggersOnVoidTransaction());
    }

    private List<DcSettings.Trigger> resolveTriggers(List<DcSettings.Trigger> triggers) {
        List<DcSettings.Trigger> resolvedTriggers = new ArrayList<>();
        for (DcSettings.Trigger trigger : triggers) {
            if (!StringUtils.isEmpty(trigger.getTemplate())) {
                resolvedTriggers.add(dcSettings.findTriggerTemplate(trigger.getTemplate()));
            } else {
                resolvedTriggers.add(trigger);
            }
        }
        return resolvedTriggers;
    }

    public boolean shouldTrigger(DcSettings.Trigger trigger, DebtEntity debt) {
        Debt debtVo = debt.toValueObject();
        for (DcSettings.Condition condition : trigger.getConditions()) {
            ConditionHandler handler = dcRegistry.getConditionHandler(condition.getType());
            ConditionContext context = new ConditionContextImpl(debtVo, condition, trigger, dcSettings);
            boolean result = handler.apply(context);
            if (!result) {
                return false;
            }
        }
        return true;
    }

    public void trigger(DcSettings.Trigger trigger, DebtEntity debt) {
        Debt debtVo = debt.toValueObject();
        for (DcSettings.Action action : trigger.getActions()) {
            ActionHandler actionHandler = dcRegistry.getActionHandler(action.getType());
            ActionContext context = new ActionContextImpl(debtVo, action, trigger, dcSettings);
            actionHandler.handle(context);
        }
    }

}

package fintech.marketing;

import fintech.Validate;
import fintech.crm.client.db.QClientEntity;

import java.util.Optional;

public class MarketingConditionContextImpl implements MarketingConditionContext {

    public MarketingConditionContextImpl(MarketingAudienceSettings.AudienceCondition condition, QClientEntity entity) {
        this.condition = condition;
        this.entity = entity;
    }

    private final MarketingAudienceSettings.AudienceCondition condition;
    private final QClientEntity entity;


    @Override
    public <T> T getRequiredParam(String name, Class<T> paramClass) {
        Object value = this.condition.getParams().get(name);
        Validate.notNull(value, "Param not found by name [%s] in condition [%s]", name, condition.getType());
        assertParamClass(name, paramClass, value);
        return (T) value;
    }

    @Override
    public <T> Optional<T> getParam(String name, Class<T> paramClass) {
        Object value = this.condition.getParams().get(name);
        if (value == null) {
            return Optional.empty();
        }
        assertParamClass(name, paramClass, value);
        return Optional.of((T) value);
    }

    @Override
    public QClientEntity targetClient() {
        return entity;
    }

    private <T> void assertParamClass(String name, Class<T> paramClass, Object value) {
        Validate.isAssignableFrom(paramClass, value.getClass(), "Invalid param [%s] class [%s], expected [%s], condition [%s]", name, value.getClass().getSimpleName(), paramClass.getSimpleName(), condition.getType());
    }
}

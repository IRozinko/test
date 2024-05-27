package fintech.marketing;

import fintech.crm.client.db.QClientEntity;

import java.util.Optional;

public interface MarketingConditionContext {

    <T> T getRequiredParam(String name, Class<T> paramClass);

    <T> Optional<T> getParam(String name, Class<T> paramClass);

    QClientEntity targetClient();

}

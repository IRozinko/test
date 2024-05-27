package fintech.activity.spi;

import fintech.activity.model.Activity;

import java.util.Optional;

public interface BulkActionContext {

    Activity getActivity();

    <T> T getRequiredParam(String name, Class<T> paramClass);

    <T> Optional<T> getParam(String name, Class<T> paramClass);
}

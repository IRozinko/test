package fintech.bo.components.common;

import com.vaadin.data.ValueProvider;

public class NoValueProvider<S, T> implements ValueProvider<S, T> {

    @Override
    public T apply(S s) {
        return null;
    }

}

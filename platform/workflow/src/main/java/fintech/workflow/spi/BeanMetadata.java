package fintech.workflow.spi;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by ilgvarsj on 16.10.10.
 */
@Getter
@AllArgsConstructor
public class BeanMetadata<T> {
    private final Class<? extends T> beanClass;
    private final Object[] args;
}

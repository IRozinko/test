package fintech.bo.components.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

public abstract class BoComponentProviderSupport implements BoComponentProvider {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    public abstract BoComponent newInstance();

    @Override
    public BoComponent build(BoComponentContext context) {
        if (!metadata().matches(new BoComponentMetadata())) {
            PermissionDeniedComponent component = new PermissionDeniedComponent();
            component.setUp(context);
            return component;
        } else {
            BoComponent component = newInstance();
            beanFactory.autowireBean(component);
            component.setUp(context);
            return component;
        }
    }
}

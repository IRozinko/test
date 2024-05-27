package fintech.bo.components.callcenter;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringView(name = CallCenterView.NAME)
public class CallCenterView extends VerticalLayout implements View {

    public static final String NAME = "call-center";

    private final CallCenterListComponentProvider provider;

    public CallCenterView(CallCenterListComponentProvider provider) {
        this.provider = provider;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption(provider.metadata().getCaption());
        BoComponent boComponent = provider.build(new BoComponentContext());
        boComponent.refresh();
        addComponentsAndExpand(boComponent);
    }

}

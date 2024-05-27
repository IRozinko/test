package fintech.bo.components.sms;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SpringView(name = OutgoingSmsView.NAME)
public class OutgoingSmsView extends VerticalLayout implements View {

    public static final String NAME = "outgoing-sms";

    @Autowired
    private OutgoingSmsLogComponentProvider provider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption(provider.metadata().getCaption());
        BoComponent boComponent = provider.build(new BoComponentContext());
        boComponent.refresh();
        addComponentsAndExpand(boComponent);
    }
}

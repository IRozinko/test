package fintech.bo.components.affiliate;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SpringView(name = AffiliateEventsView.NAME)
public class AffiliateEventsView extends VerticalLayout implements View {

    public static final String NAME = "affiliate-events";

    @Autowired
    private AffiliateListComponentProvider provider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption(provider.metadata().getCaption());
        BoComponent boComponent = provider.build(new BoComponentContext());
        boComponent.refresh();
        addComponentsAndExpand(boComponent);
    }
}

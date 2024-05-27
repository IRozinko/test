package fintech.bo.spain.scoring;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SpringView(name = SpainScoringLogView.NAME)
public class SpainScoringLogView extends VerticalLayout implements View {

    public static final String NAME = "spain-scoring-log";

    @Autowired
    private SpainScoringLogComponentProvider provider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption(provider.metadata().getCaption());
        BoComponent boComponent = provider.build(new BoComponentContext());
        boComponent.refresh();
        addComponentsAndExpand(boComponent);
    }
}

package fintech.bo.components.payments.statement;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SpringView(name = StatementUploadView.NAME)
public class StatementUploadView extends VerticalLayout implements View {

    public static final String NAME = "statement-upload";
    
    @Autowired
    private StatementComponents statementComponents;
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Statement upload");
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(statementComponents.institutionGrid());
        addComponentsAndExpand(layout);
    }
}

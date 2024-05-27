package fintech.bo.components.dc;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SpringView(name = DebtUploadView.NAME)
public class DebtUploadView extends VerticalLayout implements View {

    public static final String NAME = "debt-upload";
    
    @Autowired
    private DebtComponents debtComponents;
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Import debts view");
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(debtComponents.institutionGrid());
        addComponentsAndExpand(layout);
    }
}

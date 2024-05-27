package fintech.bo.components.utils;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class SelectionComponent extends CustomComponent {

    private final TextField textField;
    private final Button button;

    public SelectionComponent(String caption) {
        setCaption(caption);
        textField = new TextField();
        textField.setReadOnly(true);
        button = new Button("Select");
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponentsAndExpand(textField);
        layout.addComponent(button);
        setCompositionRoot(layout);
    }

    public TextField getTextField() {
        return textField;
    }

    public Button getButton() {
        return button;
    }
}

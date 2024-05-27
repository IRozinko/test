package fintech.bo.components.risk.checklist;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import fintech.bo.api.model.risk.checklist.AddChecklistRequest;
import fintech.bo.components.dialogs.ActionDialog;

import java.util.List;

public class AddChecklistDialog extends ActionDialog {

    private Binder<AddChecklistRequest> binder = new Binder<>();

    public AddChecklistDialog(String type, List<String> types) {
        super("Edit Checklist", "Save");
        setWidth(500, Unit.PIXELS);

        ComboBox<String> typeField = new ComboBox<>();
        typeField.setWidth(100, Unit.PERCENTAGE);
        typeField.setPlaceholder("Type");
        typeField.setItems(types);
        typeField.setEmptySelectionAllowed(false);
        typeField.setTextInputAllowed(false);

        TextArea valueField = new TextArea("Value");
        valueField.setRows(2);
        valueField.setWidth(100, Unit.PERCENTAGE);

        TextArea commentField = new TextArea("Comment");
        commentField.setRows(2);
        commentField.setWidth(100, Unit.PERCENTAGE);

        binder.bind(typeField, AddChecklistRequest::getType, AddChecklistRequest::setType);
        binder.bind(valueField, AddChecklistRequest::getValue1, AddChecklistRequest::setValue1);
        binder.bind(commentField, AddChecklistRequest::getComment, AddChecklistRequest::setComment);

        AddChecklistRequest request = new AddChecklistRequest();
        request.setType(type);
        binder.setBean(request);

        FormLayout layout = new FormLayout();
        layout.addComponents(typeField, valueField, commentField);
        setDialogContent(layout);
    }

    public AddChecklistRequest getRequest() {
        return binder.getBean();
    }

}

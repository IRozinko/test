package fintech.bo.components.risk.checklist;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import fintech.bo.api.model.risk.checklist.UpdateChecklistRequest;
import fintech.bo.components.dialogs.ActionDialog;

import java.util.List;

public class EditChecklistDialog extends ActionDialog {

    private Binder<UpdateChecklistRequest> binder = new Binder<>();

    public EditChecklistDialog(String type, String value, String comment, List<String> types) {
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


        binder.bind(typeField, UpdateChecklistRequest::getType, UpdateChecklistRequest::setType);
        binder.bind(valueField, UpdateChecklistRequest::getValue1, UpdateChecklistRequest::setValue1);
        binder.bind(commentField, UpdateChecklistRequest::getComment, UpdateChecklistRequest::setComment);

        UpdateChecklistRequest request = new UpdateChecklistRequest();
        request.setType(type);
        request.setValue1(value);
        request.setComment(comment);
        binder.setBean(request);

        FormLayout layout = new FormLayout();
        layout.addComponents(typeField, valueField, commentField);
        setDialogContent(layout);
    }

    public UpdateChecklistRequest getRequest() {
        return binder.getBean();
    }

}

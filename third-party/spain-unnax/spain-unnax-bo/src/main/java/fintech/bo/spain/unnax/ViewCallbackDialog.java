package fintech.bo.spain.unnax;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fintech.JsonUtils;
import fintech.spain.unnax.db.jooq.tables.records.CallbackRecord;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;

public class ViewCallbackDialog extends Window {

    private final CallbackRecord callbackRecord;
    private final Queries queries;

    public ViewCallbackDialog(CallbackRecord record, Queries queries) {
        super("Callback event data");
        this.callbackRecord = record;
        this.queries = queries;

        Button closeButton = new Button("Close");
        closeButton.addClickListener((event) -> close());
        closeButton.focus();

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponents(closeButton);
        buttons.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);

        VerticalLayout root = new VerticalLayout();
        root.addComponentsAndExpand(createContent());
        root.addComponent(buttons);
        root.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
        setContent(root);
        center();
        setWidth(600, Unit.PIXELS);
        setHeight(400, Unit.PIXELS);
    }

    private Component createContent() {
        AceEditor editor = new AceEditor();
        String formattedCallbackData = JsonUtils.formatJson(callbackRecord.getData());
        editor.setValue(formattedCallbackData);
        editor.setMode(AceMode.json);
        editor.setEnabled(false);
        editor.setSizeFull();
        return editor;
    }

}

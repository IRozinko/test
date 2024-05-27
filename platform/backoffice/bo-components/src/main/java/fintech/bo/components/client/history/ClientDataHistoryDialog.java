package fintech.bo.components.client.history;

import com.vaadin.ui.*;
import fintech.bo.components.Formats;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientDataHistoryDialog extends Window {

    public ClientDataHistoryDialog(List<ClientDataHistory> history) {
        super("History");

        Grid<ClientDataHistory> content = new Grid<>();
        content.addColumn(r -> r.getTimestamp().format(DateTimeFormatter.ofPattern(Formats.DATE_TIME_FORMAT)))
            .setCaption("Timestamp");
        content.addColumn(ClientDataHistory::getValue).setCaption("Value");
        content.setItems(history);

        Button closeButton = new Button("Close");
        closeButton.addClickListener((event) -> close());
        closeButton.focus();

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponents(closeButton);
        buttons.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);

        VerticalLayout root = new VerticalLayout();
        root.addComponent(content);
        root.setComponentAlignment(content, Alignment.MIDDLE_CENTER);
        root.addComponent(buttons);
        root.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
        setContent(root);
        center();
        setWidth(550, Unit.PIXELS);
        setHeight(600, Unit.PIXELS);

    }


}

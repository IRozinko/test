package fintech.bo.components.dc;

import com.vaadin.data.HasDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.Formats;
import fintech.bo.db.jooq.dc.tables.records.ActionRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static fintech.bo.db.jooq.dc.Tables.ACTION;
import static java.util.stream.Collectors.toList;

public class ActionHistoryComponent extends VerticalLayout implements HasDataProvider<Record>, HasComponents {

    private DataProvider<Record, ?> dataProvider;

    public void show() {
        List<Record> actions = dataProvider.fetch(new Query<>(0, 20, Collections.emptyList(), null, null)).collect(toList());
        addStyleName(BackofficeTheme.COMPACT_SPACING);
        setSpacing(true);
        removeAllComponents();
        if (actions.isEmpty()) {
            Label info = new Label("No actions");
            info.addStyleName(ValoTheme.LABEL_SMALL);
            info.addStyleName(ValoTheme.LABEL_LIGHT);
            addComponent(info);
        } else {
            actions.forEach(this::addAction);
        }
    }

    private void addAction(Record record) {
        ActionRecord actionRecord = record.into(ACTION);
        String formattedDate = actionRecord.getCreatedAt().format(DateTimeFormatter.ofPattern(Formats.LONG_DATETIME_FORMAT));
        String firstLineText = String.format("%s &nbsp; &bull; &nbsp; %s", formattedDate, actionRecord.getActionName());
        if (!StringUtils.isBlank(actionRecord.getResolution())) {
            firstLineText += String.format("&nbsp; &bull; &nbsp; %s", actionRecord.getResolution());
        }
        Label firstLine = new Label(firstLineText, ContentMode.HTML);
        firstLine.addStyleName(ValoTheme.LABEL_COLORED);
        firstLine.addStyleName(ValoTheme.LABEL_SMALL);
        firstLine.addStyleName(ValoTheme.LABEL_BOLD);
        addComponent(firstLine);

        String secondLineText = String.format("Status: %s &nbsp; &bull; &nbsp; %s", actionRecord.getDebtStatus(), actionRecord.getAgent());
        Label secondLine = new Label(secondLineText, ContentMode.HTML);
        secondLine.addStyleName(ValoTheme.LABEL_TINY);
        addComponent(secondLine);


        if (!StringUtils.isBlank(actionRecord.getComments())) {
            TextArea comments = new TextArea();
            comments.setValue(actionRecord.getComments());
            comments.setWordWrap(true);
            comments.setReadOnly(true);
            comments.setRows(4);
            comments.setWidth(100, Unit.PERCENTAGE);
            comments.addStyleName(ValoTheme.TEXTAREA_SMALL);
            addComponent(comments);
        }

        Label spacer = new Label();
        addComponent(spacer);
    }

    @Override
    public void setDataProvider(DataProvider<Record, ?> dataProvider) {
        this.dataProvider = dataProvider;
        show();
    }

    @Override
    public DataProvider<Record, ?> getDataProvider() {
        return dataProvider;
    }
}

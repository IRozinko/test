package fintech.bo.components.sql;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import fintech.Validate;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.dialogs.InfoDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.SecuredView;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SecuredView({BackofficePermissions.ADMIN})
@Slf4j
@SpringView(name = SqlConsoleView.NAME)
public class SqlConsoleView extends VerticalLayout implements View {

    public static final String NAME = "sql-console";

    @Autowired
    private DSLContext db;
    private TextArea sql;

    private VerticalLayout gridLayout;

    @Autowired
    private TransactionTemplate txTemplate;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("SQL Console");

        removeAllComponents();
        VerticalLayout layout = new VerticalLayout();
        sql = new TextArea();
        sql.setPlaceholder("Enter SQL...");
        sql.addStyleName(BackofficeTheme.TEXT_MONO);
        sql.setWordWrap(false);
        sql.setRows(10);
        sql.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(sql);

        Button runSql = new Button("Run");
        runSql.addClickListener(e -> runSql());
        layout.addComponent(runSql);

        gridLayout = new VerticalLayout();
        gridLayout.setSizeFull();
        gridLayout.setMargin(false);
        layout.addComponentsAndExpand(gridLayout);
        layout.setSizeFull();

        addComponentsAndExpand(layout);
    }

    private void runSql() {
        Validate.notBlank(sql.getValue(), "Empty SQL");
        txTemplate.execute(status -> {
            // very important! will not allow to execute any update, deletes etc.
            status.setRollbackOnly();

            try (Stream<Record> stream = db.fetchStream(sql.getValue())) {
                gridLayout.removeAllComponents();
                List<Record> records = stream.limit(500).collect(Collectors.toList());
                Notifications.trayNotification("SQL executed");
                if (!records.isEmpty()) {
                    Grid<Record> grid = buildGrid(records);
                    grid.setSizeFull();
                    gridLayout.addComponentsAndExpand(grid);
                }
            }
            return 0;
        });
    }

    private Grid<Record> buildGrid(List<Record> records) {
        Grid<Record> grid = new Grid<>();
        grid.addColumn(r -> {
            Button btn = new Button("View");
            btn.addStyleNames(ValoTheme.BUTTON_SMALL);
            btn.addClickListener(e -> {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                for (Field field : r.fields()) {
                    pw.println(field.getName() + ":");
                    pw.println(field.getValue(r));
                    pw.println("----------------------------------------------------------");
                }
                InfoDialog dialog = Dialogs.showText("Row", sw.toString());
                dialog.setWidth(90, Unit.PERCENTAGE);
                dialog.setHeight(90, Unit.PERCENTAGE);
                dialog.setModal(true);
            });
            return btn;
        }, new ComponentRenderer()).setWidth(80).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setWidth(50).setSortable(false);

        Record first = records.get(0);
        for (Field field : first.fields()) {
            grid.addColumn(r -> r.getValue(field)).setCaption(field.getName());
        }
        grid.setItems(records);
        return grid;
    }
}

package fintech.bo.spain.asnef;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.Refreshable;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.security.SecuredView;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

@SpringView(name = AsnefView.NAME)
@SecuredView(BackofficePermissions.ADMIN)
public class AsnefView extends VerticalLayout implements View, Refreshable {

    public static final String NAME = "asnef";

    @Autowired
    private DSLContext db;

    @Autowired
    private AsnefComponents asnefComponents;

    private AsnefLogsDataProvider asnefLogsDataProvider;

    private ComboBox<String> type;

    private ComboBox<String> status;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Asnef");

        removeAllComponents();
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    @Override
    public void refresh() {
        asnefLogsDataProvider.setType(type.getValue());
        asnefLogsDataProvider.setStatus(status.getValue());
        asnefLogsDataProvider.refreshAll();
    }

    private void buildTop(GridViewLayout layout) {
        type = asnefComponents.typeComboBox();
        type.setCaption("Type");
        type.addValueChangeListener(event -> refresh());

        status = asnefComponents.statusComboBox();
        status.setCaption("Status");
        status.addValueChangeListener(event -> refresh());

        layout.addTopComponent(type);
        layout.addTopComponent(status);
        layout.addActionMenuItem("Generate file", e -> generateFile());
        layout.addActionMenuItem("Upload file", e -> importFile());
        layout.setRefreshAction((e) -> refresh());
    }

    private void buildGrid(GridViewLayout layout) {
        asnefLogsDataProvider = new AsnefLogsDataProvider(db);

        layout.setContent(asnefComponents.grid(asnefLogsDataProvider, this));
    }

    private void generateFile() {
        GenerateAsnefFileDialog dialog = asnefComponents.generateAsnefFileDialog();
        dialog.addCloseListener(e -> refresh());
        UI.getCurrent().addWindow(dialog);
    }

    private void importFile() {
        ImportAsnefFileDialog dialog = asnefComponents.importAsnefFileDialog();
        dialog.addCloseListener(e -> refresh());
        UI.getCurrent().addWindow(dialog);
    }
}

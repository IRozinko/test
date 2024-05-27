package fintech.bo.components.dc;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.DcApiClient;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.security.SecuredView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN})
@SpringView(name = DcSettingsView.NAME)
public class DcSettingsView extends VerticalLayout implements View {

    public static final String NAME = "dc-settings";

    @Autowired
    private DcApiClient dcApiClient;

    @Autowired
    private DcQueries dcQueries;

    private TextArea jsonViewer;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("DC settings");
        removeAllComponents();

        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildContent(layout);
        addComponentsAndExpand(layout);

        refresh();
    }

    private void buildTop(GridViewLayout layout) {
        layout.addActionMenuItem("Edit", (event) -> {
            EditDcSettingsDialog dialog = new EditDcSettingsDialog(dcApiClient, dcQueries);
            dialog.addCloseListener(e -> refresh());
            getUI().addWindow(dialog);
        });
        layout.setRefreshAction(e -> refresh());
    }

    private void buildContent(GridViewLayout layout) {
        jsonViewer = new TextArea();
        jsonViewer.setSizeFull();
        jsonViewer.setReadOnly(true);
        jsonViewer.addStyleName(BackofficeTheme.TEXT_MONO);
        layout.setContent(jsonViewer);
    }

    private void refresh() {
        jsonViewer.setValue(dcQueries.getRawSettings());
    }
}

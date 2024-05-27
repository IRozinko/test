package fintech.bo.components.institution;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.PaymentApiClient;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.security.SecuredView;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import static fintech.bo.db.jooq.payment.tables.Institution.INSTITUTION;
import static fintech.bo.db.jooq.payment.tables.InstitutionAccount.INSTITUTION_ACCOUNT;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN})
@SpringView(name = InstitutionsView.NAME)
public class InstitutionsView extends VerticalLayout implements View {

    public static final String NAME = "institutions";

    @Autowired
    private DSLContext db;

    @Autowired
    private PaymentApiClient paymentApiClient;

    private InstitutionDataProvider dataProvider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Institutions");

        dataProvider = new InstitutionDataProvider(db);

        removeAllComponents();
        GridViewLayout layout = new GridViewLayout();
        Grid<Record> grid = buildGrid(dataProvider);
        layout.setContent(grid);
        addComponentsAndExpand(layout);
    }

    private Grid<Record> buildGrid(JooqDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Edit", (record) -> {
            EditInstitutionDialog dialog = new EditInstitutionDialog(record, paymentApiClient);
            dialog.addCloseListener((e) -> refresh());
            getUI().addWindow(dialog);
        });
        builder.addColumn(INSTITUTION.ID);
        builder.addColumn(INSTITUTION.NAME);
        builder.addColumn(INSTITUTION.IS_PRIMARY);
        builder.addColumn(INSTITUTION_ACCOUNT.ACCOUNT_NUMBER);
        builder.addColumn(INSTITUTION.INSTITUTION_TYPE);
        builder.addColumn(INSTITUTION.STATEMENT_EXPORT_FORMAT);
        builder.addColumn(INSTITUTION.STATEMENT_IMPORT_FORMAT);
        builder.addColumn(INSTITUTION.STATEMENT_EXPORT_PARAMS_JSON);
        builder.addColumn(INSTITUTION.DISABLED);

        builder.addAuditColumns(INSTITUTION);
        builder.sortAsc(INSTITUTION.NAME);
        return builder.build(dataProvider);
    }

    private void refresh() {
        dataProvider.refreshAll();
    }

}

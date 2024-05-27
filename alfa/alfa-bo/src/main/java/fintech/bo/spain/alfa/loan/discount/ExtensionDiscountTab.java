package fintech.bo.spain.alfa.loan.discount;

import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.spain.alfa.api.ExtensionDiscountApiClient;
import org.jooq.DSLContext;

import static fintech.bo.api.model.permissions.BackofficePermissions.CLIENT_PHONES_TAB_VIEW_AND_EDIT;
import static fintech.bo.components.security.LoginService.hasPermission;
import static fintech.bo.spain.alfa.loan.discount.ExtensionDiscountDialog.createExtensionDiscount;

public class ExtensionDiscountTab extends VerticalLayout {

    private final long loanId;
    private final ExtensionDiscountApiClient api;
    private final DSLContext db;

    public ExtensionDiscountTab(long loanId, DSLContext db, ExtensionDiscountApiClient discountApiClient) {
        super();
        this.loanId = loanId;
        this.api = discountApiClient;
        this.db = db;
        render();
    }

    private void render() {
        removeAllComponents();
        addComponent(addExtensionDiscountButton());
        addComponent(new ExtensionDiscountGrid(new ExtensionDiscountGridDataProvider(loanId, db), api));
    }

    private Button addExtensionDiscountButton() {
        Button button = new Button("Add discount");
        button.setEnabled(hasPermission(CLIENT_PHONES_TAB_VIEW_AND_EDIT));
        button.addClickListener(event -> UI.getCurrent().addWindow(createExtensionDiscount(loanId, api, this::render)));
        return button;
    }

}

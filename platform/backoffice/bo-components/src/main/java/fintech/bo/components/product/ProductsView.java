package fintech.bo.components.product;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.ProductsApiClient;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.security.SecuredView;
import fintech.bo.db.jooq.lending.tables.records.ProductRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN})
@SpringView(name = ProductsView.NAME)
public class ProductsView extends VerticalLayout implements View {

    public static final String NAME = "products";

    private final ProductComponents productComponents;
    private final ProductsApiClient productsApiClient;
    private final ProductQueries productQueries;

    private ComboBox<ProductRecord> productComboBox;
    private TabSheet tabs;
    private ProductRecord product;

    @Autowired
    public ProductsView(ProductComponents productComponents, ProductsApiClient productsApiClient, ProductQueries productQueries) {
        this.productComponents = productComponents;
        this.productsApiClient = productsApiClient;
        this.productQueries = productQueries;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Products");
        removeAllComponents();

        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildContent(layout);
        addComponentsAndExpand(layout);

        productComboBox.setSelectedItem(productQueries.findLast());
    }

    private void buildTop(GridViewLayout layout) {
        productComboBox = productComponents.productsComboBox();
        productComboBox.setCaption("Select product");
        productComboBox.setWidth(300, Unit.PIXELS);
        productComboBox.addValueChangeListener(e -> {
            Long id = e.getValue().getId();
            product = productQueries.findById(id);
            refresh();
        });

        layout.addTopComponent(productComboBox);

        layout.addActionMenuItem("Edit", (event) -> {
            EditProductSettingsDialog dialog = new EditProductSettingsDialog(productsApiClient, product);
            dialog.addCloseListener((e) -> refresh());
            UI.getCurrent().addWindow(dialog);
        });

        layout.setRefreshAction(e -> refresh());
    }

    private void buildContent(GridViewLayout layout) {
        tabs = new TabSheet();
        tabs.setSizeFull();
        layout.setContent(tabs);
    }

    private Component settingsTab() {
        TextArea json = new TextArea();
        json.setValue(product.getDefaultSettingsJson());
        json.setSizeFull();
        json.setReadOnly(true);
        json.addStyleName(BackofficeTheme.TEXT_MONO);
        VerticalLayout layout = new VerticalLayout(json);
        layout.setSizeFull();
        return layout;
    }

    private void refresh() {
        tabs.removeAllComponents();
        if (product == null) {
            return;
        }
        product = productQueries.findById(product.getId());
        tabs.addTab(settingsTab(), "Settings");
    }
}

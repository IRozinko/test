package fintech.bo.components.product;

import com.vaadin.ui.ComboBox;
import fintech.bo.db.jooq.lending.tables.records.ProductRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductComponents {

    @Autowired
    private DSLContext db;

    public ProductDataProvider dataProvider() {
        return new ProductDataProvider(db);
    }

    public ComboBox<ProductRecord> productsComboBox() {
        ComboBox<ProductRecord> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Select product...");
        comboBox.setPageLength(20);
        comboBox.setDataProvider(dataProvider());
        comboBox.setPopupWidth("300px");
        comboBox.setTextInputAllowed(false);
        comboBox.setEmptySelectionAllowed(false);
        comboBox.setItemCaptionGenerator(item -> String.format("%s | %s",
            item.getId(),
            item.getProductType()));
        return comboBox;
    }
}

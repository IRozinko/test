package fintech.bo.components.invoice;

import com.vaadin.data.provider.GridSortOrderBuilder;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import fintech.bo.components.Formats;
import fintech.bo.components.GridHelper;
import fintech.bo.db.jooq.lending.tables.records.InvoiceItemRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static fintech.bo.components.Formats.decimalRenderer;
import static fintech.bo.components.GridHelper.alignRightStyle;
import static fintech.bo.db.jooq.lending.tables.InvoiceItem.INVOICE_ITEM;

@Component
public class InvoiceItemComponents {

    @Autowired
    private DSLContext db;

    public InvoiceItemDataProvider dataProvider() {
        return new InvoiceItemDataProvider(db);
    }

    public Grid<Record> grid(InvoiceItemDataProvider dataProvider) {
        Grid<Record> grid = new Grid<>();

        grid.addColumn(record -> record.get(INVOICE_ITEM.ID)).setCaption("ID").setId(INVOICE_ITEM.ID.getName()).setWidth(80);
        grid.addColumn(record -> record.get(INVOICE_ITEM.TYPE)).setCaption("Type").setId(INVOICE_ITEM.TYPE.getName()).setWidth(120);
        grid.addColumn(record -> record.get(INVOICE_ITEM.SUB_TYPE)).setCaption("Subtype").setId(INVOICE_ITEM.SUB_TYPE.getName()).setWidth(180);
        addBigDecimalColumn(grid, INVOICE_ITEM.AMOUNT, "Amount");
        addBigDecimalColumn(grid, INVOICE_ITEM.AMOUNT_PAID, "Amount paid");
        grid.addColumn(record -> record.get(INVOICE_ITEM.CORRECTION)).setCaption("Correction").setId(INVOICE_ITEM.CORRECTION.getName());
        Grid.Column<Record, LocalDateTime> createdAt = grid.addColumn(record -> record.get(INVOICE_ITEM.CREATED_AT))
            .setCaption("Created At").setRenderer(new LocalDateTimeRenderer(Formats.DATE_TIME_FORMAT))
            .setId(INVOICE_ITEM.CREATED_AT.getName()).setWidth(200);
        grid.setSortOrder(new GridSortOrderBuilder<Record>().thenDesc(createdAt));
        grid.setSizeFull();

        GridHelper.addTotalCountAsCaption(grid, dataProvider);
        grid.setDataProvider(dataProvider);
        return grid;
    }

    private static Grid.Column<Record, BigDecimal> addBigDecimalColumn(Grid<Record> grid, TableField<InvoiceItemRecord, BigDecimal> field, String caption) {
        return grid.addColumn(record -> record.get(field))
            .setCaption(caption)
            .setId(field.getName()).setRenderer(decimalRenderer())
            .setStyleGenerator(alignRightStyle())
            .setWidth(150);
    }

}

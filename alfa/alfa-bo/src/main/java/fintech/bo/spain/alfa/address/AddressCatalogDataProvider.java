package fintech.bo.spain.alfa.address;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.AddressRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.spain.alfa.db.jooq.alfa.Tables.ADDRESS;


public class AddressCatalogDataProvider extends JooqDataProvider<AddressRecord> {

    private String textFilter;

    public AddressCatalogDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<AddressRecord> buildSelect(Query<AddressRecord, String> query) {
        SelectWhereStep<AddressRecord> select = db.selectFrom(ADDRESS);
        if (!StringUtils.isBlank(textFilter)) {
            select.where(
                ADDRESS.POSTAL_CODE.startsWith(textFilter)
                    .or(ADDRESS.CITY.likeIgnoreCase("%" + textFilter + "%"))
                    .or(ADDRESS.PROVINCE.likeIgnoreCase("%" + textFilter + "%"))
                    .or(ADDRESS.STATE.likeIgnoreCase("%" + textFilter + "%"))
            );
        }
        return select;
    }

    @Override
    protected Object id(AddressRecord item) {
        return item.getId();
    }


    public void setTextFilter(String textFilter) {
        this.textFilter = textFilter;
    }
}

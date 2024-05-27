package fintech.bo.spain.alfa.strategy;

import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.db.jooq.cms.Tables;
import fintech.bo.db.jooq.cms.tables.records.ItemRecord;
import fintech.spain.alfa.strategy.CalculationStrategyCmsItemKey;
import org.jooq.DSLContext;

import java.util.Optional;

public class CmsItemNotificationLabel {

    private final String strategyType;
    private final String calculationType;

    public CmsItemNotificationLabel(String strategyType, String calculationType) {
        this.strategyType = strategyType;
        this.calculationType = calculationType;
    }

    public Optional<Label> get() {
        String itemKey = new CalculationStrategyCmsItemKey(strategyType, calculationType).get();

        ItemRecord itemRecord = ApiAccessor.gI().get(DSLContext.class).selectFrom(Tables.ITEM)
            .where(Tables.ITEM.ITEM_KEY.eq(itemKey))
            .fetchOne();

        if (itemRecord == null) {
            Label label = new Label("CMS Item '" + itemKey + "' will be created");
            label.addStyleName(ValoTheme.LABEL_COLORED);
            return Optional.of(label);
        }
        return Optional.empty();
    }
}

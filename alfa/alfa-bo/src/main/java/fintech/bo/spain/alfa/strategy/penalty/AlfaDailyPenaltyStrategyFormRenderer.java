package fintech.bo.spain.alfa.strategy.penalty;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.AlfaDailyPenaltyStrategyRecord;
import fintech.bo.spain.alfa.strategy.CmsItemNotificationLabel;
import fintech.spain.alfa.strategy.CalculationType;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.spain.alfa.strategy.penalty.DailyPenaltyStrategyProperties;
import fintech.strategy.bo.StrategyForm;
import fintech.strategy.bo.StrategyFormRenderer;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import static fintech.bo.spain.alfa.db.jooq.alfa.Tables.ALFA_DAILY_PENALTY_STRATEGY;

@Component
public class AlfaDailyPenaltyStrategyFormRenderer implements StrategyFormRenderer {

    @Override
    public boolean canRender(String strategyType, String calculationType) {
        return StrategyType.PENALTY.getType().equals(strategyType) && CalculationType.A.name().equals(calculationType);
    }

    @Override
    public StrategyForm renderNew() {
        return new AlfaDailyPenaltyStrategyForm();
    }

    @Override
    public StrategyForm renderForStrategy(Long strategyId) {
        AlfaDailyPenaltyStrategyRecord property = ApiAccessor.gI().get(DSLContext.class)
            .selectFrom(ALFA_DAILY_PENALTY_STRATEGY)
            .where(ALFA_DAILY_PENALTY_STRATEGY.CALCULATION_STRATEGY_ID.eq(strategyId))
            .fetchOne();
        return new AlfaDailyPenaltyStrategyForm(new DailyPenaltyStrategyProperties().setPenaltyRate(property.getPenaltyRate()));
    }

    private static class AlfaDailyPenaltyStrategyForm extends StrategyForm {

        private final DailyPenaltyStrategyProperties properties;

        AlfaDailyPenaltyStrategyForm(DailyPenaltyStrategyProperties properties) {
            this.properties = properties;

            VerticalLayout root = new VerticalLayout();
            root.setMargin(false);

            new CmsItemNotificationLabel(StrategyType.PENALTY.getType(), CalculationType.A.name())
                .get()
                .ifPresent(root::addComponent);

            Binder<DailyPenaltyStrategyProperties> binder = new Binder<>();
            binder.setBean(properties);

            TextField penaltyRate = new TextField("Daily penalty rate (%)");
            binder.forField(penaltyRate)
                .asRequired()
                .withNullRepresentation("")
                .withConverter(new StringToBigDecimalConverter("Number is required"))
                .bind(DailyPenaltyStrategyProperties::getPenaltyRate, DailyPenaltyStrategyProperties::setPenaltyRate);


            root.addComponent(penaltyRate);
            root.setHeight(100, Unit.PERCENTAGE);

            setCompositionRoot(root);
        }

        AlfaDailyPenaltyStrategyForm() {
            this(new DailyPenaltyStrategyProperties());
        }

        @Override
        public Object getStrategyProperties() {
            return properties;
        }
    }
}

package fintech.bo.spain.alfa.strategy.interest;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.spain.alfa.db.jooq.alfa.Tables;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.AlfaMonthlyInterestStrategyRecord;
import fintech.bo.spain.alfa.strategy.CmsItemNotificationLabel;
import fintech.spain.alfa.strategy.CalculationType;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.spain.alfa.strategy.interest.MonthlyInterestStrategyProperties;
import fintech.strategy.bo.StrategyForm;
import fintech.strategy.bo.StrategyFormRenderer;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

@Component
public class AlfaInterestStrategyFormRenderer implements StrategyFormRenderer {

    @Override
    public boolean canRender(String strategyType, String calculationType) {
        return StrategyType.INTEREST.getType().equals(strategyType)
            && CalculationType.X.name().equals(calculationType);
    }

    @Override
    public StrategyForm renderNew() {
        return new AlfaInterestStrategyForm();
    }

    @Override
    public StrategyForm renderForStrategy(Long strategyId) {
        AlfaMonthlyInterestStrategyRecord property = ApiAccessor.gI().get(DSLContext.class)
            .selectFrom(Tables.ALFA_MONTHLY_INTEREST_STRATEGY)
            .where(Tables.ALFA_MONTHLY_INTEREST_STRATEGY.CALCULATION_STRATEGY_ID.eq(strategyId))
            .fetchOne();

        return new AlfaInterestStrategyForm(new MonthlyInterestStrategyProperties()
            .setMonthlyInterestRate(property.getInterestRate())
            .setUsingDecisionEngine(property.getUsingDecisionEngine())
            .setScenario(property.getScenario()));
    }

    private static class AlfaInterestStrategyForm extends StrategyForm {

        private final MonthlyInterestStrategyProperties properties;

        AlfaInterestStrategyForm(MonthlyInterestStrategyProperties properties) {
            this.properties = properties;

            VerticalLayout root = new VerticalLayout();
            root.setMargin(false);

            new CmsItemNotificationLabel(StrategyType.INTEREST.getType(), CalculationType.X.name())
                .get()
                .ifPresent(root::addComponent);

            Binder<MonthlyInterestStrategyProperties> binder = new Binder<>();
            binder.setBean(properties);

            TextField monthlyInterestRate = new TextField("Monthly interest rate (%)");
            binder.forField(monthlyInterestRate)
                .asRequired()
                .withNullRepresentation("")
                .withConverter(new StringToBigDecimalConverter("Number is required"))
                .bind(MonthlyInterestStrategyProperties::getMonthlyInterestRate, MonthlyInterestStrategyProperties::setMonthlyInterestRate);

            CheckBox usingDecisionEngine = new CheckBox("Use Decision Engine");
            usingDecisionEngine.setWidth(50, Unit.PERCENTAGE);
            root.addComponent(usingDecisionEngine);
            usingDecisionEngine.setValue(properties.isUsingDecisionEngine());
            binder.forField(usingDecisionEngine).bind(MonthlyInterestStrategyProperties::isUsingDecisionEngine, MonthlyInterestStrategyProperties::setUsingDecisionEngine);

            TextField deScenario = new TextField("Decision Engine Scenario");
            binder.forField(deScenario)
                .asRequired()
                .withNullRepresentation("")
                .bind(MonthlyInterestStrategyProperties::getScenario, MonthlyInterestStrategyProperties::setScenario);


            root.addComponent(monthlyInterestRate);
            root.addComponent(usingDecisionEngine);
            root.addComponent(deScenario);
            root.setHeight(100, Unit.PERCENTAGE);

            setCompositionRoot(root);
        }

        AlfaInterestStrategyForm() {
            this(new MonthlyInterestStrategyProperties());
        }

        @Override
        public Object getStrategyProperties() {
            return properties;
        }
    }
}

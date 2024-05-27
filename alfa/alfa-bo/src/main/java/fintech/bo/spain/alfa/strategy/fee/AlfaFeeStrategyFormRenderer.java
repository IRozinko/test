package fintech.bo.spain.alfa.strategy.fee;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.validator.BigDecimalRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.spain.alfa.strategy.CmsItemNotificationLabel;
import fintech.spain.alfa.strategy.CalculationType;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.spain.alfa.strategy.fee.FeeStrategyProperties;
import fintech.strategy.bo.StrategyForm;
import fintech.strategy.bo.StrategyFormRenderer;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;
import static fintech.bo.db.jooq.alfa.Tables.ALFA_FEE_STRATEGY;

@Component
public class AlfaFeeStrategyFormRenderer implements StrategyFormRenderer {

    @Override
    public boolean canRender(String strategyType, String calculationType) {
        return StrategyType.FEE.getType().equals(strategyType)
            && CalculationType.FT.name().equals(calculationType);
    }

    @Override
    public StrategyForm renderNew() {
        return new AlfaFeeStrategyForm();
    }

    @Override
    public StrategyForm renderForStrategy(Long strategyId) {
        List<FeeStrategyProperties.FeeOption> feeStrategyProperties = ApiAccessor.gI().get(DSLContext.class)
            .selectFrom(ALFA_FEE_STRATEGY)
            .where(ALFA_FEE_STRATEGY.CALCULATION_STRATEGY_ID.eq(strategyId))
            .fetch()
            .stream()
            .map(r -> new FeeStrategyProperties.FeeOption()
                .setCompany(r.getCompany())
                .setOneTimeFeeRate(r.getFeeRate()))
            .sorted(Comparator.comparing(FeeStrategyProperties.FeeOption::getCompany))
            .collect(Collectors.toList());

        return new AlfaFeeStrategyForm(new FeeStrategyProperties().setFees(feeStrategyProperties));
    }

    private static class AlfaFeeStrategyForm extends StrategyForm {

        private final FeeStrategyProperties properties;
        private final Panel optionsHolder;

        AlfaFeeStrategyForm(FeeStrategyProperties properties) {
            this.properties = properties;

            if (this.properties.getFees().isEmpty()) {
                this.properties.getFees().add(new FeeStrategyProperties.FeeOption());
            }

            VerticalLayout root = new VerticalLayout();
            root.setMargin(false);

            new CmsItemNotificationLabel(StrategyType.FEE.getType(), CalculationType.FT.name())
                .get()
                .ifPresent(root::addComponent);

            Button addButton = new Button("New Option", VaadinIcons.PLUS);
            addButton.addStyleName(ValoTheme.BUTTON_TINY);
            addButton.addClickListener(c -> {
                properties.getFees().add(new FeeStrategyProperties.FeeOption());
                refresh();
            });

            root.addComponent(addButton);

            optionsHolder = new Panel();
            optionsHolder.addStyleName(ValoTheme.PANEL_BORDERLESS);
            optionsHolder.setHeight(300, Unit.PIXELS);

            root.addComponent(optionsHolder);
            root.setHeight(100, Unit.PERCENTAGE);


            setCompositionRoot(root);

            refresh();
        }

        AlfaFeeStrategyForm() {
            this(new FeeStrategyProperties());
        }

        private void refresh() {
            VerticalLayout scroll = new VerticalLayout();
            scroll.setMargin(false);
            optionsHolder.setContent(scroll);
            properties.getFees()
                .forEach(feeOption -> {
                    HorizontalLayout row = new HorizontalLayout();

                    Binder<FeeStrategyProperties.FeeOption> binder = new Binder<>();

                    TextField term = new TextField("Company ");
                    binder.forField(term)
                        .asRequired()
                        .withNullRepresentation("")
                        .bind(FeeStrategyProperties.FeeOption::getCompany, FeeStrategyProperties.FeeOption::setCompany);

                    row.addComponent(term);
                    TextField rate = new TextField("Fee rate (%)");
                    binder.forField(rate)
                        .asRequired()
                        .withNullRepresentation("")
                        .withConverter(new StringToBigDecimalConverter("Decimal number is required"))
                        .withValidator(new BigDecimalRangeValidator("Positive value is required", amount(0.01), null))
                        .bind(FeeStrategyProperties.FeeOption::getOneTimeFeeRate, FeeStrategyProperties.FeeOption::setOneTimeFeeRate);
                    row.addComponent(rate);

                    Button deleteButton = new Button("", VaadinIcons.TRASH);
                    deleteButton.addStyleNames(ValoTheme.BUTTON_DANGER);
                    deleteButton.addClickListener(c -> {
                        properties.getFees().remove(feeOption);
                        refresh();
                    });
                    row.addComponent(deleteButton);
                    row.setComponentAlignment(deleteButton, Alignment.BOTTOM_RIGHT);

                    binder.setBean(feeOption);

                    scroll.addComponent(row);
                });
        }

        @Override
        public Object getStrategyProperties() {
            return properties;
        }
    }
}

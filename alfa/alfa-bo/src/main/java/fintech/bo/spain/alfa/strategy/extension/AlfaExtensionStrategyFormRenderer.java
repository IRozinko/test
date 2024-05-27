package fintech.bo.spain.alfa.strategy.extension;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToLongConverter;
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
import fintech.spain.alfa.strategy.extension.ExtensionStrategyProperties;
import fintech.strategy.bo.StrategyForm;
import fintech.strategy.bo.StrategyFormRenderer;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;
import static fintech.bo.spain.alfa.db.jooq.alfa.Tables.ALFA_EXTENSION_STRATEGY;

@Component
public class AlfaExtensionStrategyFormRenderer implements StrategyFormRenderer {

    @Override
    public boolean canRender(String strategyType, String calculationType) {
        return StrategyType.EXTENSION.getType().equals(strategyType)
            && CalculationType.D.name().equals(calculationType);
    }

    @Override
    public StrategyForm renderNew() {
        return new AlfaExtensionStrategyForm();
    }

    @Override
    public StrategyForm renderForStrategy(Long strategyId) {
        List<ExtensionStrategyProperties.ExtensionOption> extensionOptions = ApiAccessor.gI().get(DSLContext.class)
            .selectFrom(ALFA_EXTENSION_STRATEGY)
            .where(ALFA_EXTENSION_STRATEGY.CALCULATION_STRATEGY_ID.eq(strategyId))
            .fetch()
            .stream()
            .map(r -> new ExtensionStrategyProperties.ExtensionOption()
                .setRate(r.getRate())
                .setTerm(r.getTerm()))
            .sorted(Comparator.comparing(ExtensionStrategyProperties.ExtensionOption::getTerm))
            .collect(Collectors.toList());

        return new AlfaExtensionStrategyForm(new ExtensionStrategyProperties().setExtensions(extensionOptions));
    }

    private static class AlfaExtensionStrategyForm extends StrategyForm {

        private final ExtensionStrategyProperties properties;
        private final Panel optionsHolder;

        AlfaExtensionStrategyForm(ExtensionStrategyProperties properties) {
            this.properties = properties;

            if (this.properties.getExtensions().isEmpty()) {
                this.properties.getExtensions().add(new ExtensionStrategyProperties.ExtensionOption());
            }

            VerticalLayout root = new VerticalLayout();
            root.setMargin(false);

            new CmsItemNotificationLabel(StrategyType.EXTENSION.getType(), CalculationType.D.name())
                .get()
                .ifPresent(root::addComponent);

            Button addButton = new Button("New Option", VaadinIcons.PLUS);
            addButton.addStyleName(ValoTheme.BUTTON_TINY);
            addButton.addClickListener(c -> {
                properties.getExtensions().add(new ExtensionStrategyProperties.ExtensionOption());
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

        AlfaExtensionStrategyForm() {
            this(new ExtensionStrategyProperties());
        }

        private void refresh() {
            VerticalLayout scroll = new VerticalLayout();
            scroll.setMargin(false);
            optionsHolder.setContent(scroll);
            properties.getExtensions()
                .forEach(extensionOption -> {
                    HorizontalLayout row = new HorizontalLayout();

                    Binder<ExtensionStrategyProperties.ExtensionOption> binder = new Binder<>();

                    TextField term = new TextField("Term (days)");
                    binder.forField(term)
                        .asRequired()
                        .withNullRepresentation("")
                        .withConverter(new StringToLongConverter("Number is required"))
                        .bind(ExtensionStrategyProperties.ExtensionOption::getTerm, ExtensionStrategyProperties.ExtensionOption::setTerm);

                    row.addComponent(term);
                    TextField rate = new TextField("Rate (%)");
                    binder.forField(rate)
                        .asRequired()
                        .withNullRepresentation("")
                        .withConverter(new StringToBigDecimalConverter("Decimal number is required"))
                        .withValidator(new BigDecimalRangeValidator("Positive value is required", amount(0.01), null))
                        .bind(ExtensionStrategyProperties.ExtensionOption::getRate, ExtensionStrategyProperties.ExtensionOption::setRate);
                    row.addComponent(rate);

                    Button deleteButton = new Button("", VaadinIcons.TRASH);
                    deleteButton.addStyleNames(ValoTheme.BUTTON_DANGER);
                    deleteButton.addClickListener(c -> {
                        properties.getExtensions().remove(extensionOption);
                        refresh();
                    });
                    row.addComponent(deleteButton);
                    row.setComponentAlignment(deleteButton, Alignment.BOTTOM_RIGHT);

                    binder.setBean(extensionOption);

                    scroll.addComponent(row);
                });
        }

        @Override
        public Object getStrategyProperties() {
            return properties;
        }
    }
}

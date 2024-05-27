package fintech.strategy.bo;

import com.vaadin.data.Binder;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.StrategyApiClient;
import fintech.bo.api.model.strategy.CreateStrategyRequest;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import retrofit2.Call;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class NewStrategyDialog extends ActionDialog {

    private Binder<CreateStrategyRequest> binder;

    private final SupportedStrategiesResolver resolver;
    private final Collection<StrategyFormRenderer> renderers;
    private StrategyForm strategyForm;

    public NewStrategyDialog() {
        super("Create strategy", "Save");

        resolver = ApiAccessor.gI().get(SupportedStrategiesResolver.class);
        renderers = ApiAccessor.gI().getAll(StrategyFormRenderer.class);

        binder = new Binder<>();
        binder.setBean(new CreateStrategyRequest());

        setDialogContent(content());
        setModal(true);
        setWidth(600, Unit.PIXELS);
    }

    private Component content() {
        VerticalLayout layout = new VerticalLayout();

        List<SupportedStrategiesResolver.Strategy> availableStrategies = resolver.resolve();

        ComboBox<SupportedStrategiesResolver.Strategy> strategyType = new ComboBox<>("Strategy Type");
        strategyType.setWidth(50, Unit.PERCENTAGE);
        strategyType.setItems(availableStrategies);
        strategyType.setEmptySelectionAllowed(false);
        strategyType.setTextInputAllowed(false);
        strategyType.setItemCaptionGenerator(SupportedStrategiesResolver.Strategy::getName);
        layout.addComponent(strategyType);

        binder.forField(strategyType)
            .asRequired()
            .bind(
                bean -> availableStrategies.stream()
                    .filter(f -> f.getType().equals(bean.getStrategyType()))
                    .findFirst()
                    .orElse(null),
                (bean, value) -> {
                    Optional.ofNullable(value).ifPresent(v -> bean.setStrategyType(v.getType()));
                });

        ComboBox<String> calculationType = new ComboBox<>("Calculation Type");
        calculationType.setEmptySelectionAllowed(false);
        calculationType.setTextInputAllowed(false);
        calculationType.setWidth(50, Unit.PERCENTAGE);
        layout.addComponent(calculationType);

        binder.forField(calculationType)
            .asRequired()
            .bind(CreateStrategyRequest::getCalculationType, CreateStrategyRequest::setCalculationType);

        strategyType.addValueChangeListener(v -> {
            List<String> strategies = Optional.ofNullable(v.getValue())
                .map(SupportedStrategiesResolver.Strategy::getCalculationTypes)
                .orElseGet(ArrayList::new);

            calculationType.setValue(null);
            calculationType.setItems(strategies);
        });

        TextField version = new TextField("Version");
        version.setWidth(50, Unit.PERCENTAGE);
        layout.addComponent(version);
        binder.forField(version)
            .asRequired()
            .bind(CreateStrategyRequest::getVersion, CreateStrategyRequest::setVersion);

        CheckBox enabledCheckBox = new CheckBox("Enabled");
        enabledCheckBox.setWidth(50, Unit.PERCENTAGE);
        layout.addComponent(enabledCheckBox);
        binder.forField(enabledCheckBox).bind(CreateStrategyRequest::isEnabled, CreateStrategyRequest::setEnabled);

        CheckBox defaultCheckBox = new CheckBox("Is Default");
        defaultCheckBox.setWidth(50, Unit.PERCENTAGE);
        layout.addComponent(defaultCheckBox);
        binder.forField(defaultCheckBox).bind(CreateStrategyRequest::isDefaultStrategy, CreateStrategyRequest::setDefaultStrategy);

        defaultCheckBox.addValueChangeListener(v -> {
            if (Boolean.TRUE.equals(v.getValue())) {
                enabledCheckBox.setValue(true);
                enabledCheckBox.setEnabled(false);
            } else {
                enabledCheckBox.setEnabled(true);
            }
        });

        VerticalLayout formHolder = new VerticalLayout();
        formHolder.setMargin(false);
        layout.addComponent(formHolder);

        calculationType.addValueChangeListener(v -> {
            formHolder.removeAllComponents();
            strategyForm = null;

            Optional<StrategyForm> form = Optional.ofNullable(v.getValue())
                .flatMap(str ->
                    renderers.stream()
                        .filter(f -> f.canRender(binder.getBean().getStrategyType(), binder.getBean().getCalculationType()))
                        .findFirst()
                )
                .map(StrategyFormRenderer::renderNew);

            if (form.isPresent()) {
                formHolder.addComponent(form.get());
                strategyForm = form.get();
                center();
            }
        });

        return layout;
    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            CreateStrategyRequest request = binder.getBean().setProperties(strategyForm.getStrategyProperties());

            Call<Void> call = ApiAccessor.gI().get(StrategyApiClient.class).createStrategy(request);
            BackgroundOperations.callApi("Saving new calculation strategy", call, t -> {
                Notifications.trayNotification("Strategy saved");
                close();
            }, Notifications::errorNotification);
        }
    }
}

package fintech.strategy.bo;

import com.vaadin.data.Binder;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.StrategyApiClient;
import fintech.bo.api.model.strategy.UpdateStrategyRequest;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.strategy.tables.records.CalculationStrategyRecord;
import org.jooq.DSLContext;
import retrofit2.Call;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static fintech.bo.db.jooq.strategy.Tables.CALCULATION_STRATEGY;

public class EditStrategyDialog extends ActionDialog {

    private Binder<UpdateStrategyRequest> binder;

    private final DSLContext db;
    private final SupportedStrategiesResolver resolver;
    private final Collection<StrategyFormRenderer> renderers;

    private StrategyForm strategyForm;

    private Long strategyId;
    private final CalculationStrategyRecord strategyRecord;

    public EditStrategyDialog(Long strategyId) {
        super("Edit strategy", "Save");
        this.strategyId = strategyId;

        resolver = ApiAccessor.gI().get(SupportedStrategiesResolver.class);
        renderers = ApiAccessor.gI().getAll(StrategyFormRenderer.class);
        db = ApiAccessor.gI().get(DSLContext.class);
        strategyRecord = db.selectFrom(CALCULATION_STRATEGY)
            .where(CALCULATION_STRATEGY.ID.eq(strategyId))
            .fetchOne();

        binder = new Binder<>();

        setDialogContent(content());
        setModal(true);
        setWidth(600, Unit.PIXELS);

        binder.setBean(new UpdateStrategyRequest()
            .setDefaultStrategy(Boolean.TRUE.equals(strategyRecord.getIsDefault()))
            .setEnabled(strategyRecord.getEnabled())
            .setStrategyId(strategyRecord.getId())
            .setVersion(strategyRecord.getVersion()));
    }

    private Component content() {
        VerticalLayout layout = new VerticalLayout();

        Label warningLabel = new Label("Updating strategy will affect all dependant loans");
        warningLabel.addStyleName(ValoTheme.LABEL_FAILURE);
        layout.addComponent(warningLabel);

        List<SupportedStrategiesResolver.Strategy> availableStrategies = resolver.resolve();

        ComboBox<SupportedStrategiesResolver.Strategy> type = new ComboBox<>("Type");
        type.setWidth(50, Unit.PERCENTAGE);
        type.setItems(availableStrategies);
        type.setEmptySelectionAllowed(false);
        type.setTextInputAllowed(false);
        type.setValue(availableStrategies.stream().filter(s -> s.getType().equals(strategyRecord.getStrategyType())).findFirst().orElse(null));
        type.setEnabled(false);
        type.setItemCaptionGenerator(SupportedStrategiesResolver.Strategy::getName);
        layout.addComponent(type);

        ComboBox<String> strategy = new ComboBox<>("Strategy");
        strategy.setEmptySelectionAllowed(false);
        strategy.setTextInputAllowed(false);
        strategy.setWidth(50, Unit.PERCENTAGE);
        strategy.setValue(strategyRecord.getCalculationType());
        strategy.setEnabled(false);
        layout.addComponent(strategy);

        TextField version = new TextField("Version");
        version.setWidth(50, Unit.PERCENTAGE);
        layout.addComponent(version);
        binder.forField(version).bind(UpdateStrategyRequest::getVersion, UpdateStrategyRequest::setVersion);

        CheckBox enabledCheckBox = new CheckBox("Enabled");
        enabledCheckBox.setWidth(50, Unit.PERCENTAGE);
        enabledCheckBox.setEnabled(!Boolean.TRUE.equals(strategyRecord.getIsDefault()));
        layout.addComponent(enabledCheckBox);
        binder.forField(enabledCheckBox).bind(UpdateStrategyRequest::isEnabled, UpdateStrategyRequest::setEnabled);

        CheckBox defaultCheckBox = new CheckBox("Is Default");
        defaultCheckBox.setWidth(50, Unit.PERCENTAGE);
        defaultCheckBox.setEnabled(!Boolean.TRUE.equals(strategyRecord.getIsDefault()));
        layout.addComponent(defaultCheckBox);
        binder.forField(defaultCheckBox).bind(UpdateStrategyRequest::isDefaultStrategy, UpdateStrategyRequest::setDefaultStrategy);

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

        Optional<StrategyForm> form = renderers.stream()
            .filter(f -> f.canRender(strategyRecord.getStrategyType(), strategyRecord.getCalculationType()))
            .findFirst()
            .map(r -> r.renderForStrategy(strategyId));

        if (form.isPresent()) {
            formHolder.addComponent(form.get());
            strategyForm = form.get();
            center();
        }

        return layout;
    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            UpdateStrategyRequest request = binder.getBean().setProperties(strategyForm.getStrategyProperties());

            Call<Void> call = ApiAccessor.gI().get(StrategyApiClient.class).updateStrategy(request);
            BackgroundOperations.callApi("Updating calculation strategy", call, t -> {
                Notifications.trayNotification("Strategy updated");
                close();
            }, Notifications::errorNotification);
        }
    }
}

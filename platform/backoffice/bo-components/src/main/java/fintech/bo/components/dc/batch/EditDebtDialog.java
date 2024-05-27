package fintech.bo.components.dc.batch;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import fintech.TimeMachine;
import fintech.bo.components.Formats;
import fintech.bo.components.dc.DcComponents;
import fintech.bo.components.dc.DcQueries;
import fintech.bo.components.dc.DcSettingsJson;
import fintech.bo.components.dc.DcSettingsJson.Portfolio;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class EditDebtDialog extends Window {

    private final DcQueries dcQueries;
    private final String defaultPortfolio;

    private static final String DEFAULT_STATUS = "NoStatus";
    private static final String DEFAULT_PORTFOLIO = "Collections";
    private static final String DEFAULT_AGENT = "Distribute Evenly";

    private final ImmutableList<String> EXTERNAL_PORTFOLIOS = ImmutableList.<String>builder()
        .add("Sold")
        .add("External Collections")
        .build();

    private final ImmutableList<String> LEGAL_STATUSES = ImmutableList.<String>builder()
        .add("Concursal")
        .add("Fraud")
        .add("RecoveryClaim")
        .build();

    EditDebtDialog(String caption, String actionButtonCaption, boolean showPortfolios, DcComponents dcComponents,
                   DcQueries dcQueries, Consumer<EditDebtForm> consumer) {
        this(caption, actionButtonCaption, showPortfolios, dcComponents, dcQueries, consumer, DEFAULT_PORTFOLIO);
    }

    EditDebtDialog(String caption, String actionButtonCaption, boolean showPortfolios, DcComponents dcComponents,
                   DcQueries dcQueries, Consumer<EditDebtForm> consumer, String defaultPortfolio) {
        super(caption);
        this.defaultPortfolio = Optional.ofNullable(Strings.emptyToNull(defaultPortfolio)).orElse(DEFAULT_PORTFOLIO);
        this.dcQueries = dcQueries;

        ComboBox<String> agentsComboBox = dcComponents.agentsComboBox(DEFAULT_AGENT);
        agentsComboBox.setWidth(100, Unit.PERCENTAGE);

        DateTimeField nextActionAt = new DateTimeField("Next action at", TimeMachine.now());
        nextActionAt.setDateFormat(Formats.DATE_TIME_FORMAT);
        nextActionAt.setVisible(false);
        nextActionAt.setTextFieldEnabled(false);

        ComboBox<String> nextActionsComboBox = new ComboBox<>("Next action");
        nextActionsComboBox.setItems(dcComponents.actionsForPortfolio(this.defaultPortfolio));
        nextActionsComboBox.setTextInputAllowed(false);
        nextActionsComboBox.setEmptySelectionAllowed(true);
        nextActionsComboBox.setPageLength(20);

        nextActionsComboBox.setWidth(100, Unit.PERCENTAGE);
        nextActionsComboBox.addValueChangeListener(e -> {
            nextActionAt.setVisible(isNotBlank(e.getValue()));
        });

        ComboBox<String> portfoliosComboBox = portfoliosComboBox();
        portfoliosComboBox.setWidth(100, Unit.PERCENTAGE);
        portfoliosComboBox.addValueChangeListener(portfolio -> {
            nextActionsComboBox.clear();
            nextActionsComboBox.setItems(dcComponents.actionsForPortfolio(portfolio.getValue()));
        });

        ComboBox<String> statusesComboBox = statusesComboBox();
        statusesComboBox.setWidth(100, Unit.PERCENTAGE);
        statusesComboBox.addValueChangeListener(e -> {
            List<String> portfolios = LEGAL_STATUSES.contains(e.getValue()) ? getPortfoliosWithout("Legal") : getPortfolios();
            portfoliosComboBox.setItems(portfolios);
            portfoliosComboBox.setValue(portfolios.get(0));
        });

        Button actionButton = new Button(actionButtonCaption);
        actionButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        actionButton.addClickListener((event) -> {
            consumer.accept(new EditDebtDialog.EditDebtForm()
                .setAgent(agentsComboBox.getValue())
                .setStatus(statusesComboBox.getValue())
                .setNextAction(nextActionsComboBox.getValue())
                .setPortfolio(portfoliosComboBox.getValue())
                .setNextActionAt(nextActionAt.isVisible() ? nextActionAt.getValue() : null));
            close();
        });

        actionButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener((event) -> close());

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponents(cancelButton, actionButton);
        VerticalLayout bottom = new VerticalLayout();
        bottom.setMargin(false);
        bottom.addComponent(buttons);
        bottom.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
        VerticalLayout content = new VerticalLayout();
        content.addComponent(agentsComboBox);
        content.addComponent(statusesComboBox);
        if (showPortfolios) {
            content.addComponent(portfoliosComboBox);
        }
        content.addComponent(nextActionsComboBox);
        content.addComponent(nextActionAt);
        content.addComponent(bottom);
        setContent(content);
        setWidth(400, Unit.PIXELS);
        setModal(true);
        center();
    }

    private ComboBox<String> portfoliosComboBox() {
        List<String> items = getPortfolios();

        ComboBox<String> comboBox = new ComboBox<>("Debt Portfolio");
        comboBox.setItems(items);
        comboBox.setTextInputAllowed(false);
        comboBox.setEmptySelectionAllowed(false);
        if (items.contains(DEFAULT_PORTFOLIO))
            comboBox.setValue(DEFAULT_PORTFOLIO);
        comboBox.setPageLength(20);
        return comboBox;
    }

    private ComboBox<String> statusesComboBox() {
        List<String> items = dcQueries.listStatuses();
        ComboBox<String> comboBox = new ComboBox<>("Debt Status");
        comboBox.setItems(items);
        comboBox.setTextInputAllowed(false);
        comboBox.setEmptySelectionAllowed(false);
        comboBox.setValue(DEFAULT_STATUS);
        comboBox.setPageLength(20);
        return comboBox;
    }

    private List<String> getPortfolios() {
        return getPortfoliosWithout(null);
    }

    private List<String> getPortfoliosWithout(String excludePortfolio) {
        DcSettingsJson settings = dcQueries.getSettings();
        return settings.getPortfolios().stream()
            .map(Portfolio::getName)
            .filter(name -> !EXTERNAL_PORTFOLIOS.contains(name))
            .filter(name -> !name.equals(excludePortfolio))
            .collect(toList());
    }

    @Data
    @Accessors(chain = true)
    public static class EditDebtForm {
        private String agent;
        private String status;
        private String nextAction;
        private String portfolio;
        private LocalDateTime nextActionAt;
    }
}

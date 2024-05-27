package fintech.bo.spain.alfa.dc.rescheduling;

import com.google.common.collect.Iterables;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.bo.api.model.dc.LogDebtActionRequest;
import fintech.bo.api.model.dc.ReschedulingPreviewResponse;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.dc.BulkActionComponent;
import fintech.bo.components.dc.DcQueries;
import fintech.bo.components.dc.DcSettingsJson;
import fintech.bo.components.dc.NewActionComponent;
import fintech.bo.db.jooq.dc.tables.records.DebtRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static fintech.BigDecimalUtils.isPositive;
import static java.math.BigDecimal.ZERO;

public class DebtManualReschedulingComponent extends VerticalLayout implements BulkActionComponent {

    private VerticalLayout scheduleLayout;
    private HorizontalLayout footer = new HorizontalLayout();
    private DebtRecord debt;
    private List<ReschedulingItemComponent> itemComponents = newArrayList();
    private final DcQueries dcQueries;

    public DebtManualReschedulingComponent() {
        this.dcQueries = ApiAccessor.gI().get(DcQueries.class);
        setMargin(true);
    }

    @Override
    public void build(NewActionComponent actionPanel, DcSettingsJson.BulkAction bulkAction) {
        this.debt = actionPanel.getDebt();

        addComponent(createColumnHeaders());

        scheduleLayout = new VerticalLayout();
        scheduleLayout.setMargin(false);

        addComponent(scheduleLayout);

        addNewItem();
        itemChanged();

        addComponent(footer);

        buildFooter();

        Label label = new Label("All unscheduled penalties will be written off!");
        label.addStyleName(BackofficeTheme.TEXT_ACTIVE);
        label.addStyleName(ValoTheme.LABEL_BOLD);
        label.addStyleName(ValoTheme.LABEL_SMALL);
        addComponent(new Label());
        addComponent(label);
    }

    private HorizontalLayout createColumnHeaders() {
        Label dueDateLabel = new Label("Due date");
        dueDateLabel.setWidth(120, Unit.PIXELS);
        Label principalLabel = new Label("Principal");
        principalLabel.setWidth(100, Unit.PIXELS);
        Label interestLabel = new Label("Interest");
        interestLabel.setWidth(100, Unit.PIXELS);
        Label penaltyLabel = new Label("Penalty");
        penaltyLabel.setWidth(100, Unit.PIXELS);
        Label totalLabel = new Label("Total");
        totalLabel.setWidth(100, Unit.PIXELS);

        return new HorizontalLayout(dueDateLabel, principalLabel, interestLabel, penaltyLabel, totalLabel);
    }

    private void buildFooter() {
        BigDecimal principalRemaining = debt.getPrincipalOutstanding();
        BigDecimal interestRemaining = debt.getInterestOutstanding();
        BigDecimal penaltyRemaining = debt.getPenaltyOutstanding();
        BigDecimal totalRemaining = debt.getTotalOutstanding();
        for (ReschedulingItemComponent itemComponent : itemComponents) {
            ReschedulingItem item = itemComponent.getItem();
            principalRemaining = principalRemaining.subtract(item.getPrincipal());
            interestRemaining = interestRemaining.subtract(item.getInterest());
            penaltyRemaining = penaltyRemaining.subtract(item.getPenalty());
            totalRemaining = totalRemaining.subtract(item.getTotal());
        }

        Label remaining = new Label("Remaining:");
        remaining.setWidth(120, Unit.PIXELS);
        Label principal = new Label(principalRemaining.toString());
        principal.setWidth(100, Unit.PIXELS);
        Label interest = new Label(interestRemaining.toString());
        interest.setWidth(100, Unit.PIXELS);
        Label penalty = new Label(penaltyRemaining.toString());
        penalty.setWidth(100, Unit.PIXELS);
        Label total = new Label(totalRemaining.toString());
        total.setWidth(100, Unit.PIXELS);

        footer.setMargin(false);
        footer.removeAllComponents();
        footer.addComponents(remaining, principal, interest, penalty, total);
    }

    private void addNewItem() {
        ReschedulingItem reschedulingItem = new ReschedulingItem();
        if (itemComponents.isEmpty()) {
            reschedulingItem.setDueDate(TimeMachine.today().plusDays(2));
        } else {
            reschedulingItem.setDueDate(Iterables.getLast(itemComponents).getItem().getDueDate().plusDays(30));
        }

        ReschedulingItemComponent itemComponent = new ReschedulingItemComponent(
            reschedulingItem, this::itemChanged, this::addNewItem, this::removeItem
        );
        itemComponents.add(itemComponent);
        scheduleLayout.addComponent(itemComponent);
    }

    private void removeItem(ReschedulingItemComponent source) {
        if (itemComponents.size() > 1) {
            itemComponents.remove(source);
            scheduleLayout.removeComponent(source);
            itemChanged();
        }
    }

    private void itemChanged() {
        for (ReschedulingItemComponent itemComponent : itemComponents) {
            ReschedulingItem item = itemComponent.getItem();
            itemComponent.setTotal(item.getPrincipal().add(item.getInterest().add(item.getPenalty())));
        }

        buildFooter();
    }

    @Override
    public Optional<String> validate() {
        if (itemComponents.isEmpty()) {
            return Optional.of("Please generate rescheduling plan first");
        }

        return Optional.empty();
    }

    @Override
    public LogDebtActionRequest.BulkAction saveData() {
        ReschedulingPreviewResponse reschedulingPreview = new ReschedulingPreviewResponse();
        DcSettingsJson.ReschedulingSettings settings = dcQueries.getSettings().getReschedulingSettings();
        long repaymentDueDays = settings.getRepaymentDueDays();
        long gracePeriodDays = settings.getGracePeriodDays();

        for (int i = 0; i < itemComponents.size(); i++) {
            ReschedulingItem reschedulingItem = itemComponents.get(i).getItem();
            if (!isPositive(reschedulingItem.getTotal())) {
                continue;
            }

            ReschedulingPreviewResponse.Item previewItem = new ReschedulingPreviewResponse.Item();
            previewItem.setInterestScheduled(reschedulingItem.getInterest());
            previewItem.setPrincipalScheduled(reschedulingItem.getPrincipal());
            previewItem.setPenaltyScheduled(reschedulingItem.getPenalty());
            previewItem.setInstallmentSequence((long) i + 1);
            if (i == 0) {
                previewItem.setPeriodFrom(reschedulingItem.getDueDate());
                previewItem.setPeriodTo(reschedulingItem.getDueDate());
            } else {
                previewItem.setPeriodFrom(itemComponents.get(i - 1).getItem().getDueDate().plusDays(repaymentDueDays));
                previewItem.setPeriodTo(reschedulingItem.getDueDate());
            }
            previewItem.setPeriodTo(reschedulingItem.getDueDate());
            previewItem.setDueDate(reschedulingItem.getDueDate());
            previewItem.setGenerateInvoiceOnDate(null);
            previewItem.setGracePeriodInDays(gracePeriodDays);
            previewItem.setApplyPenalty(false);
            previewItem.setInterestApplied(ZERO);
            previewItem.setTotalScheduled(reschedulingItem.getTotal());

            reschedulingPreview.getItems().add(previewItem);
        }

        LogDebtActionRequest.BulkAction data = new LogDebtActionRequest.BulkAction();
        data.getParams().put("schedule", JsonUtils.writeValueAsString(reschedulingPreview));
        return data;
    }

}

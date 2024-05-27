package fintech.bo.spain.alfa.loan;

import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.renderers.LocalDateRenderer;
import fintech.TimeMachine;
import fintech.bo.api.model.dc.ReschedulingPreviewRequest;
import fintech.bo.api.model.dc.ReschedulingPreviewResponse;
import fintech.bo.components.Formats;
import fintech.bo.components.GridHelper;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dc.DcQueries;
import fintech.bo.components.dc.DcSettingsJson;
import fintech.bo.components.loan.LoanQueries;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import retrofit2.Call;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static fintech.BigDecimalUtils.goe;
import static fintech.bo.components.Formats.decimalRenderer;
import static fintech.bo.components.GridHelper.alignRightStyle;
import static java.util.stream.Collectors.toList;

public class DebtReschedulingComponent extends VerticalLayout {

    private final AlfaApiClient apiClient;
    private final DcQueries dcQueries;
    private final LoanRecord loanRecord;

    private ReschedulingPreviewResponse reschedulingPreview;
    private ComboBox<Integer> numberOfPaymentsComboBox;
    private TextField repaymentDue;
    private TextField gracePeriod;
    private VerticalLayout previewLayout;

    public DebtReschedulingComponent(Long loanId) {
        this.apiClient = ApiAccessor.gI().get(AlfaApiClient.class);
        this.dcQueries = ApiAccessor.gI().get(DcQueries.class);
        this.loanRecord = ApiAccessor.gI().get(LoanQueries.class).findById(loanId);
        build();
        setMargin(true);
    }

    private void build() {
        DcSettingsJson.ReschedulingSettings settings = dcQueries.getSettings().getReschedulingSettings();
        List<Integer> installmentOptions = IntStream.rangeClosed(settings.getMinInstallments(), settings.getMaxInstallments())
            .boxed()
            .collect(toList());
        int repaymentDueDays = settings.getRepaymentDueDays();
        int gracePeriodDays = settings.getGracePeriodDays();
        if (!reschedulingAvailable()) {
            addComponent(new Label("Rescheduling is not available"));
            addComponent(new Label("Min dpd: " + settings.getMinDpd()));
            addComponent(new Label("Min total outstanding: " + settings.getMinTotalOutstanding()));
            return;
        }
        Label loanNumber = new Label("Loan Number: " + loanRecord.getLoanNumber());
        numberOfPaymentsComboBox = new ComboBox<>("Number of payments");
        numberOfPaymentsComboBox.setItems(installmentOptions);
        numberOfPaymentsComboBox.setValue(installmentOptions.get(0));
        numberOfPaymentsComboBox.setTextInputAllowed(false);
        numberOfPaymentsComboBox.setEmptySelectionAllowed(false);

        repaymentDue = new TextField("Repayment due days");
        repaymentDue.setReadOnly(true);
        repaymentDue.setValue(Integer.toString(repaymentDueDays));

        gracePeriod = new TextField("Grace period days");
        gracePeriod.setReadOnly(true);
        gracePeriod.setValue(Integer.toString(gracePeriodDays));

        Button generate = new Button("Generate");
        generate.addClickListener(e -> generatePreview());
        HorizontalLayout top = new HorizontalLayout();
        HorizontalLayout rescheduledFields = new HorizontalLayout();
        rescheduledFields.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        top.addComponent(loanNumber);
        addComponents(top);
        rescheduledFields.addComponents(numberOfPaymentsComboBox, repaymentDue, gracePeriod, generate);
        rescheduledFields.setMargin(false);
        addComponents(rescheduledFields);

        previewLayout = new VerticalLayout();
        previewLayout.setMargin(false);
        addComponent(previewLayout);
    }

    private void generatePreview() {
        ReschedulingPreviewRequest request = new ReschedulingPreviewRequest()
            .setLoanId(loanRecord.getId())
            .setNumberOfPayments(numberOfPaymentsComboBox.getValue())
            .setWhen(TimeMachine.today());

        Call<ReschedulingPreviewResponse> call = apiClient.generateReschedulingPreview(request);
        BackgroundOperations.callApi("Generating rescheduling preview", call, response -> {
            this.reschedulingPreview = response;
            showPreview();
        }, Notifications::errorNotification);
    }

    public boolean reschedulingAvailable() {
        DcSettingsJson.ReschedulingSettings settings = dcQueries.getSettings().getReschedulingSettings();
        return loanRecord.getOverdueDays() >= settings.getMinDpd()
            && goe(loanRecord.getTotalDue(), settings.getMinTotalOutstanding());
    }

    private void showPreview() {
        Grid<ReschedulingPreviewResponse.Item> grid = new Grid<>();
        grid.addColumn(ReschedulingPreviewResponse.Item::getInstallmentSequence).setCaption("Sequence");
        grid.addColumn(ReschedulingPreviewResponse.Item::getDueDate).setCaption("Due date").setRenderer(new LocalDateRenderer(Formats.DATE_FORMAT));
        grid.addColumn(ReschedulingPreviewResponse.Item::getTotalScheduled).setCaption("Total").setRenderer(decimalRenderer()).setStyleGenerator(alignRightStyle()).setId("totalScheduled");
        grid.addColumn(ReschedulingPreviewResponse.Item::getPrincipalScheduled).setCaption("Principal").setRenderer(decimalRenderer()).setStyleGenerator(alignRightStyle());
        grid.addColumn(ReschedulingPreviewResponse.Item::getInterestScheduled).setCaption("Interest").setRenderer(decimalRenderer()).setStyleGenerator(alignRightStyle());
        grid.addColumn(ReschedulingPreviewResponse.Item::getPenaltyScheduled).setCaption("Penalty").setRenderer(decimalRenderer()).setStyleGenerator(alignRightStyle());
        grid.addColumn(item -> Optional.ofNullable(item.getFeeItems())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(ReschedulingPreviewResponse.FeeItem::getAmountScheduled)
            .reduce(BigDecimal::add)
            .orElse(BigDecimal.ZERO))
            .setCaption("Fee")
            .setRenderer(decimalRenderer())
            .setStyleGenerator(alignRightStyle());

        BigDecimal totalScheduled = totalScheduled();
        FooterRow footer = grid.appendFooterRow();
        FooterCell totalCell = footer.getCell("totalScheduled");
        totalCell.setStyleName(GridHelper.ALIGN_RIGHT_STYLE);
        totalCell.setText(Formats.decimalFormat().format(totalScheduled));
        grid.setItems(reschedulingPreview.getItems());

        grid.setWidth(100, Unit.PERCENTAGE);
        grid.setHeightByRows(numberOfPaymentsComboBox.getValue());

        previewLayout.removeAllComponents();
        previewLayout.addComponent(grid);
    }

    private BigDecimal totalScheduled() {
        return reschedulingPreview.getItems().stream()
            .map(ReschedulingPreviewResponse.Item::getTotalScheduled)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Optional<String> validate() {
        if (reschedulingPreview == null) {
            return Optional.of("Please generate rescheduling plan first");
        } else {
            return Optional.empty();
        }
    }

    public ReschedulingPreviewResponse getReschedulingPreview() {
        return reschedulingPreview;
    }
}

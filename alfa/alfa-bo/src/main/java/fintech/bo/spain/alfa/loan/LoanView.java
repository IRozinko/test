package fintech.bo.spain.alfa.loan;

import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import fintech.TimeMachine;
import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.loan.AbstractLoanView;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.bo.spain.alfa.api.ExtensionDiscountApiClient;
import fintech.bo.spain.alfa.loan.discount.ExtensionDiscountTab;
import fintech.bo.spain.alfa.loan.rescheduling.ReschedulingLoanTab;
import fintech.bo.spain.alfa.viventor.ViventorComponents;
import fintech.bo.spain.asnef.AsnefComponents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import java.time.temporal.ChronoUnit;

@Slf4j
@SpringView(name = AbstractLoanView.NAME)
public class LoanView extends AbstractLoanView {

    @Autowired
    private ViventorComponents viventorComponents;

    @Autowired
    private AlfaApiClient alfaApiClient;

    @Autowired
    private PaymentSimulationComponentProvider paymentSimulation;

    @Autowired
    private AsnefComponents asnefComponents;

    @Autowired
    private LoanCertificateComponents loanCertificateComponents;

    @Autowired
    protected ExtensionDiscountApiClient extensionDiscountApiClient;

    public LoanView(AlfaLoanComponents alfaLoanComponents) {
        super(alfaLoanComponents);
    }

    @Override
    protected void addTabsBefore(BusinessObjectLayout layout) {
//        layout.addTab("Payment simulation", this::overview);
    }

    @Override
    protected void addTabsAfter(BusinessObjectLayout layout) {
    }

    @Override
    protected void addCustomActions(BusinessObjectLayout layout) {
        if (LoginService.hasPermission(BackofficePermissions.LOAN_RENOUNCE)) {
            if (loan.getStatusDetail().equals(LoanConstants.LOAN_STATUS_DETAIL_ACTIVE) && ChronoUnit.DAYS.between(loan.getIssueDate(), TimeMachine.today()) <= 14) {
                layout.addActionMenuItem("Renounce loan", event -> {
                    RenounceLoanDialog dialog = new RenounceLoanDialog(loanId, alfaApiClient);
                    dialog.addCloseListener(e -> refresh());
                    UI.getCurrent().addWindow(dialog);
                });
            }
        }
        if (LoginService.hasPermission(BackofficePermissions.LOAN_RESCHEDULE)) {
            if (loan.getStatusDetail().equals(LoanConstants.LOAN_STATUS_DETAIL_ACTIVE)) {
                layout.addActionMenuItem("Reschedule loan", event -> {
                    RescheduleLoanDialog dialog = new RescheduleLoanDialog(loanId, alfaApiClient);
                    dialog.addCloseListener(e -> refresh());
                    UI.getCurrent().addWindow(dialog);
                });
            }
        }
        if (LoginService.hasPermission(BackofficePermissions.LOAN_BREAK_RESCHEDULE)) {
            if (loan.getStatusDetail().equals(LoanConstants.LOAN_STATUS_DETAIL_RESCHEDULED)) {
                layout.addActionMenuItem("Cancel reschedule", event -> {
                    Dialogs.confirm("Break rescheduled loan", (Button.ClickListener) e -> {
                        IdRequest request = new IdRequest(loanId);
                        Call<Void> call = alfaApiClient.breakRescheduledLoan(request);
                        BackgroundOperations.callApi("Breaking rescheduled loan",
                            call, t -> {
                                Notifications.trayNotification("Rescheduled loan has been broken");
                                refresh();
                            }, Notifications::errorNotification);
                    });
                });
            }
        }
        if (LoginService.hasPermission(BackofficePermissions.ADMIN)) {
            layout.addActionMenuItem("Generate penalty", event -> {
                Call<Void> call = alfaApiClient.generatePenalty(new IdRequest(loanId));
                BackgroundOperations.callApi("Generating penalty", call, t -> {
                    Notifications.trayNotification("Done");
                    refresh();
                }, Notifications::errorNotification);
            });
        }

        addCertificateButtons(layout);
    }

    private Component extensionDiscounts() {
        return new ExtensionDiscountTab(loanId, db, extensionDiscountApiClient);
    }

    private Component loanRescheduling() {
        return new ReschedulingLoanTab(loanId, db);
    }

    private void addCertificateButtons(BusinessObjectLayout layout) {
        loanCertificateComponents.getMenuItems(loan)
            .forEach(i -> layout.addActionMenuItem(i.getCaption(), i.getCommand()));
    }

    private Component overview() {
        BoComponent component = paymentSimulation.build(new BoComponentContext().withScope(StandardScopes.SCOPE_LOAN, this.loanId));
        component.refresh();
        return component;
    }
}

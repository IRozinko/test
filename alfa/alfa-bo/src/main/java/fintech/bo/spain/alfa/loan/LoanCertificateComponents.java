package fintech.bo.spain.alfa.loan;

import com.vaadin.ui.MenuBar;
import fintech.bo.components.menu.MenuItem;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import fintech.bo.spain.alfa.api.LoanCertificateApiClient;
import fintech.spain.alfa.bo.model.LoanCertificateRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fintech.TimeMachine.today;

@Component
@RequiredArgsConstructor
public class LoanCertificateComponents {

    private final LoanCertificateApiClient apiClient;

    public List<MenuItem> getMenuItems(LoanRecord loan) {
        return Stream.of(Certificate.values())
            .filter(cert -> cert.getCondition().test(loan))
            .map(cert -> new MenuItem(cert.getCaption(), generateCertificateCommand(loan.getId(), cert.getType())))
            .collect(Collectors.toList());
    }

    private MenuBar.Command generateCertificateCommand(Long loanId, String certificateType) {
        return event -> {
            try {
                apiClient.generateCertificate(loanId, new LoanCertificateRequest(certificateType)).execute();
                Notifications.trayNotification("The certificate was saved to client attachments.");
            } catch (IOException e) {
                Notifications.errorNotification("Error: " + e.getMessage());
            }
        };
    }

    private static boolean isOpen(LoanRecord loan) {
        return loan.getCloseDate() == null;
    }

    private static boolean isLoanBeforeEndOfTerm(LoanRecord loan) {
        LocalDate today = today();
        return isOpen(loan) && today.isAfter(loan.getIssueDate())
            && today.isBefore(loan.getIssueDate().plusDays(loan.getPeriodCount()));
    }

    private static boolean isDebt(LoanRecord loan) {
        return isOpen(loan) && today().isAfter(loan.getMaturityDate());
    }

    @Getter
    @AllArgsConstructor
    private enum Certificate {

        EARLY_REPAYMENT(
            "Certificate of early repayment", "EARLY_REPAYMENT", LoanCertificateComponents::isLoanBeforeEndOfTerm
        ),

        DEBT(
            "Certificate of debt", "DEBT", LoanCertificateComponents::isDebt
        );

        private String caption;
        private String type;
        private Predicate<LoanRecord> condition;

    }
}

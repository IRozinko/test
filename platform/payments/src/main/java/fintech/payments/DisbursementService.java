package fintech.payments;

import fintech.payments.commands.AddDisbursementCommand;
import fintech.payments.model.Disbursement;
import fintech.payments.model.DisbursementExportResult;
import fintech.payments.model.DisbursementStatusDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Validated
public interface DisbursementService {

    Long add(@Valid AddDisbursementCommand command);

    void exported(long disbursementId, LocalDateTime when, DisbursementExportResult result);

    void exportError(long disbursementId, String error);

    void error(long disbursementId, String error);

    void settled(long disbursementId);

    void revertSettled(long disbursementId);

    void cancel(long disbursementId, String error);

    void voidDisbursement(long disbursementId, String error);

    void invalid(long disbursementId, String error);

    void pending(long disbursementId);

    Disbursement getDisbursement(long disbursementId);

    List<Disbursement> findDisbursements(DisbursementQuery query);

    Optional<Disbursement> getOptional(DisbursementQuery query);

    String generateReference(String prefix, String suffix, int length);

    @Data
    @NoArgsConstructor // TODO Remove
    @AllArgsConstructor
    @Builder
    class DisbursementQuery {
        private Long institutionId;
        private Long institutionAccountId;
        private Long loanId;
        private DisbursementStatusDetail statusDetail;
        private Long clientId;
        private String reference;
        private String fileName;
        private BigDecimal amount;

        public static DisbursementQuery byLoan(long loanId, DisbursementStatusDetail status) {
            return DisbursementQuery.builder()
                .loanId(loanId)
                .statusDetail(status)
                .build();
        }

        public static DisbursementQuery byLoanAndAmount(long loanId, DisbursementStatusDetail status, BigDecimal amount) {
            return DisbursementQuery.builder()
                .loanId(loanId)
                .statusDetail(status)
                .amount(amount)
                .build();
        }

        public static DisbursementQuery byClient(long clientId, DisbursementStatusDetail status) {
            return DisbursementQuery.builder()
                .clientId(clientId)
                .statusDetail(status)
                .build();
        }

        public static DisbursementQuery byReference(String reference) {
            return DisbursementQuery.builder()
                .reference(reference)
                .build();
        }

        public static DisbursementQuery byFileName(String fileName, DisbursementStatusDetail status) {
            return DisbursementQuery.builder()
                .fileName(fileName)
                .statusDetail(status)
                .build();
        }

        public static DisbursementQuery pending(Long institutionId, Long institutionAccountId) {
            return DisbursementQuery.builder()
                .institutionId(institutionId)
                .institutionAccountId(institutionAccountId)
                .statusDetail(DisbursementStatusDetail.PENDING)
                .build();
        }
    }
}

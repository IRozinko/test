package fintech.instantor.db;

import fintech.db.BaseEntity;
import fintech.instantor.model.InstantorProcessingStatus;
import fintech.instantor.model.InstantorResponse;
import fintech.instantor.model.InstantorResponseAttributes;
import fintech.instantor.model.InstantorResponseStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.safeAmount;

@Getter
@Setter
@Entity
@Table(name = "response", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_response_client_id"),
})
@OptimisticLocking(type = OptimisticLockType.NONE)
@DynamicUpdate
public class InstantorResponseEntity extends BaseEntity {

    private Long clientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstantorResponseStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstantorProcessingStatus processingStatus;

    private String payloadJson;

    private String error;

    private String paramMessageId;

    private String paramSource;

    private String paramTimestamp;

    private boolean latest;

    private String personalNumberForVerification;

    private String nameForVerification;

    private String accountNumbers;

    @OptimisticLock(excluded = true)
    @ElementCollection
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(name = "response_attribute", joinColumns = @JoinColumn(name = "response_id"), schema = Entities.SCHEMA)
    private Map<String, String> attributes = new HashMap<>();

    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstantorAccountEntity> accounts;

    public InstantorResponse toValueObject() {
        InstantorResponse val = new InstantorResponse();
        val.setId(this.getId());
        val.setClientId(this.getClientId());
        val.setCreatedAt(this.getCreatedAt());
        val.setStatus(this.getStatus());
        val.setAccountNumbers(this.getAccountNumbers());
        val.setPersonalNumberForVerification(this.getPersonalNumberForVerification());
        val.setNameForVerification(this.getNameForVerification());
        val.setAverageAmountOfIncomingTransactionsPerMonth(safeAmount(attributes.get(InstantorResponseAttributes.AVERAGE_AMOUNT_OF_INCOMING_TRANSACTIONS_PER_MONTH.name())));
        val.setAverageAmountOfOutgoingTransactionsPerMonth(safeAmount(attributes.get(InstantorResponseAttributes.AVERAGE_AMOUNT_OF_OUTGOING_TRANSACTIONS_PER_MONTH.name())));
        val.setAverageMinimumBalancePerMonth(safeAmount(attributes.get(InstantorResponseAttributes.AVERAGE_MINIMUM_BALANCE_PER_MONTH.name())));
        val.setLastMonthAmountOfLoans(safeAmount(attributes.get(InstantorResponseAttributes.LAST_MONTH_AMOUNT_OF_LOANS.name())));
        val.setThisMonthAmountOfLoans(safeAmount(attributes.get(InstantorResponseAttributes.THIS_MONTH_AMOUNT_OF_LOANS.name())));
        val.setAverageNumberOfTransactionsPerMonth(safeAmount(attributes.get(InstantorResponseAttributes.AVERAGE_AMOUNT_OF_TRANSACTIONS_PER_MONTH.name())));
        val.setMonthsAvailable(safeInteger(attributes.get(InstantorResponseAttributes.MONTHS_AVAILABLE.name())));
        val.setTotalNumberOfTransactions(safeInteger((attributes.get(InstantorResponseAttributes.TOTAL_NUMBER_OF_TRANSACTIONS.name()))));
        val.setBankName(attributes.get(InstantorResponseAttributes.BANK_NAME.name()));
        val.setAccounts(accounts.stream().map(InstantorAccountEntity::toValueObject).collect(Collectors.toList()));
        val.setAttributes(attributes);
        return val;
    }

    private Integer safeInteger(String number) {
        if (number != null) {
            return Integer.valueOf(number);
        } else {
            return null;
        }
    }
}

package fintech.spain.unnax.db;

import fintech.TimeMachine;
import fintech.db.BaseEntity;
import fintech.spain.unnax.transfer.model.TransferAutoRequest;
import fintech.spain.unnax.transfer.model.TransferAutoType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "transfer_auto", schema = Entities.SCHEMA)
public class TransferAutoEntity extends BaseEntity {

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String destinationAccount;

    @Column(nullable = false)
    private String customerCode;

    @Column(nullable = false, unique = true)
    private String orderCode;

    private String currency;

    private String customerNames;
    private String concept;
    private String bankOrderCode;

    @Enumerated(EnumType.STRING)
    private TransferAutoType transferType;

    private String tags;

    @Enumerated(EnumType.STRING)
    private TransferAutoStatus status;

    private String sourceAccount;
    private String errorDetails;

    private LocalDateTime orderPendingAt;
    private LocalDateTime orderCreatedAt;
    private LocalDateTime orderProcessedAt;

    public TransferAutoEntity(TransferAutoRequest request) {
        super();
        this.amount = request.getAmount();
        this.destinationAccount = request.getDestinationAccount();
        this.customerCode = request.getCustomerCode();
        this.orderCode = request.getOrderCode();
        this.currency = request.getCurrency();
        this.customerNames = request.getCustomerNames();
        this.concept = request.getConcept();
        this.bankOrderCode = request.getBankOrderCode();
        this.transferType = TransferAutoType.findByNumeric(request.getTransferType());
        this.tags = String.join(", ", request.getTags());
        this.sourceAccount = request.getSourceAccount();
        this.status = TransferAutoStatus.PENDING;
        this.orderPendingAt = TimeMachine.now();
    }
}

package fintech.spain.unnax.db;

import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "payment_with_transfer_authorized", schema = Entities.SCHEMA)
@NoArgsConstructor
public class PaymentWithTransferAuthorizedEntity extends BaseEntity {

    private String orderCode;
    private String bankOrderCode;
    private BigDecimal amount;
    private String currency;
    private String customerCode;
    private String customerNames;
    private String service;
    private String status;
    private boolean success;
    private String errorMessages;
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime date;
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime processedAt;

}

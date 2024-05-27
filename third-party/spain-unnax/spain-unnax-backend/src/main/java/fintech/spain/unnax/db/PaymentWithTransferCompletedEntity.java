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
@Table(name = "payment_with_transfer_completed", schema = Entities.SCHEMA)
@NoArgsConstructor
public class PaymentWithTransferCompletedEntity extends BaseEntity {

    private String customerCode;
    private String orderCode;
    private String bankOrderCode;
    private BigDecimal amount;
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime date;
    private Boolean success;
    private String signature;
    private Boolean result;
    private String accountNumber;
    private String status;
    private String service;
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime processedAt;

}

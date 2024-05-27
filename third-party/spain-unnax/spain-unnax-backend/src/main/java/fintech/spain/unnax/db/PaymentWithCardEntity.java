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
@Table(name = "payment_with_card", schema = Entities.SCHEMA)
@NoArgsConstructor
public class PaymentWithCardEntity extends BaseEntity {

    private String pan;
    private String bin;
    private String currency;
    private String transactionType;
    private String expirationDate;
    private Integer expireMonth;
    private Integer expireYear;
    private String cardHolder;
    private String cardBrand;
    private String cardType;
    private String cardCountry;
    private String cardBank;
    private String orderCode;
    private String token;
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime date;
    private BigDecimal amount;
    private String concept;
    private Integer state;
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime processedAt;

}

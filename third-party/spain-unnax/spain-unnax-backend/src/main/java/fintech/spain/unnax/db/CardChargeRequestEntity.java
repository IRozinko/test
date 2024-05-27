package fintech.spain.unnax.db;

import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Entity
@Table(name = "card_charge_request", schema = Entities.SCHEMA)
@NoArgsConstructor
public class CardChargeRequestEntity extends BaseEntity {

    @NotNull
    private Long clientId;

    @NotEmpty
    @Length(min = 6, max = 13)
    private String orderCode;

    @NotNull
    private Integer amount;

    @NotEmpty
    private String concept;

    @NotEmpty
    private String cardHash;

    @NotEmpty
    private String cardHashReference;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CardChargeStatus status;

    private String error;

}

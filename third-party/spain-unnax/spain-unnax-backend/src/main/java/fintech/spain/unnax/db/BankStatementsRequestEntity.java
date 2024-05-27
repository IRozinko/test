package fintech.spain.unnax.db;

import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "bank_statements_request", schema = Entities.SCHEMA)
@NoArgsConstructor
public class BankStatementsRequestEntity extends BaseEntity {

    @NotNull
    private LocalDate fromDate;

    @NotNull
    private LocalDate toDate;

    @NotEmpty
    private String iban;

    @NotEmpty
    private String requestCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BankStatementsRequestStatus status;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime processedAt;

    private String error;

}

package fintech.payments.db;

import fintech.db.BaseEntity;
import fintech.payments.model.Statement;
import fintech.payments.model.StatementStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "statement", schema = Entities.SCHEMA)
public class StatementEntity extends BaseEntity {

    private Long institutionId;

    private String format;

    @Column(nullable = false)
    private Long fileId;
    private String fileName;

    @Column(columnDefinition = "DATE")
    private LocalDate startDate;

    @Column(columnDefinition = "DATE")
    private LocalDate endDate;

    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatementStatus status;

    private String error;


    public Statement toValueObject() {
        Statement statement = new Statement();
        statement.setId(this.getId());
        statement.setFileId(this.getFileId());
        statement.setFileName(this.getFileName());
        statement.setInstitutionId(this.getInstitutionId());
        statement.setStartDate(this.getStartDate());
        statement.setEndDate(this.getEndDate());
        statement.setAccountNumber(this.getAccountNumber());
        statement.setStatus(this.getStatus());
        statement.setError(this.getError());
        return statement;
    }

}

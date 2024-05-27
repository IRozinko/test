package fintech.spain.asnef.db;

import fintech.db.BaseEntity;
import fintech.spain.asnef.LogRow;
import fintech.spain.asnef.LogRowStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true, exclude = "log")
@Entity
@Table(name = "log_row", schema = Entities.SCHEMA)
public class LogRowEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogRowStatus status;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Long loanId;

    @Column(nullable = false)
    private String operationIdentifier;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String outgoingRow;

    private String outgoingHolderRow;

    private String outgoingAddressRow;

    private String incomingRow;

    @ManyToOne(optional = false)
    @JoinColumn(name = "log_id")
    private LogEntity log;

    public LogRow toValueObject() {
        LogRow row = new LogRow();
        row.setId(id);
        row.setStatus(status);
        row.setClientId(clientId);
        row.setLoanId(loanId);
        row.setOperationIdentifier(operationIdentifier);
        row.setNumber(number);
        row.setOutgoingRow(outgoingRow);
        row.setOutgoingHolderRow(outgoingHolderRow);
        row.setOutgoingAddressRow(outgoingAddressRow);
        row.setIncomingRow(incomingRow);

        return row;
    }
}

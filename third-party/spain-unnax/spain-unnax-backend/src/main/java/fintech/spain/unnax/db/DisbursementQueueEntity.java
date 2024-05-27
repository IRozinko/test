package fintech.spain.unnax.db;

import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "disbursement_queue", schema = Entities.SCHEMA)
public class DisbursementQueueEntity extends BaseEntity {

    private long disbursementId;

    @Enumerated(EnumType.STRING)
    private DisbursementQueueStatus status;

    private int attempts = 0;

}

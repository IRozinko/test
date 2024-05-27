package fintech.spain.alfa.product.loc.db;


import fintech.db.BaseEntity;
import fintech.spain.alfa.product.db.Entities;
import fintech.spain.alfa.product.loc.LocBatchStatus;
import fintech.spain.alfa.product.loc.LocBatchStatusDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
@Entity
@Table(name = "loc_batch", schema = Entities.SCHEMA, indexes = { @Index(columnList = "clientId")})
public class LocBatchEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private Long clientId;

    @Column(nullable = false)
    private String clientNumber;

    private Long applicationId;

    @Column(nullable = false)
    private Long batchNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LocBatchStatus status;

    @Enumerated(EnumType.STRING)
    private LocBatchStatusDetail statusDetail;

}

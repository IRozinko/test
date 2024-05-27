package fintech.iovation.db;

import com.google.common.collect.ImmutableMap;
import fintech.db.BaseEntity;
import fintech.iovation.model.IovationStatus;
import fintech.iovation.model.IovationTransaction;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OptimisticLock;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "transaction", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_transaction_client_id"),
})
public class IovationTransactionEntity extends BaseEntity {

    private Long clientId;

    private Long applicationId;

    @Column(nullable = false)
    private String ipAddress;

    private String blackBox;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IovationStatus status;

    private String endBlackBox;
    private String result;
    private String reason;
    private String trackingNumber;
    private String deviceId;

    private String error;

    @OptimisticLock(excluded = true)
    @ElementCollection
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(name = "transaction_detail", joinColumns = @JoinColumn(name = "transaction_id"), schema = Entities.SCHEMA)
    private Map<String, String> details = new HashMap<>();


    public IovationTransaction toValueObject() {
        IovationTransaction val = new IovationTransaction();
        val.setId(this.id);
        val.setClientId(this.clientId);
        val.setApplicationId(this.applicationId);
        val.setIpAddress(this.ipAddress);
        val.setBlackBox(this.blackBox);
        val.setStatus(this.status);
        val.setEndBlackBox(this.endBlackBox);
        val.setResult(this.result);
        val.setReason(this.reason);
        val.setTrackingNumber(this.trackingNumber);
        val.setDeviceId(this.deviceId);
        val.setError(this.error);
        val.setDetails(ImmutableMap.copyOf(this.details));
        return val;
    }
}

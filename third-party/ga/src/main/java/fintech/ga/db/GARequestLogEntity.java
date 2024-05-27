package fintech.ga.db;


import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@ToString(callSuper = true)
@Table(name = "requests_log", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_requests_log_client_id")
})
public class GARequestLogEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    private String request;
    private String response;
    private Integer responseCode;

}

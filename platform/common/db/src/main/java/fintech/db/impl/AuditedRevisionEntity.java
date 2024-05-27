package fintech.db.impl;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "revision", schema = "common")
@RevisionEntity(AuditingRevisionListener.class)
public class AuditedRevisionEntity {

    @RevisionNumber
    @Id
    @GeneratedValue(generator = "sequence")
    @GenericGenerator(
        name = "sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "rev_id_seq"),
            @org.hibernate.annotations.Parameter(name = "schema", value = "common"),
            @org.hibernate.annotations.Parameter(name = "initial_value", value = "1000"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
        }
    )
    private int id;

    @RevisionTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date revisionAt;

    @Column(nullable = false)
    private String userName;

    private String requestId;

    private String requestUri;

    private String ipAddress;

}

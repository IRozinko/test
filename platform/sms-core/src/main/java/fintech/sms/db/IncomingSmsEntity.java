package fintech.sms.db;


import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "incoming", schema = Entities.SCHEMA)
@DynamicUpdate
public class IncomingSmsEntity extends BaseEntity {

    private String source;
    private String phoneNumber;
    private String text;
    private String rawDataJson;
}

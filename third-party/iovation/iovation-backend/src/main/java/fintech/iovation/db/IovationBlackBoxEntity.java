package fintech.iovation.db;

import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "blackbox", schema = Entities.SCHEMA)
public class IovationBlackBoxEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String blackBox;

    private Long loanApplicationId;

}

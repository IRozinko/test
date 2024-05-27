package fintech.admintools.db;

import fintech.admintools.AdminActionStatus;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "log", schema = Entities.SCHEMA)
@OptimisticLocking(type = OptimisticLockType.NONE)
@DynamicUpdate
public class AdminActionLogEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminActionStatus status;

    private String message;

    private String error;

    private String params;
}

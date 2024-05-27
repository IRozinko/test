package fintech.accounting.db;


import fintech.accounting.Account;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "account", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "code", name = "idx_account_code"),
})
@Audited
@AuditOverride(forClass = BaseEntity.class)
public class AccountEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;


    public Account toValueObject() {
        Account val = new Account();
        val.setId(this.id);
        val.setCode(this.code);
        val.setName(this.name);
        return val;
    }
}

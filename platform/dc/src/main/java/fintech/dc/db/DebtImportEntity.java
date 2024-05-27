package fintech.dc.db;

import fintech.db.BaseEntity;
import fintech.dc.model.DebtImport;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "debt_import", schema = Entities.SCHEMA)
public class DebtImportEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String code;

    private String debtImportFormat;

    @Column(nullable = false)
    private boolean disabled = false;

    public DebtImport toValueObject() {
        DebtImport val = new DebtImport();
        val.setId(this.id);
        val.setName(this.name);
        val.setCode(this.code);
        val.setDisabled(this.disabled);
        val.setDebtImportFormat(this.debtImportFormat);
        return val;
    }
}

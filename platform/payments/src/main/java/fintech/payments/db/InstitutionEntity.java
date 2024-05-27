package fintech.payments.db;

import fintech.db.BaseEntity;
import fintech.payments.model.Institution;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString(callSuper = true, exclude = {"accounts"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "institution", schema = Entities.SCHEMA)
public class InstitutionEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String code;

    @Column(nullable = false)
    private String institutionType;

    @Column(nullable = false, name = "is_primary")
    private boolean primary;

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<InstitutionAccountEntity> accounts = new ArrayList<>();

    private String paymentMethods;

    private String statementImportFormat;

    private String statementExportFormat;

    private String statementApiExporter;

    private String statementExportParamsJson;

    @Column(nullable = false)
    private boolean disabled = false;

    public Institution toValueObject() {
        Institution val = new Institution();
        val.setId(this.id);
        val.setInstitutionType(this.institutionType);
        val.setName(this.name);
        val.setCode(this.code);
        val.setPrimary(this.primary);
        val.setDisabled(this.disabled);
        val.setStatementImportFormat(this.statementImportFormat);
        val.setStatementExportFormat(this.statementExportFormat);
        val.setStatementApiExporter(this.statementApiExporter);
        val.setAccounts(this.accounts.stream().map(InstitutionAccountEntity::toValueObject).collect(Collectors.toList()));
        val.setStatementExportParamsJson(this.statementExportParamsJson);
        return val;
    }
}

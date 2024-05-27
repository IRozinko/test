package fintech.spain.alfa.product.db;

import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "identification_document", schema = Entities.SCHEMA)
@DynamicUpdate
public class IdentificationDocumentEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    @Column
    private Long taskId;

    @Column(nullable = false)
    private String documentType;

    @Column(nullable = false)
    private String documentNumber;

    @Column(name = "surname_1")
    private String surname1;

    @Column(name = "surname_2")
    private String surname2;

    private String name;

    private String gender;

    private String nationality;

    @Column(columnDefinition = "DATE")
    private LocalDate dateOfBirth;

    @Column(columnDefinition = "DATE")
    private LocalDate expirationDate;

    private String street;

    private String house;

    private String city;

    private String province;

    private String placeOfBirth;

    @Column(nullable = false)
    private Long frontFileId;

    @Column(nullable = false)
    private String frontFileName;

    private Long backFileId;

    private String backFileName;

    private String customerServiceAssessment;

    @Column(name = "is_valid")
    private boolean isValid;

    private LocalDateTime validatedAt;
}

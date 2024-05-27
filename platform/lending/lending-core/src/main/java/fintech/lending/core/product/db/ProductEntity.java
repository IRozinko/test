package fintech.lending.core.product.db;

import fintech.lending.core.db.Entities;
import fintech.lending.core.product.Product;
import fintech.lending.core.product.ProductType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id")
@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
@Table(name = "product", schema = Entities.SCHEMA)
public class ProductEntity {

    @Id
    protected Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    @Version
    @Column(nullable = false)
    protected Long entityVersion;

    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @Column(nullable = false)
    protected LocalDateTime updatedAt;

    @CreatedBy
    protected String createdBy;

    @LastModifiedBy
    protected String updatedBy;

    @Column(nullable = false)
    private String defaultSettingsJson;

    @PrePersist
    private void created() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void updated() {
        this.updatedAt = LocalDateTime.now();
    }

    public Product toValueObject() {
        Product product = new Product();
        product.setProductType(this.productType);
        return product;
    }
}

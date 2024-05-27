package fintech.decision.db;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import fintech.db.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "scoring_value_usage", schema = Entities.SCHEMA)
@TypeDefs({
    @TypeDef(
        name = "string-array",
        typeClass = StringArrayType.class
    )
})
public class ScoringValueUsageEntity extends BaseEntity {

    @Column(nullable = false)
    private Long decisionEngineRequestId;

    @Type(type = "string-array")
    @Column(
        name = "scoring_keys",
        columnDefinition = "text[]"
    )
    private String[] scoringKeys;

}

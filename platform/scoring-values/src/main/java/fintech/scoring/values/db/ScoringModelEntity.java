package fintech.scoring.values.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import fintech.JsonUtils;
import fintech.db.BaseEntity;
import fintech.scoring.values.model.ScoringModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.collections.ListUtils;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true, of = {"clientId"})
@Entity
@Table(name = "scoring_model", schema = Entities.SCHEMA,
    indexes = {
        @Index(columnList = "clientId", name = "idx_scoring_model_client_id")
    })
@Accessors(chain = true)
@NoArgsConstructor
@TypeDef(
    name = "jsonb-node",
    typeClass = JsonNodeBinaryType.class
)
public class ScoringModelEntity extends BaseEntity {

    public ScoringModelEntity(Long clientId) {
        this.clientId = clientId;
    }

    @Column(nullable = false)
    private Long clientId;

    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb", name = "values")
    private JsonNode values;

    public void addValues(List<ScoringValueData> values) {
        setValues(ListUtils.union(values, getValues()));
    }

    public void setValues(List<ScoringValueData> values) {
        this.values = JsonUtils.toJsonNode(values);
    }

    public List<ScoringValueData> getValues() {
        return JsonUtils.readValue(values, new TypeReference<List<ScoringValueData>>() {
        });
    }

    public ScoringModel toValue() {
        return new ScoringModel(id, clientId);
    }
}

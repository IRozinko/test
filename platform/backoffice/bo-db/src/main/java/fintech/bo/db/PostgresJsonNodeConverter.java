package fintech.bo.db;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import fintech.JsonUtils;
import org.jooq.Converter;

public class PostgresJsonNodeConverter implements Converter<Object, JsonNode> {
    @Override
    public JsonNode from(Object t) {
        return t == null
                ? null
                : (t instanceof String) ? JsonUtils.toJsonNode((String) t) : JsonUtils.toJsonNode(t);
    }

    @Override
    public Object to(JsonNode u) {
        return u == null || u.equals(NullNode.instance)
                ? null
                : JsonUtils.writeValueAsString(u);
    }

    @Override
    public Class<Object> fromType() {
        return Object.class;
    }

    @Override
    public Class<JsonNode> toType() {
        return JsonNode.class;
    }
}

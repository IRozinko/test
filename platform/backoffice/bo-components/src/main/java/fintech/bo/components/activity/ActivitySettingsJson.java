package fintech.bo.components.activity;

import fintech.Validate;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class ActivitySettingsJson {

    private List<String> topics = new ArrayList<>();
    private List<AgentAction> agentActions = new ArrayList<>();

    @Data
    public static class AgentAction {
        private String name;
        private List<BulkAction> bulkActions = new ArrayList<>();
        private List<String> resolutions = new ArrayList<>();
        private List<String> userRoles = new ArrayList<>();
        private List<String> topics = new ArrayList<>();
    }

    @Data
    public static class BulkAction {
        private String type;
        private Map<String, Object> params = new HashMap<>();

        public <T> T getRequiredParam(String name, Class<T> paramClass) {
            Object value = params.get(name);
            Validate.notNull(value, "Param not found by name [%s] in bulk action [%s]", name, type);
            assertParamClass(name, paramClass, value);
            return (T) value;
        }

        public <T> Optional<T> getParam(String name, Class<T> paramClass) {
            Object value = params.get(name);
            if (value == null) {
                return Optional.empty();
            }
            assertParamClass(name, paramClass, value);
            return Optional.of((T) value);
        }

        private <T> void assertParamClass(String name, Class<T> paramClass, Object value) {
            Validate.isAssignableFrom(paramClass, value.getClass(), "Invalid param [%s] class [%s], expected [%s], bulk action [%s]", name, value.getClass().getSimpleName(), paramClass.getSimpleName(), type);
        }
    }
}

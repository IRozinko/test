package fintech.bo.components.dc;

import com.fasterxml.jackson.annotation.JsonInclude;
import fintech.Validate;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class DcSettingsJson {

    private List<Portfolio> portfolios = new ArrayList<>();

    private Map<String, Trigger> triggerTemplates = new LinkedHashMap<>();

    private List<AgingBucket> agingBuckets = new ArrayList<>();

    private Map<String, AgentAction> agentActionTemplates = new LinkedHashMap<>();

    private Map<String, AgentActionStatus> agentActionStatusTemplates = new LinkedHashMap<>();

    private Companies companies = new Companies();

    private ReschedulingSettings reschedulingSettings = new ReschedulingSettings();

    private ExtensionSettings extensionSettings = new ExtensionSettings();

    @Data
    @Accessors(chain = true)
    public static class Portfolio {
        private String name;

        private String initialStatus;

        private List<Status> statuses = new ArrayList<>();

        private List<Trigger> triggers = new ArrayList<>();

        private List<AgentAction> agentActions = new ArrayList<>();
    }

    @Data
    @Accessors(chain = true)
    public static class Status {
        private String name;
        private int priority;
    }

    @Data
    @Accessors(chain = true)
    public static class AgingBucket {
        private long dpdFrom;
        private long dpdTo;
        private String name;
    }

    @Data
    @Accessors(chain = true)
    public static class Trigger {
        private String name;
        private String template;
        private List<Action> actions = new ArrayList<>();
        private List<Condition> conditions = new ArrayList<>();
    }

    @Data
    @Accessors(chain = true)
    public static class Action {
        private String type;
        private Map<String, Object> params = new HashMap<>();
    }

    @Data
    @Accessors(chain = true)
    public static class Condition {
        private String type;
        private Map<String, Object> params = new HashMap<>();
    }

    @Data
    @Accessors(chain = true)
    public static class AgentAction {
        private String template;
        private String type;
        private List<BulkAction> bulkActions = new ArrayList<>();
        private List<AgentActionStatus> statuses = new ArrayList<>();
        private List<String> resolutions = new ArrayList<>();
        private List<String> userRoles = new ArrayList<>();
    }

    @Data
    @Accessors(chain = true)
    public static class AgentActionStatus {
        private String template;
        private String name;
        private String defaultResolution;
        private List<AgentNextAction> nextActions = new ArrayList<>();
        private List<BulkAction> bulkActions = new ArrayList<>();
        private List<AgentActionSubStatus> agentActionSubStatuses = new ArrayList<>();
    }

    @Data
    @Accessors(chain = true)
    public static class AgentActionSubStatus {
        private String name;
    }

    @Data
    @Accessors(chain = true)
    public static class AgentNextAction {
        private String type;
        private int nextActionInDays;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class Companies {
        private String defaultOwningCompany;
        private String defaultManagingCompany;
        private List<String> owningCompanies = new ArrayList<>();
        private List<String> managingCompanies = new ArrayList<>();
    }

    @Data
    @Accessors(chain = true)
    public static class BulkAction {
        private String type;
        private Map<String, Object> params = new HashMap<>();
        private boolean mandatory;


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

        public Map<String, Object> getParams() {
            return params;
        }

        private <T> void assertParamClass(String name, Class<T> paramClass, Object value) {
            Validate.isAssignableFrom(paramClass, value.getClass(), "Invalid param [%s] class [%s], expected [%s], bulk action [%s]", name, value.getClass().getSimpleName(), paramClass.getSimpleName(), type);
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class ReschedulingSettings {
        private int minInstallments;
        private int maxInstallments;
        private int repaymentDueDays;
        private int gracePeriodDays;
        private int holdToBreakDays;
        private int minDpd;
        private BigDecimal minTotalOutstanding;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class ExtensionSettings {
        private int maxPeriodDays;
    }

    public Portfolio getPortfolio(String name) {
        return this.portfolios.stream().filter(p -> StringUtils.equalsIgnoreCase(name, p.getName()))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Portfolio not found by name " + name));
    }

    public Optional<Portfolio> getOptionalPortfolio(String name) {
        return this.portfolios.stream().filter(p -> StringUtils.equalsIgnoreCase(name, p.getName())).findFirst();
    }

    public AgentAction getAgentActionTemplate(String name) {
        AgentAction template = this.agentActionTemplates.get(name);
        Validate.notNull(template, "Agent action template not found by name [%s]", name);
        return template;
    }

    public AgentActionStatus getAgentActionStatusTemplate(String name) {
        AgentActionStatus template = this.agentActionStatusTemplates.get(name);
        Validate.notNull(template, "Agent action status template not found by name [%s]", name);
        return template;
    }

    public Optional<AgentActionStatus> getOptionalAgentActionStatusTemplate(String name) {
        return Optional.ofNullable(this.agentActionStatusTemplates.get(name));
    }

    public List<AgentAction> getAgentActionsByPortfolio(String portfolioName) {
        Portfolio portfolio = getPortfolio(portfolioName);
        List<AgentAction> actions = new ArrayList<>();
        for (AgentAction action : portfolio.getAgentActions()) {
            if (!StringUtils.isBlank(action.getTemplate())) {
                actions.add(getAgentActionTemplate(action.getTemplate()));
            } else {
                actions.add(action);
            }
        }
        for (AgentAction action : actions) {
            List<AgentActionStatus> actionStatuses = new ArrayList<>();
            for (AgentActionStatus status : action.getStatuses()) {
                if (!StringUtils.isBlank(status.getTemplate())) {
                    AgentActionStatus template = getAgentActionStatusTemplate(status.getTemplate());
                    status.setName(Validate.notBlank(template.getName(), "Blank name in action template"));
                    if (!StringUtils.isBlank(template.getDefaultResolution())) {
                        status.setDefaultResolution(template.getDefaultResolution());
                    }
                    if (!CollectionUtils.isEmpty(template.getBulkActions())) {
                        status.setBulkActions(template.getBulkActions());
                    }
                    if (!CollectionUtils.isEmpty(template.getNextActions())) {
                        status.setNextActions(template.getNextActions());
                    }
                    if (!CollectionUtils.isEmpty(template.getAgentActionSubStatuses())) {
                        status.setAgentActionSubStatuses(template.getAgentActionSubStatuses());
                    }
                }
                actionStatuses.add(status);
            }
            action.setStatuses(actionStatuses);
        }
        return actions;
    }
}

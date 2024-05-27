package fintech.dc.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableList;
import fintech.Validate;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DcSettings {

    private List<Portfolio> portfolios = new ArrayList<>();

    private Map<String, Trigger> triggerTemplates = new LinkedHashMap<>();

    private List<AgingBucket> agingBuckets = new ArrayList<>();

    private Map<String, AgentAction> agentActionTemplates = new LinkedHashMap<>();

    private Map<String, AgentActionStatus> agentActionStatusTemplates = new LinkedHashMap<>();

    private Companies companies = new Companies();

    //Again new json property, but this is solution to keep it not hard-coded at the moment.
    private List<Trigger> triggersOnVoidTransaction = new ArrayList<>();

    private ReschedulingSettings reschedulingSettings = new ReschedulingSettings();

    private ExtensionSettings extensionSettings = new ExtensionSettings();


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class Portfolio {
        private String name;

        private String initialStatus;

        private List<Status> statuses = new ArrayList<>();

        private List<Trigger> triggers = new ArrayList<>();

        private List<AgentAction> agentActions = new ArrayList<>();

        public Status statusByName(String name) {
            return statuses.stream().filter(s -> s.getName().equals(name)).findFirst().orElseGet(() -> {
                    Status status = new Status();
                    status.setName(name);
                    status.setPriority(1000);
                    return status;
                }
            );
        }

        public boolean hasStatusByName(String name) {
            return statuses.stream().anyMatch(s -> s.getName().equals(name));
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class Status {
        private String name;
        private int priority;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class AgingBucket {
        private long dpdFrom;
        private long dpdTo;
        private String name;

        public boolean matches(long dpd) {
            return dpd >= this.dpdFrom && dpd <= this.dpdTo;
        }
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class Trigger {
        private String name;
        private String template;
        private List<Action> actions = new ArrayList<>();
        private List<Condition> conditions = new ArrayList<>();
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class Action {
        private String type;
        private Map<String, Object> params = new HashMap<>();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class Condition {
        private String type;
        private Map<String, Object> params = new HashMap<>();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class AgentActionStatus {
        private String template;
        private String name;
        private String defaultResolution;
        private List<NextAction> nextActions = new ArrayList<>();
        private List<BulkAction> bulkActions = new ArrayList<>();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class NextAction {
        private String type;
        private int nextActionInDays;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class BulkAction {
        private String type;
        private Map<String, Object> params = new HashMap<>();
        private boolean mandatory;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class Companies {
        private String defaultOwningCompany;
        private String defaultManagingCompany;
        private List<String> owningCompanies = new ArrayList<>();
        private List<String> managingCompanies = new ArrayList<>();

        public boolean mangingCompanyDefined(String company) {
            return managingCompanies.contains(company);
        }

        public boolean owningCompanyDefined(String company) {
            return owningCompanies.contains(company);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class ReschedulingSettings {
        private int minInstallments;
        private int maxInstallments;
        private int minDpd;
        private int repaymentDueDays;
        private int gracePeriodDays;
        private int holdToBreakDays;
        private BigDecimal minTotalOutstanding;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Accessors(chain = true)
    public static class ExtensionSettings {
        private int maxPeriodDays;
    }

    public AgingBucket resolveAgingBucket(long dpd) {
        return agingBuckets.stream().filter(b -> b.matches(dpd)).findFirst().orElseGet(() -> unknownBucket(dpd));
    }

    public Portfolio findPortfolio(String name) {
        return this.portfolios.stream().filter(p -> p.getName().equals(name)).findFirst().orElseGet(DcSettings::unknownPortfolio);
    }

    public Trigger findTriggerTemplate(String name) {
        Trigger trigger = this.triggerTemplates.get(name);
        Validate.notNull(trigger, "Trigger template not found by name [%s]", name);
        return trigger;
    }

    private static AgingBucket unknownBucket(long dpd) {
        AgingBucket bucket = new AgingBucket();
        bucket.setDpdFrom(dpd);
        bucket.setDpdTo(dpd);
        bucket.setName("Unknown");
        return bucket;
    }

    private static Portfolio unknownPortfolio() {
        Portfolio portfolio = new Portfolio();
        portfolio.setName("Unknown");
        portfolio.setInitialStatus("Unknown");
        Status status = new Status();
        status.setName("Unknown");
        status.setPriority(0);
        portfolio.setStatuses(ImmutableList.of(status));
        return portfolio;
    }
}

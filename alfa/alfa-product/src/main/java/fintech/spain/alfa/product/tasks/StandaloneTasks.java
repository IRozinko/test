package fintech.spain.alfa.product.tasks;

import com.google.common.collect.Lists;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.settings.AlfaSettings;
import fintech.task.model.TaskSettings;
import fintech.task.spi.TaskDefinition;
import fintech.task.spi.TaskDefinitionBuilder;
import fintech.task.spi.TaskRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StandaloneTasks {

    public interface StandaloneTask {
        TaskDefinition build();
    }

    @Component
    public static class ExtensionSaleCall implements StandaloneTask {

        public static final String TYPE = "ExtensionSaleCall";

        public static final String POSTPONE = "Postpone";
        public static final String UNREACHABLE = "Unreachable";
        public static final String CLIENT_ANSWERED_NO_AGREEMENT = "ClientAnswered-NoAgreement";
        public static final String CLIENT_ANSWERED_WILL_PURCHASE_EXTENSION = "ClientAnswered-WillPurchaseExtension";
        public static final String CLIENT_ANSWERED_WILL_PAY_FULL_LOAN = "ClientAnswered-WillPayFullLoan";
        public static final String EXPIRE = "Expire";

        @Autowired
        private SettingsService settingsService;

        @Override
        public TaskDefinition build() {
            TaskSettings.TaskConfig taskConfig = settingsService.getJson(AlfaSettings.ACTIVITY_SETTINGS, TaskSettings.class).getConfigOrDefault(TYPE);
            return new TaskDefinitionBuilder(TYPE)
                .group("Standalone")
                .description("CS Operator extension sale call")
                .resolution(POSTPONE).asPostpone().add()
                .resolution(UNREACHABLE).asPostpone().add()
                .resolution(CLIENT_ANSWERED_NO_AGREEMENT).add()
                .resolution(CLIENT_ANSWERED_WILL_PURCHASE_EXTENSION).add()
                .resolution(CLIENT_ANSWERED_WILL_PAY_FULL_LOAN).add()
                .resolution(EXPIRE).add()
                .defaultExpireResolution(EXPIRE)
                .priority(taskConfig.getPriority())
                .priorityAfterPostpone(taskConfig.getPriorityAfterPostpone())
                .build();
        }
    }

    @Autowired
    private final List<StandaloneTask> standaloneTasks = Lists.newArrayList();

    @Autowired
    private TaskRegistry taskRegistry;

    public void init() {
        standaloneTasks.forEach(task -> taskRegistry.addDefinition(task::build));
    }
}

package fintech.task

import fintech.task.spi.ExpiredTaskConsumer
import fintech.task.spi.TaskRegistry
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    TaskService taskService

    @Autowired
    TaskRegistry taskRegistry

    @Autowired
    AgentService agentService

    @Autowired
    TaskQueueService taskQueue

    @Autowired
    ExpiredTaskConsumer expiredTaskConsumer

    def setup() {
        testDatabase.cleanDb()
        MockTaskListener.executed = 0
    }
}

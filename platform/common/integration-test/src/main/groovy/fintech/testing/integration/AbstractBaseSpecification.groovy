package fintech.testing.integration

import fintech.IntegrationApplication
import fintech.TimeMachine
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ActiveProfiles("itest")
@ContextConfiguration
@SpringBootTest(classes = IntegrationApplication.class)
abstract class AbstractBaseSpecification extends Specification {

    @Autowired
    TestingEventConsumer eventConsumer

    @Autowired
    TestDatabase testDatabase

    private static boolean dbInitialized

    def setupSpec() {
        if (!dbInitialized) {
            TestDatabaseFactory.get().createTestDb()
        }
        dbInitialized = true
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    def setup() {
        TimeMachine.useDefaultClock()
        eventConsumer.clear()
    }

    def cleanup() {
        TimeMachine.useDefaultClock()
    }
}

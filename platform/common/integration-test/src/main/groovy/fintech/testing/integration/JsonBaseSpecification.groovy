package fintech.testing.integration

import fintech.IntegrationApplication
import fintech.TimeMachine
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ActiveProfiles("itest")
@AutoConfigureJsonTesters
@ContextConfiguration
@SpringBootTest(classes = IntegrationApplication.class)
class JsonBaseSpecification extends Specification {

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
    }

    def cleanup() {
        TimeMachine.useDefaultClock()
    }

}

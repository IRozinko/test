package fintech.testing.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureMockMvc
class ApiAbstractBaseSpecification extends AbstractBaseSpecification {

    @Autowired
    MockMvc mockMvc;

    def setup() {
        testDatabase.cleanDb()
    }

}

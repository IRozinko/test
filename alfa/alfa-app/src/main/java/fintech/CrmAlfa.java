package fintech;

import fintech.spain.alfa.product.CrmAlfaSetup;
import fintech.spain.alfa.product.testing.DemoSetup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@Slf4j
@SpringBootApplication
public class CrmAlfa implements CommandLineRunner {

    private final CrmAlfaSetup crmAlfaSetup;
    private final DemoSetup demoSetup;

    @Autowired
    public CrmAlfa(CrmAlfaSetup crmAlfaSetup, DemoSetup demoSetup) {
        this.crmAlfaSetup = crmAlfaSetup;
        this.demoSetup = demoSetup;
    }

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(CrmAlfa.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        crmAlfaSetup.setUp();
        demoSetup.setUp();
    }

}

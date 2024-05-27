package manual;
import fintech.dowjones.DowJonesRequest;
import fintech.dowjones.DowJonesRequestData;
import fintech.dowjones.DowJonesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(scanBasePackages = "fintech")
public class DowJonesTestApp implements CommandLineRunner {

    @Autowired
    private DowJonesService service;

    public static void main(String[] args) {
        System.setProperty("dowjones.provider", "mock-dowjones");
        System.setProperty("dowjones.namespace", "33");
        System.setProperty("dowjones.username", "test");
        System.setProperty("dowjones.password", "test");

        SpringApplication.run(DowJonesTestApp.class, args);
    }

    @Override
    public void run(String... args) {
        Map<String, String> params = new HashMap<>();
        params.put("name", "medvedev");
        params.put("date-of-birth", LocalDate.now().minusYears(30).toString());
        DowJonesRequest request = service.search(new DowJonesRequestData().setClientId(1L).setParameters(params));
    }
}

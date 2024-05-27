package fintech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class CrmAlfaBackoffice {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Madrid"));
        SpringApplication.run(CrmAlfaBackoffice.class, args);
    }

}

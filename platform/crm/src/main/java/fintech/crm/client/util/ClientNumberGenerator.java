package fintech.crm.client.util;

import fintech.crm.client.db.ClientRepository;
import fintech.crm.db.Entities;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
public class ClientNumberGenerator {

    @Autowired
    private ClientRepository clientRepository;

    private String forcedNumber;

    public String newNumber(String prefix, int length) {
        if (isNotBlank(forcedNumber)) {
            String newNumber = forcedNumber;
            forcedNumber = null;
            return newNumber;
        }

        for (int i = 0; i < 100; i++) {
            String number = prefix + RandomStringUtils.randomNumeric(length);
            if (!clientRepository.exists(Entities.client.number.eq(number))) {
                log.debug("Generated new client number: {}", number);
                return number;
            }
        }
        throw new IllegalStateException("Failed to generate unique client number");
    }

    public void setForcedNumber(String forcedNumber) {
        this.forcedNumber = forcedNumber;
    }
}

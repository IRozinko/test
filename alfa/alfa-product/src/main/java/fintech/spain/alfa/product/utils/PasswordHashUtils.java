package fintech.spain.alfa.product.utils;

import fintech.crm.logins.PasswordHash;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;

@Component
@Slf4j
public class PasswordHashUtils {

    @Value("${salt.path:/salt.txt}")
    private String saltPath;

    private byte[] salt;

    @PostConstruct
    private void init() throws IOException {
        byte[] saltText = StreamUtils.copyToByteArray(getClass().getResourceAsStream(saltPath));
        this.salt = Arrays.copyOfRange(saltText, 100, 600);
    }

    public boolean verifyPassword(String password, String correctHash) {
        return verifyOnlyBySH1(password, correctHash) || verifyBySalt(password, correctHash);
    }

    private boolean verifyBySalt(String password, String correctHash) {
        byte[] toCheckPassword = ArrayUtils.addAll(salt, password.getBytes());
        String hashedPassword = DigestUtils.sha1Hex(toCheckPassword);
        return correctHash.equals(hashedPassword);
    }

    private boolean verifyOnlyBySH1(String password, String correctHash) {
        try {
            return PasswordHash.verifyPassword(password, correctHash);
        } catch (Exception ex) {
            log.warn("Checking password hash by sh1 failed : {}", ex.getMessage());
            return false;
        }
    }
}

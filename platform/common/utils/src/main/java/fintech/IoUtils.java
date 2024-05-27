package fintech;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class IoUtils {

    public static byte[] copyToByteArray(InputStream inputStream) {
        try {
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            log.error("Exception on reading inputStream", e);
            throw new IllegalArgumentException(e);
        }
    }
}

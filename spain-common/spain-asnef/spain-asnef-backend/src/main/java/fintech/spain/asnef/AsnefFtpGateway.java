package fintech.spain.asnef;

import java.io.InputStream;
import java.time.LocalDate;

public interface AsnefFtpGateway {

    void upload(AsnefFtpProperties.Config config, InputStream input);

    AsnefGatewayResponse download(AsnefFtpProperties.Config config, LocalDate when);
}

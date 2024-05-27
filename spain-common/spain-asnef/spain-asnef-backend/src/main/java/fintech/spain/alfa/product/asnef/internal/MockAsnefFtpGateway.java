package fintech.spain.alfa.product.asnef.internal;

import com.google.common.collect.Lists;
import fintech.spain.asnef.AsnefFtpGateway;
import fintech.spain.asnef.AsnefFtpProperties;
import fintech.spain.asnef.AsnefGatewayResponse;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Component(MockAsnefFtpGateway.NAME)
public class MockAsnefFtpGateway implements AsnefFtpGateway {

    public static final String NAME = "mock-ftp-gateway";

    private String exported;

    @Override
    @SneakyThrows
    public void upload(AsnefFtpProperties.Config config, InputStream input) {
        this.exported = IOUtils.toString(input, StandardCharsets.US_ASCII);
    }

    @Override
    public AsnefGatewayResponse download(AsnefFtpProperties.Config config, LocalDate when) {
        return new AsnefGatewayResponse(Lists.newArrayList());
    }

    public String getExported() {
        return exported;
    }
}

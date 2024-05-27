package fintech.email.spi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailAttachment {

    private final String fileName;
    private final String contentType;
    private byte[] bytes;

    public EmailAttachment(String fileName, String contentType) {
        this.fileName = fileName;
        this.contentType = contentType;
    }
}

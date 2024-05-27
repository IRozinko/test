package fintech.bo.api.model.cms;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NotificationTemplate {

    private Email email;
    private Sms sms;

    @Data
    public static class Email {
        private String subject;
        private String body;
        private List<String> pdfAttachments = new ArrayList<>();
    }

    @Data
    public static class Sms {
        private String text;
    }
}

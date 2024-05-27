package fintech.cms;

import lombok.Value;

import java.util.Optional;

public class CmsNotification {

    private final Email email;
    private final Sms sms;

    public CmsNotification(Email email, Sms sms) {
        this.email = email;
        this.sms = sms;
    }

    public Optional<Email> getEmail() {
        return Optional.ofNullable(this.email);
    }

    public Optional<Sms> getSms() {
        return Optional.ofNullable(this.sms);
    }

    @Value
    public static class Email {
        private final String subject;
        private final String body;
    }

    @Value
    public static class Sms {
        private final String text;
    }
}

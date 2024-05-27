package fintech.email;


public interface EmailService {

    /**
     * @return id of enqueued email
     */
    Long enqueue(Email email);
}

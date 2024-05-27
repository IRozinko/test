package fintech.sms;


public interface SmsService {

    /**
     * @return sms id
     */
    Long enqueue(Sms sms);

    /**
     *
     * @return true if sms found for delivery report
     */
    boolean deliveryReportReceived(SmsDeliveryReport deliveryReport);

    Long takeIncomingSms(IncomingSms sms);
}

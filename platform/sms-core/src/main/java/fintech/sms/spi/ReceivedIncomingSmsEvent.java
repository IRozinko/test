package fintech.sms.spi;

import fintech.sms.IncomingSms;
import lombok.Value;

@Value
public class ReceivedIncomingSmsEvent {

    private IncomingSms sms;
}

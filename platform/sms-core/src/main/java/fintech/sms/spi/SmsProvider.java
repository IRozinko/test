package fintech.sms.spi;


import fintech.sms.Sms;

public interface SmsProvider {

    SmsResponse send(Sms sms);

}

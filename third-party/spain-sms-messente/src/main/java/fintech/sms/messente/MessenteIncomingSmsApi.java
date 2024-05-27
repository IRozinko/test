package fintech.sms.messente;

import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.sms.IncomingSms;
import fintech.sms.SmsDeliveryReport;
import fintech.sms.SmsService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
public class MessenteIncomingSmsApi {

    private final SmsService smsService;

    @Autowired
    public MessenteIncomingSmsApi(SmsService smsService) {
        this.smsService = smsService;
    }

    @GetMapping("/api/public/web/messente-sms")
    public void receiveSms(@ModelAttribute MessenteIncomingSms sms) {
        log.info("Received incoming SMS: [{}]", sms);

        IncomingSms incoming = new IncomingSms();
        incoming.setSource("Messente");
        incoming.setPhoneNumber(sms.getFrom());
        incoming.setText(sms.getText());
        incoming.setRawDataJson(JsonUtils.writeValueAsString(sms));
        smsService.takeIncomingSms(incoming);
    }

    @GetMapping("/api/public/web/messente-dlr")
    public void receiveDeliveryReport(@ModelAttribute MessenteDeliveryReport report) {
        log.info("Received SMS delivery report: [{}]", report);

        SmsDeliveryReport deliveryReport = new SmsDeliveryReport();
        deliveryReport.setProviderMessageId(report.getSms_unique_id());
        deliveryReport.setStatus(report.getStatus());
        deliveryReport.setStatus2(report.getStat());
        deliveryReport.setError(report.getErr());
        deliveryReport.setReceivedAt(TimeMachine.now());
        smsService.deliveryReportReceived(deliveryReport);
    }

    @Data
    public static class MessenteIncomingSms {
        private String from;
        private String to;
        private String text;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime time;

        private String msgid;
    }

    @Data
    public static class MessenteDeliveryReport {
        private String sms_unique_id;
        private String status;
        private String stat;
        private String err;
    }
}

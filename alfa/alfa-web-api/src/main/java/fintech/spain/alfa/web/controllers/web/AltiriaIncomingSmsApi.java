package fintech.spain.alfa.web.controllers.web;

import fintech.JsonUtils;
import fintech.sms.IncomingSms;
import fintech.sms.SmsService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AltiriaIncomingSmsApi {

    @Autowired
    private SmsService smsService;

    @PostMapping("/api/public/web/altiria-sms")
    @ResponseBody
    public String receiveSms(AltiriaIncomingSms sms) {
        log.info("Received incoming SMS: [{}]", sms);

        IncomingSms incoming = new IncomingSms();
        incoming.setSource("Altiria");
        incoming.setPhoneNumber(sms.getTelnum());
        incoming.setText(sms.getKeyword() + " " + sms.getText());
        incoming.setRawDataJson(JsonUtils.writeValueAsString(sms));

        smsService.takeIncomingSms(incoming);
        return "OK";
    }

    @Data
    public static class AltiriaIncomingSms {
        private String alias;
        private String telnum;
        private String keyword;
        private String text;
        private String provider;
        private String date;
        private String shortnum;
    }
}

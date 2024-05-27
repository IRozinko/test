package fintech.sms.altiria

import fintech.sms.Sms

import java.time.LocalDateTime

class AltiriaManualRun {

    static void main(String[] args) {
        AltiriaSmsProvider provider = new AltiriaSmsProvider(
            "http://www.altiria.net/api/http",
            "viasms",
            "viasms",
            "viasms2011dicptt"
        )
        def result = provider.send(new Sms(senderId: "LOC", to: "+37127783911", text: "Hello!? " + System.currentTimeMillis(), sendAt: LocalDateTime.now()))
        println result
        result = provider.send(new Sms(senderId: "LOC", to: "+37127783911", text: "Hello!? " + System.currentTimeMillis(), sendAt: LocalDateTime.now()))
        println result
    }
}

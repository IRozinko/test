package fintech.payments.spi

import spock.lang.Specification
import spock.lang.Subject

class StatementProcessorHelperTest extends Specification {

    @Subject
    StatementProcessorHelper helper = new StatementProcessorHelper()

    def "ExtractBankOrderCode"() {
        expect:
        !helper.extractBankOrderCode(null).isPresent()
        !helper.extractBankOrderCode("").isPresent()
        !helper.extractBankOrderCode("1231231 123 123 123").isPresent()
        !helper.extractBankOrderCode("UN 321 123 123").isPresent()

        helper.extractBankOrderCode("UNX0373 bankorder1 Unnax test").get() == "bankorder1"
        helper.extractBankOrderCode("Transferencia emitida Jon Doe UNX0373 bankorder1 Unnax test").get() == "bankorder1"
        helper.extractBankOrderCode('''02
            02
            002
            000000000000
            BBVAESMMXXX     
            ANA RUANO COSTA                       
            TRANSFER INMEDIATA                    
            UNX 835907845
            00200061TRF                           
            TRANSFER INMEDIATA                    ''').get() == "835907845"
    }

}

package fintech.payments

import fintech.JsonUtils
import fintech.TimeMachine
import fintech.spain.unnax.callback.model.CallbackRequest
import fintech.spain.unnax.callback.model.PaymentWithCardCallbackData
import fintech.spain.unnax.callback.model.PaymentWithTransferAuthorizedData
import fintech.spain.unnax.callback.model.PaymentWithTransferCompletedData
import fintech.spain.unnax.callback.model.TransferAutoProcessedCallbackData

class UnnaxCallbackRequestFactory {

    static CallbackRequest transferAutoProcessed() {
        return transferAutoProcessed(true)
    }

    static CallbackRequest transferAutoProcessed(boolean success) {
        def data = new TransferAutoProcessedCallbackData()
            .setSuccess(success)
            .setProduct("movex_dbt")
            .setOrderId("123123")
            .setBankOrderId("2222")
            .setDate(TimeMachine.today())
            .setTime(TimeMachine.now().toLocalTime())
            .setAmount(100)
            .setCurrency("EUR")
            .setCustomerId("C123123")
            .setCustomerAccount("")
            .setSourceAccount("")
            .setSrcAccountBalance(1000)
            .setCancelled(false)
            .setSourceBankId(1L)

        return new CallbackRequest()
            .setResponseId("1234")
            .setSignature("ea59b3c20c590ba9a889ddb8c7be5c87ee95db85")
            .setData(JsonUtils.readTree(data))
    }

    static CallbackRequest cardPayment(boolean success) {
        def data = new PaymentWithCardCallbackData()
            .setPan("1111")
            .setBin("000000")
            .setCurrency("978")
            .setTransactionType("pay")
            .setExpirationDate("2021")
            .setExpireMonth("11")
            .setExpireYear("20")
            .setCardHolder("Name Owner Credit Card")
            .setCardBrand("VISA || MASTER || AMEX")
            .setCardType("DEBIT || CREDIT")
            .setCardCountry("country")
            .setCardBank("bank name")
            .setOrderCode("order_code")
            .setToken("1a4b4833253ecas143f4ec1111faa056ac0ga4g62")
            .setDate("2017-03-13T17:35:00")
            .setAmount(100)
            .setConcept("Pay Credit Card")
            .setState(success ? 4 : 3)

        return new CallbackRequest()
            .setResponseId("b9f174fc-7763-4853-96f1-ab8bfdbc66a4")
            .setSignature("e8f88f223f7a6269966a74f404fa24ae039302e8")
            .setTriggeredEvent("event_payment_creditcard_pay")
            .setService("payment_creditcard")
            .setEnvironment("unnax_integration_aws")
            .setTraceIdentifier("b9f174fc-7763-4853-96f1-ab8bfdbc66a4")
            .setDate(TimeMachine.now())
            .setData(JsonUtils.readTree(data))
    }

    static CallbackRequest transferLockstepAuthorised() {
        def data = new PaymentWithTransferAuthorizedData()
            .setOrderCode("CR1556890585")
            .setAmount(101)
            .setCurrency("EUR")
            .setCustomerCode("")
            .setCustomerNames("")
            .setService("payment_transfer_dbt")
            .setStatus("payment_authorized")
            .setSuccess(true)
            .setErrorMessages("")

        return new CallbackRequest()
            .setResponseId("3e4ae5bb-f70a-4f86-b817-96903a7e1529")
            .setSignature("a4018e82d651ed74c6013fdb470fb68b82ed539b")
            .setDate(TimeMachine.now())
            .setService("lockstep_sign")
            .setTriggeredEvent("event_payment_transfer_lockstep_authorized")
            .setEnvironment("unnax_integration_aws")
            .setData(JsonUtils.readTree(data))
    }

    static CallbackRequest transferLockstepCompleted() {
        def data = new PaymentWithTransferCompletedData()
            .setCustomerCode("")
            .setOrderCode("CR1556890585")
            .setBankOrderCode("")
            .setAmount(101)
            .setDate("2019-05-03 13:39:03")
            .setSuccess(true)
            .setSignature("b5419bb6205dc2918ecdc8009391f74434325507")
            .setResult(true)
            .setAccountNumber("ES7299992018121921123652")
            .setStatus("payment_completed")
            .setService("payment_transfer_dbt")

        return new CallbackRequest()
            .setResponseId("0a0566620c114a57a5a22e1682152582")
            .setSignature("b5419bb6205dc2918ecdc8009391f74434325507")
            .setDate(TimeMachine.now())
            .setService("payment_transfer_dbt")
            .setTriggeredEvent("event_payment_transfer_lockstep_completed")
            .setEnvironment("unnax_integration_aws")
            .setData(JsonUtils.readTree(data))
    }
}

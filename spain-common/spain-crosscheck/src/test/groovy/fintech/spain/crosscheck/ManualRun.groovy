package fintech.spain.crosscheck

import fintech.spain.crosscheck.impl.SpainCrosscheckProviderBean
import fintech.spain.crosscheck.model.SpainCrosscheckInput

class ManualRun {

    static void main(String[] args) {
        def provider = new SpainCrosscheckProviderBean()
        provider.setUrl("http://localhost:8080/api/internal/crosscheck/client")
        provider.setApiKey("pass123")
        def response = provider.request(new SpainCrosscheckInput(
            dni: "34020478M",
        ))
        println response
    }
}

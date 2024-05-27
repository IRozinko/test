package fintech.spain.inglobaly

import fintech.spain.inglobaly.impl.InglobalyProviderBean

class ManualRun {

    public static void main(String[] args) {
        def provider = new InglobalyProviderBean()
        provider.login = "webservice@alfa"
        provider.password = "tu\$32FasVvak88="
        provider.accesoServiceUrl = "https://ws.inglobaly.com:443/AccesoWebService"
        provider.domiciliosServiceUrl = "https://ws.inglobaly.com:443/DomiciliosWebService"
        def data = provider.request("46981857X")
        println data
    }
}

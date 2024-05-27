package fintech.spain.experian

import fintech.spain.experian.impl.ObjectSerializer
import fintech.spain.experian.impl.concursales.ExperianConcursalesConfiguration
import fintech.spain.experian.impl.concursales.ExperianConcursalesProviderBean
import fintech.spain.experian.impl.concursales.ExperianConcursalesRequestBuilder
import fintech.spain.experian.model.CaisDocumentType
import fintech.spain.experian.model.ConcursalesRequest

class ExperianConcursalesManualRun {

    static ExperianConcursalesConfiguration prodConfig() {
        return new ExperianConcursalesConfiguration(
            serviceLogin: "USRSMS01",
            servicePassword: "17SVMS15",
            serviceSubscriberId: "07453376",
            serviceUrl: "http://localhost:8300/ConcursalesWebService/ServicioInformes.svc?wsdl"
        )
    }


    static ExperianConcursalesConfiguration testConfig() {
        return new ExperianConcursalesConfiguration(
            serviceLogin: "TWIXML01",
            servicePassword: "Kx-LhY+R1",
            serviceSubscriberId: "07453676",
            serviceUrl: "http://localhost:8301/ConcursalesWebServiceUAT/ServicioInformes.svc"
        )
    }

    static void main(String[] args) {
//        def dni = "77404869W" // for prod with debts
        def dni = "51444375E123" // not found

        def serializer = new ObjectSerializer()

        def config = testConfig()
        def requestBuilder = new ExperianConcursalesRequestBuilder(config)
        def gateway = new ExperianConcursalesProviderBean(config)

        def request = requestBuilder.prepareRequest(new ConcursalesRequest(documentType: CaisDocumentType.NIF, documentNumber: dni))
        def report = gateway.request(request)

        def xml = serializer.marshal([report])
        println xml
    }
}

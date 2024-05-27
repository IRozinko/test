package fintech.spain.experian

import fintech.spain.experian.impl.ObjectSerializer
import fintech.spain.experian.impl.cais.ExperianCaisConfiguration
import fintech.spain.experian.impl.cais.ExperianCaisProviderBean
import fintech.spain.experian.impl.cais.ExperianCaisRequestBuilder
import fintech.spain.experian.model.CaisRequest

class ExperianCaisManualRun {

    static ExperianCaisConfiguration prodConfig() {
        return new ExperianCaisConfiguration(
            serviceLogin: "USRSMS01",
            servicePassword: "17SVMS15",
            serviceSubscriberId: "07453376",
            serviceUrl: "http://localhost:8300/CaisWebService/ServicioInformes.svc?wsdl"
        )
    }


    static ExperianCaisConfiguration testConfig() {
        return new ExperianCaisConfiguration(
            serviceLogin: "TWIXML01",
            servicePassword: "Kx-LhY+R1",
            serviceSubscriberId: "07453676",
            serviceUrl: "http://localhost:8301/CaisWebServiceUAT/ServicioInformes.svc?wsdl"
        )
    }

    static void main(String[] args) {
//        def dni = "77404869W" // for prod with debts
        def dni = "51444375E" // not found

        def serializer = new ObjectSerializer()

        def config = testConfig()
        def requestBuilder = new ExperianCaisRequestBuilder(config)

        def resumen = requestBuilder.prepareRequest(new CaisRequest(documentNumber: dni), ExperianCaisConfiguration.RESUMEN)
        def listaOperaciones = requestBuilder.prepareRequest(new CaisRequest(documentNumber: dni), ExperianCaisConfiguration.LISTA_OPERACIONES)

        def gatway = new ExperianCaisProviderBean(config)

        def resumentReport = gatway.request(resumen)
        def listaOperacionesReport = gatway.request(listaOperaciones)

        println serializer.marshal(resumentReport.getContent())
        println serializer.marshal(listaOperacionesReport.getContent())
    }
}

package fintech.spain.experian

import fintech.TimeMachine
import fintech.spain.experian.impl.cais.ExperianServiceBean
import fintech.spain.experian.impl.cais.MockExperianCaisProvider
import fintech.spain.experian.model.CaisQuery
import fintech.spain.experian.model.CaisRequest
import fintech.spain.experian.model.ExperianStatus
import org.springframework.beans.factory.annotation.Autowired

class ExperianServiceTest extends BaseSpecification {

    @Autowired
    ExperianServiceBean service

    @Autowired
    MockExperianCaisProvider mockGateway

    def "Resumen - not found"() {
        given:
        mockGateway.setResumenResponseResource("experian/cais-response-resumen-not-found.xml")
        def request = new CaisRequest(1L, 2L, "72404869W")

        when:
        def result = service.requestResumen(request)

        then:
        result.status == ExperianStatus.NOT_FOUND
    }

    def "Resumen - success"() {
        given:
        mockGateway.setResumenResponseResource("experian/cais-response-resumen-success.xml")
        def request = new CaisRequest(1L, 2L, "72404869W")

        when:
        def result = service.requestResumen(request)

        then:
        result.status == ExperianStatus.FOUND
        result.importeTotalImpagado == 873.22
        result.maximoImporteImpagado == 966.4
        result.numeroTotalCuotasImpagadas == 0
        result.numeroTotalOperacionesImpagadas == 1
        result.peorSituacionPago == "Mayor de 180 días"
        result.peorSituacionPagoHistorica == "Mayor de 180 días"
        result.provinciaCodigo == "36"
    }

    def "Resumen - cached"() {
        given:
        mockGateway.setResumenResponseResource("experian/cais-response-resumen-success.xml")
        def request = new CaisRequest(1L, 2L, "72404869W")

        expect:
        !service.findLatestResumenResponse(new CaisQuery(clientId: 1L, documentNumber: "72404869W")).isPresent()

        when:
        def result = service.requestResumen(request)
        def cachedResult = service.findLatestResumenResponse(new CaisQuery(clientId: 1L, documentNumber: "72404869W", createdAfter: TimeMachine.now().minusMinutes(5), status: [ExperianStatus.FOUND])).get()

        then:
        cachedResult.id == result.id

        and:
        !service.findLatestResumenResponse(new CaisQuery(clientId: 2L, documentNumber: "72404869W")).isPresent()
        !service.findLatestResumenResponse(new CaisQuery(clientId: 1L, documentNumber: "A 72404869W")).isPresent()
        !service.findLatestResumenResponse(new CaisQuery(clientId: 1L, documentNumber: "72404869W", status: [ExperianStatus.NOT_FOUND])).isPresent()
        !service.findLatestResumenResponse(new CaisQuery(clientId: 1L, documentNumber: "72404869W", createdAfter: TimeMachine.now().plusSeconds(1))).isPresent()
    }

    def "Resumen - invalid DNI"() {
        given:
        mockGateway.setResumenResponseResource("experian/cais-response-resumen-invalid-dni.xml")
        def request = new CaisRequest(1L, 2L, "72404869W")

        when:
        def result = service.requestResumen(request)

        then:
        result.status == ExperianStatus.ERROR
        result.error == "1008: Formato incorrecto del DNI/NIF/CIF en la trama de entrada"
    }

    def "Resumen - required object not found in XML"() {
        given:
        mockGateway.setResumenResponseResource("experian/cais-response-operaciones-success.xml")
        def request = new CaisRequest(1L, 2L, "72404869W")

        when:
        def result = service.requestResumen(request)

        then:
        result.status == ExperianStatus.ERROR
        result.error == "No required objects found in response"
    }


    def "Resumen - invalid XML"() {
        given:
        mockGateway.setResumenResponseResource("experian/invalid.xml")
        def request = new CaisRequest(1L, 2L, "72404869W")

        when:
        def result = service.requestResumen(request)

        then:
        result.status == ExperianStatus.ERROR
    }

    def "Operaciones - not found"() {
        given:
        mockGateway.setListOperacionesResponseSource("experian/cais-response-operaciones-not-found.xml")
        def request = new CaisRequest(1L, 2L, "72404869W")

        when:
        def result = service.requestListOperaciones(request)

        then:
        result.status == ExperianStatus.NOT_FOUND
    }

    def "Operaciones - invalid DNI"() {
        given:
        mockGateway.setListOperacionesResponseSource("experian/cais-response-operaciones-invalid-dni.xml")
        def request = new CaisRequest(1L, 2L, "72404869W")

        when:
        def result = service.requestListOperaciones(request)

        then:
        result.status == ExperianStatus.ERROR
        result.error == "1008: Formato incorrecto del DNI/NIF/CIF en la trama de entrada"
    }

    def "Operaciones - success"() {
        given:
        mockGateway.setListOperacionesResponseSource("experian/cais-response-operaciones-success.xml")
        def request = new CaisRequest(1L, 2L, "72404869W")

        when:
        def result = service.requestListOperaciones(request)

        then:
        result.status == ExperianStatus.FOUND
        result.numeroRegistrosDevueltos == 1
    }

    def "Operaciones - cached"() {
        given:
        mockGateway.setListOperacionesResponseSource("experian/cais-response-operaciones-success.xml")
        def request = new CaisRequest(1L, 2L, "72404869W")

        expect:
        !service.findLatestListOperacionesResponse(new CaisQuery(clientId: 1L, documentNumber: "72404869W")).isPresent()

        when:
        def result = service.requestListOperaciones(request)
        def cachedResult = service.findLatestListOperacionesResponse(new CaisQuery(clientId: 1L, documentNumber: "72404869W", createdAfter: TimeMachine.now().minusMinutes(5), status: [ExperianStatus.FOUND])).get()

        then:
        cachedResult.id == result.id

        and:
        !service.findLatestResumenResponse(new CaisQuery(clientId: 2L, documentNumber: "72404869W")).isPresent()
        !service.findLatestResumenResponse(new CaisQuery(clientId: 1L, documentNumber: "A 72404869W")).isPresent()
        !service.findLatestListOperacionesResponse(new CaisQuery(clientId: 1L, documentNumber: "72404869W", status: [ExperianStatus.NOT_FOUND])).isPresent()
        !service.findLatestListOperacionesResponse(new CaisQuery(clientId: 1L, documentNumber: "72404869W", createdAfter: TimeMachine.now().plusSeconds(1))).isPresent()
    }

    def "Operaciones - required object not found in XML"() {
        given:
        mockGateway.setListOperacionesResponseSource("experian/cais-response-resumen-success.xml")
        def request = new CaisRequest(1L, 2L, "72404869W")

        when:
        def result = service.requestListOperaciones(request)

        then:
        result.status == ExperianStatus.ERROR
        result.error == "No required objects found in response"
    }

    def "Operaciones - invalid XML"() {
        given:
        mockGateway.setListOperacionesResponseSource("experian/invalid.xml")
        def request = new CaisRequest(1L, 2L, "72404869W")

        when:
        def result = service.requestListOperaciones(request)

        then:
        result.status == ExperianStatus.ERROR
    }


    def "Operaciones - debts"() {
        given:
        mockGateway.setListOperacionesResponseSource("experian/cais-response-operaciones-success.xml")
        def request = new CaisRequest(1L, 2L, "72404869W")

        when:
        def result = service.requestListOperaciones(request)
        def debts = service.findDebtsByOperacionesResponse(result.id)

        then:
        debts.size() == 2
        debts[0].saldoImpagado == 873.22
        debts[0].tipoProductoFinanciadoCodigo == "47"
        debts[0].tipoProductoFinanciadoDescription == "Telecomunicaciones"
        debts[1].saldoImpagado == 227.9
        debts[1].tipoProductoFinanciadoCodigo == "26"
        debts[1].tipoProductoFinanciadoDescription == "Descubierto en Cuenta Corriente"
    }
}

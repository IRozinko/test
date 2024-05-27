package fintech.spain.experian.impl.cais;

enum ExperianCaisRequestParameter {

    ID_SUBSCRIBER("ID_SUSCRIPTOR"),
    REFERENCE_CONSULTATION("REFERENCIA_CONSULTA"),
    TYPE_DOCUMENT("TIPO_DOCUMENTO"),
    NUMBER_DOCUMENT("NUMERO_DOCUMENTO"),
    COUNTRY_DOCUMENT("PAIS_DOCUMENTO"),
    TRANSACTION_ID("ID_OPERACION"),
    RETURN_RECORDS_FROM_INDEX("NUMERO_ORDEN"),
    RETURN_RECORDS_COUNT("NUMERO_REGISTROS");

    private final String key;

    ExperianCaisRequestParameter(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

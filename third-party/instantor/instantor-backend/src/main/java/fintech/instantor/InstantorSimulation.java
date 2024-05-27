package fintech.instantor;

import fintech.instantor.model.InstantorResponseStatus;
import fintech.instantor.model.SaveInstantorResponseCommand;
import lombok.SneakyThrows;

import java.math.BigDecimal;

public class InstantorSimulation {

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateOkResponse(Long clientId, String dni, String name, String iban, String iban2) {
        return InsightInstantorSimulation.simulateOkResponse(clientId, dni, name, iban, iban2);
    }

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateCommonOkResponse(Long clientId, String dni, String name, String iban, String iban2) {
        return CommonInstantorSimulation.simulateOkResponse(clientId, dni, name, iban, iban2);
    }

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateOkResponse(SimulateInstantorReq req, String json) {
        return InsightInstantorSimulation.simulateOkResponse(req, json);
    }

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateOkResponseWithSingleAccount(Long clientId, String dni, String name, String iban) {
        return InsightInstantorSimulation.simulateOkResponseWithSingleAccount(clientId, dni, name, iban);
    }

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateCommonOkResponseWithSingleAccount(Long clientId, String dni, String name, String iban) {
        return CommonInstantorSimulation.simulateOkResponseWithSingleAccount(clientId, dni, name, iban);
    }

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateOkResponseWithSingleAccount(Long clientId, String dni, String name, String iban, BigDecimal averageAmountOfIncomingTransactionsMonth, BigDecimal averageAmountOfOutgoingTransactionsMonth) {
        return InsightInstantorSimulation.simulateOkResponseWithSingleAccount(clientId, dni, name, iban, averageAmountOfIncomingTransactionsMonth, averageAmountOfOutgoingTransactionsMonth);
    }

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateOkResponseFromJson(Long clientId, String dni, String name, String iban, String jsonPayLoad) {
        return InsightInstantorSimulation.simulateOkResponseFromJson(clientId, dni, name, iban, jsonPayLoad);
    }

    public static final SaveInstantorResponseCommand simulateFailResponse(Long clientId) {
        SaveInstantorResponseCommand command = new SaveInstantorResponseCommand();
        command.setStatus(InstantorResponseStatus.FAILED);
        command.setClientId(clientId);
        return command;
    }
}

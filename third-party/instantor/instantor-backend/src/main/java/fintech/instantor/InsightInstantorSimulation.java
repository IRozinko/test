package fintech.instantor;

import fintech.JsonUtils;
import fintech.Validate;
import fintech.instantor.json.insight.InstantorInsightResponse;
import fintech.instantor.model.InstantorResponseStatus;
import fintech.instantor.model.SaveInstantorResponseCommand;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static fintech.BigDecimalUtils.amount;

public class InsightInstantorSimulation {

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateOkResponse(Long clientId, String dni, String name, String iban, String iban2) {
        @Cleanup InputStream input = new ClassPathResource("instantor-insight-fake.json").getInputStream();
        String json = IOUtils.toString(input, StandardCharsets.UTF_8);
        return simulateOkResponse(SimulateInstantorReq.builder()
            .clientId(clientId)
            .dni(dni)
            .name(name)
            .iban(iban)
            .iban2(iban2)
            .account1(iban)
            .account2(iban2)
            .averageAmountOfIncomingTransactionsMonth(amount(701))
            .averageAmountOfOutgoingTransactionsMonth(amount(600))
            .build(),
            json);
    }

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateOkResponse(SimulateInstantorReq request, String json) {
        Validate.notNull(request.clientId, "Null client id");
        json = StringUtils.replace(json, "#clientId#", request.clientId.toString());
        json = StringUtils.replace(json, "#dni#", request.dni);
        json = StringUtils.replace(json, "#name#", request.name);
        json = StringUtils.replace(json, "#iban#", request.iban);
        json = StringUtils.replace(json, "#iban2#", request.iban2);
        json = StringUtils.replace(json, "#iban3#", request.iban3);
        json = StringUtils.replace(json, "#account#", request.iban);
        json = StringUtils.replace(json, "#account2#", request.iban2);
        json = StringUtils.replace(json, "#account3#", request.iban3);

        json = StringUtils.replace(json, "#averageAmountOfIncomingTransactionsMonth#", request.averageAmountOfIncomingTransactionsMonth.toString());
        json = StringUtils.replace(json, "#averageAmountOfOutgoingTransactionsMonth#", request.averageAmountOfOutgoingTransactionsMonth.toString());

        SaveInstantorResponseCommand command = new SaveInstantorResponseCommand();
        command.setPayloadJson(json);
        command.setStatus(InstantorResponseStatus.OK);
        return command;
    }

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateOkResponseWithSingleAccount(Long clientId, String dni, String name, String iban) {
        return simulateOkResponseWithSingleAccount(clientId, dni, name, iban, amount(3550.92), amount(600.01));
    }

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateOkResponseWithSingleAccount(Long clientId, String dni, String name, String iban, BigDecimal averageAmountOfIncomingTransactionsMonth, BigDecimal averageAmountOfOutgoingTransactionsMonth) {
        Validate.notNull(clientId, "Null client id");
        @Cleanup InputStream input = new ClassPathResource("instantor-insight-fake-single-account.json").getInputStream();
        String json = IOUtils.toString(input, StandardCharsets.UTF_8);
        json = StringUtils.replace(json, "#clientId#", clientId.toString());
        json = StringUtils.replace(json, "#dni#", dni);
        json = StringUtils.replace(json, "#name#", name);
        json = StringUtils.replace(json, "#iban#", iban);
        json = StringUtils.replace(json, "#averageAmountOfIncomingTransactionsMonth#", averageAmountOfIncomingTransactionsMonth.toString());
        json = StringUtils.replace(json, "#averageAmountOfOutgoingTransactionsMonth#", averageAmountOfOutgoingTransactionsMonth.toString());

        SaveInstantorResponseCommand command = new SaveInstantorResponseCommand();
        command.setPayloadJson(json);
        command.setStatus(InstantorResponseStatus.OK);
        return command;
    }

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateOkResponseFromJson(Long clientId, String dni, String name, String iban, String jsonPayLoad) {
        Validate.notNull(clientId, "Null client id");
        InstantorInsightResponse json = JsonUtils.readValue(jsonPayLoad, InstantorInsightResponse.class);
        json.getMiscParams().stream().filter(e -> e.getName().equals("clientId")).forEach(e -> e.setValue(clientId.toString()));
        json.getUserDetails().getPersonalIdentifier().stream().filter(e -> e.getName().equals("dni")).forEach(e -> e.setValue(dni));
        json.getAccountList().forEach(a -> {
            a.setHolderName(name);
            a.setNumber(iban);
            a.setIban(iban);
        });
        json.getAccountReportList().forEach(a -> a.setNumber(iban));
        SaveInstantorResponseCommand command = new SaveInstantorResponseCommand();
        command.setPayloadJson(JsonUtils.writeValueAsString(json));
        command.setStatus(InstantorResponseStatus.OK);
        return command;
    }
}

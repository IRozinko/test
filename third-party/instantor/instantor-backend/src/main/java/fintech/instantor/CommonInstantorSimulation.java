package fintech.instantor;

import fintech.JsonUtils;
import fintech.Validate;
import fintech.instantor.json.common.AccountList;
import fintech.instantor.json.common.InstantorCommonResponse;
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

@Deprecated
public class CommonInstantorSimulation {

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateOkResponse(Long clientId, String dni, String name, String iban, String iban2) {
        return simulateOkResponse(clientId, dni, name, iban, iban2, amount(701), amount(600));
    }

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateOkResponse(Long clientId, String dni, String name, String iban, String iban2, BigDecimal averageAmountOfIncomingTransactionsMonth, BigDecimal averageAmountOfOutgoingTransactionsMonth) {
        Validate.notNull(clientId, "Null client id");
        @Cleanup InputStream input = new ClassPathResource("instantor-fake.json").getInputStream();
        String json = IOUtils.toString(input, StandardCharsets.UTF_8);
        json = StringUtils.replace(json, "#clientId#", clientId.toString());
        json = StringUtils.replace(json, "#dni#", dni);
        json = StringUtils.replace(json, "#name#", name);
        json = StringUtils.replace(json, "#iban#", iban);
        json = StringUtils.replace(json, "#iban2#", iban2);

        json = StringUtils.replace(json, "#averageAmountOfIncomingTransactionsMonth#", averageAmountOfIncomingTransactionsMonth.toString());
        json = StringUtils.replace(json, "#averageAmountOfOutgoingTransactionsMonth#", averageAmountOfOutgoingTransactionsMonth.toString());

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
        @Cleanup InputStream input = new ClassPathResource("instantor-fake-single-account.json").getInputStream();
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
    public static final SaveInstantorResponseCommand simulateOkResponseWithAmountOfLoans(Long clientId, String dni, String name, String iban, Integer thisMonthAmountOfLoans, Integer lastMonthAmountOfLoans) {
        Validate.notNull(clientId, "Null client id");
        @Cleanup InputStream input = new ClassPathResource("instantor-fake-with-amount-of-loans.json").getInputStream();
        String json = IOUtils.toString(input, StandardCharsets.UTF_8);
        json = StringUtils.replace(json, "#clientId#", clientId.toString());
        json = StringUtils.replace(json, "#dni#", dni);
        json = StringUtils.replace(json, "#name#", name);
        json = StringUtils.replace(json, "#iban#", iban);
        json = StringUtils.replace(json, "#thisMonthAmountOfLoans#", thisMonthAmountOfLoans.toString());
        json = StringUtils.replace(json, "#lastMonthAmountOfLoans#", lastMonthAmountOfLoans.toString());

        SaveInstantorResponseCommand command = new SaveInstantorResponseCommand();
        command.setPayloadJson(json);
        command.setStatus(InstantorResponseStatus.OK);
        return command;
    }

    @SneakyThrows
    public static final SaveInstantorResponseCommand simulateOkResponseFromJson(Long clientId, String dni, String name, String iban, String jsonPayLoad) {
        Validate.notNull(clientId, "Null client id");
        InstantorCommonResponse json = JsonUtils.readValue(jsonPayLoad, InstantorCommonResponse.class);
        json.getBasicInfo().getMiscEntryList().stream().filter(e -> e.getKey().equals("clientId")).forEach(e -> e.setValue(clientId.toString()));
        json.getBasicInfo().getMiscEntryList().stream().filter(e -> e.getKey().equals("dni")).forEach(e -> e.setValue(dni));
        for (AccountList a : json.getScrape().getAccountList()) {
            a.setHolderName(name);
            a.setNumber(iban);
            a.setIban(iban);
        }
        SaveInstantorResponseCommand command = new SaveInstantorResponseCommand();
        command.setPayloadJson(JsonUtils.writeValueAsString(json));
        command.setStatus(InstantorResponseStatus.OK);
        return command;
    }

    public static final SaveInstantorResponseCommand simulateFailResponse(Long clientId) {
        SaveInstantorResponseCommand command = new SaveInstantorResponseCommand();
        command.setStatus(InstantorResponseStatus.FAILED);
        command.setClientId(clientId);
        return command;
    }
}

package fintech.instantor;

import fintech.instantor.model.InstantorResponse;
import fintech.instantor.model.InstantorResponseQuery;
import fintech.instantor.model.InstantorTransaction;
import fintech.instantor.model.InstantorTransactionQuery;
import fintech.instantor.model.SaveInstantorResponseCommand;
import fintech.instantor.parser.InstantorParser;

import java.util.List;
import java.util.Optional;

public interface InstantorService {

    Long saveResponse(SaveInstantorResponseCommand command);

    void processResponse(Long responseId);

    void processingFailed(Long responseId);

    Optional<InstantorResponse> findLatest(InstantorResponseQuery query);

    InstantorResponse getResponse(Long responseId);

    void saveManualTransactionCategory(Long transactionId, String category);

    void saveNordigenTransactionCategory(Long transactionId, String category);

    List<InstantorTransaction> findTransactions(InstantorTransactionQuery query);

    void updateAccountData(Long responseId, String bankAccountNumber);

    void setInstantorParser(InstantorParser instantorParser);

    String getJsonPayload(Long responseId);
}

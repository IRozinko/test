package fintech.iovation;

import fintech.iovation.model.CheckTransactionCommand;
import fintech.iovation.model.IovationBlackboxQuery;
import fintech.iovation.model.IovationTransaction;
import fintech.iovation.model.SaveBlackboxCommand;

import java.util.Optional;

public interface IovationService {

    Long saveBlackbox(SaveBlackboxCommand command);

    Long checkTransaction(CheckTransactionCommand command);

    IovationTransaction getTransaction(Long id);

    Optional<String> findLatestBlackBox(IovationBlackboxQuery query);
}

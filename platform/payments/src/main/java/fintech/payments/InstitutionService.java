package fintech.payments;


import fintech.payments.commands.AddInstitutionCommand;
import fintech.payments.commands.UpdateInstitutionCommand;
import fintech.payments.model.Institution;
import fintech.payments.model.InstitutionAccount;

import java.util.List;
import java.util.Optional;

public interface InstitutionService {

    Long addInstitution(AddInstitutionCommand command);

    Institution getInstitution(Long id);

    Institution getInstitution(String code);

    InstitutionAccount getAccount(Long accountId);

    List<Institution> getAllInstitutions();

    Institution getPrimaryInstitution();

    Optional<InstitutionAccount> findAccountByNumber(String accountNumber);

    void updateInstitution(UpdateInstitutionCommand command);
}

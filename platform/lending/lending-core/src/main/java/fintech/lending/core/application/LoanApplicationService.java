package fintech.lending.core.application;

import fintech.lending.core.application.commands.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LoanApplicationService {

    LoanApplication get(Long applicationId);

    Optional<LoanApplication> findByUuid(String uuid);

    Long submit(SubmitLoanApplicationCommand command);

    void attachWorkflow(AttachWorkflowCommand command);

    void updateInquiry(UpdateInquiryCommand command);

    void updateOffer(LoanApplicationOfferCommand command);

    void updateStrategies(UpdateLoanAppStrategiesCommand command);

    void approveOffer(ApproveOfferCommand command);

    void approve(ApproveLoanApplicationCommand command);

    void updateStatusDetail(Long applicationId, String statusDetail);

    void reject(Long applicationId, String reason);

    void cancel(Long applicationId, String reason);

    void saveScore(SaveScoreCommand command);

    void saveCreditLimit(@Valid SaveCreditLimitCommand command);

    void updateInterestRate(@Valid UpdateLoanApplicationInterestRateCommand command);

    void saveParams(@Valid SaveParamsCommand command);

    Map<String, String> getParams(Long applicationId);

    List<LoanApplication> find(LoanApplicationQuery query);

    Optional<LoanApplication> findLatest(LoanApplicationQuery query);

    Optional<LoanApplication> findFirst(LoanApplicationQuery query);

    void addAttribute(Long applicationId, String key, String value);
}

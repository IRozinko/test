package fintech.lending.core.application;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@Accessors(chain = true)
public class LoanApplicationQuery {

    private Long clientId;
    private Long applicationId;
    private String longApproveCode;
    private String shortApproveCode;
    private LocalDate submittedDateFrom;
    private LocalDate submittedDateTo;
    private List<LoanApplicationType> types = new ArrayList<>();
    private List<LoanApplicationStatus> statuses = new ArrayList<>();
    private List<String> statusDetails = new ArrayList<>();

    public static LoanApplicationQuery byClientId(Long clientId, LoanApplicationStatus... statuses) {
        LoanApplicationQuery query = new LoanApplicationQuery();
        query.setClientId(clientId);
        query.setStatuses(Arrays.asList(statuses));
        return query;
    }

    public static LoanApplicationQuery byClientId(Long clientId, List<String> statusDetails, LoanApplicationStatus... statuses) {
        LoanApplicationQuery query = new LoanApplicationQuery();
        query.setClientId(clientId);
        query.setStatusDetails(statusDetails);
        query.setStatuses(Arrays.asList(statuses));
        return query;
    }

    public static LoanApplicationQuery byLongApproveCode(String longCode, LoanApplicationStatus... statuses) {
        LoanApplicationQuery query = new LoanApplicationQuery();
        query.setLongApproveCode(longCode);
        query.setStatuses(Arrays.asList(statuses));
        return query;
    }

    public static LoanApplicationQuery byShortApproveCode(Long clientId, String shortCode, LoanApplicationStatus... statuses) {
        LoanApplicationQuery query = new LoanApplicationQuery();
        query.setShortApproveCode(shortCode);
        query.setClientId(clientId);
        query.setStatuses(Arrays.asList(statuses));
        return query;
    }

    public static LoanApplicationQuery byType(Long clientId, LoanApplicationType type) {
        LoanApplicationQuery query = new LoanApplicationQuery();
        query.setClientId(clientId);
        query.getTypes().add(type);
        return query;
    }

    public static LoanApplicationQuery byType(Long clientId, LoanApplicationType type, LoanApplicationStatus... statuses) {
        LoanApplicationQuery query = new LoanApplicationQuery();
        query.setClientId(clientId);
        query.getTypes().add(type);
        query.setStatuses(Arrays.asList(statuses));
        return query;
    }

    public static LoanApplicationQuery byType(Long clientId, LoanApplicationType type, LoanApplicationStatus status, String statusDetail) {
        LoanApplicationQuery query = new LoanApplicationQuery();
        query.setClientId(clientId);
        query.getTypes().add(type);
        query.setStatuses(Collections.singletonList(status));
        query.setStatusDetails(Collections.singletonList(statusDetail));
        return query;
    }

    public static LoanApplicationQuery withdrawal(Long clientId, LoanApplicationStatus... statuses) {
        LoanApplicationQuery query = new LoanApplicationQuery();
        query.setClientId(clientId);
        query.getTypes().add(LoanApplicationType.FIRST_WITHDRAWAL);
        query.getTypes().add(LoanApplicationType.WITHDRAWAL);
        query.setStatuses(Arrays.asList(statuses));
        return query;
    }
}

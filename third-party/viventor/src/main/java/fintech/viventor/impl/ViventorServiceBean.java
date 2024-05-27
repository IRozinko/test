package fintech.viventor.impl;

import fintech.JsonUtils;
import fintech.viventor.*;
import fintech.viventor.db.ViventorLogEntity;
import fintech.viventor.db.ViventorLogRepository;
import fintech.viventor.impl.ViventorProvider.ViventorResponse;
import fintech.viventor.model.PostLoanExtensionRequest;
import fintech.viventor.model.PostLoanPaidRequest;
import fintech.viventor.model.PostLoanPaymentRequest;
import fintech.viventor.model.PostLoanRequest;
import fintech.viventor.model.ViventorLoan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

@Component
class ViventorServiceBean implements ViventorService {

    @Autowired
    private ViventorLogRepository viventorLogRepository;

    @Resource(name = "${viventor.provider:" + MockViventorProvider.NAME + "}")
    private ViventorProvider viventorProvider;

    @Autowired
    private TransactionTemplate txTemplate;

    @Override
    public ViventorLog postLoan(PostLoanCommand command) {
        PostLoanRequest postLoanRequest = new PostLoanRequest();
        postLoanRequest.setLoan(command.getLoan());
        postLoanRequest.setBorrower(command.getBorrower());
        postLoanRequest.setSchedule(command.getSchedule());
        ViventorResponse response = viventorProvider.postLoan(postLoanRequest);

        ViventorLogEntity logEntity = new ViventorLogEntity();
        logEntity.setLoanId(command.getLoanId());
        logEntity.setViventorLoanId(command.getLoan().getId());
        logEntity.setRequestType(ViventorRequestType.NEW_LOAN);
        logEntity.setRequestBody(JsonUtils.writeValueAsString(postLoanRequest));

        logResponse(logEntity, response);

        return logEntity.toValueObject();
    }

    @Override
    public ViventorLog postLoanPayment(PostLoanPaymentCommand command) {
        PostLoanPaymentRequest postLoanPaymentRequest = new PostLoanPaymentRequest(
            command.getNumber(),
            command.getActualDate()
        );
        ViventorResponse response = viventorProvider.postLoanPayment(command.getViventorLoanId(), postLoanPaymentRequest);

        ViventorLogEntity logEntity = new ViventorLogEntity();
        logEntity.setLoanId(command.getLoanId());
        logEntity.setViventorLoanId(command.getViventorLoanId());
        logEntity.setRequestType(ViventorRequestType.LOAN_PAYMENT);
        logEntity.setRequestBody(JsonUtils.writeValueAsString(postLoanPaymentRequest));

        logResponse(logEntity, response);
        return logEntity.toValueObject();
    }

    @Override
    public ViventorLog postLoanPaid(PostLoanPaidCommand command) {
        PostLoanPaidRequest request = new PostLoanPaidRequest(command.getDate());
        ViventorResponse response = viventorProvider.postLoanPaid(command.getViventorLoanId(), request);

        ViventorLogEntity logEntity = new ViventorLogEntity();
        logEntity.setLoanId(command.getLoanId());
        logEntity.setViventorLoanId(command.getViventorLoanId());
        logEntity.setRequestType(ViventorRequestType.LOAN_PAID);
        logEntity.setRequestBody(JsonUtils.writeValueAsString(request));

        logResponse(logEntity, response);

        return logEntity.toValueObject();
    }

    @Override
    public ViventorLog postLoanExtension(PostLoanExtensionCommand command) {
        PostLoanExtensionRequest request = new PostLoanExtensionRequest(command.getMaturityDate());
        ViventorResponse response = viventorProvider.postLoanExtension(command.getViventorLoanId(), request);

        ViventorLogEntity logEntity = new ViventorLogEntity();
        logEntity.setLoanId(command.getLoanId());
        logEntity.setViventorLoanId(command.getViventorLoanId());
        logEntity.setRequestType(ViventorRequestType.LOAN_EXTENSION);
        logEntity.setRequestBody(JsonUtils.writeValueAsString(request));

        logResponse(logEntity, response);
        return logEntity.toValueObject();
    }

    @Override
    public GetLoanResult getLoan(GetLoanCommand command) {
        ViventorResponse response = viventorProvider.getLoan(command.getViventorLoanId());

        ViventorLogEntity logEntity = new ViventorLogEntity();
        logEntity.setLoanId(command.getLoanId());
        logEntity.setViventorLoanId(command.getViventorLoanId());
        logEntity.setRequestType(ViventorRequestType.GET_LOAN);
        logEntity.setRequestBody(null);
        logEntity.setRequestUrl(response.getUrl());
        logEntity.setResponseBody(response.getResponseBody());
        logEntity.setResponseStatusCode(response.getResponseStatusCode());
        logEntity.setStatus(response.getStatus());

        ViventorLoan viventorLoan = JsonUtils.readValue(response.getResponseBody(), ViventorLoan.class);
        return new GetLoanResult(logEntity.toValueObject(), viventorLoan);
    }

    private void logResponse(ViventorLogEntity logEntity, ViventorResponse response) {
        logEntity.setRequestUrl(response.getUrl());
        logEntity.setResponseBody(response.getResponseBody());
        logEntity.setResponseStatusCode(response.getResponseStatusCode());
        logEntity.setStatus(response.getStatus());

        txTemplate.execute((status) -> {
            viventorLogRepository.saveAndFlush(logEntity);
            return true;
        });
    }

}

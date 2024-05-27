package fintech.viventor.impl;

import fintech.viventor.ViventorResponseStatus;
import fintech.viventor.model.PostLoanExtensionRequest;
import fintech.viventor.model.PostLoanPaidRequest;
import fintech.viventor.model.PostLoanPaymentRequest;
import fintech.viventor.model.PostLoanRequest;
import lombok.Data;
import lombok.ToString;

public interface ViventorProvider {

    ViventorResponse postLoan(PostLoanRequest postLoanRequest);

    ViventorResponse postLoanPaid(String viventorLoanId, PostLoanPaidRequest postLoanPaidRequest);

    ViventorResponse postLoanPayment(String viventorLoanId, PostLoanPaymentRequest postLoanPaidRequest);

    ViventorResponse postLoanExtension(String viventorLoanId, PostLoanExtensionRequest postLoanExtensionRequest);

    ViventorResponse getLoan(String viventorLoanId);

    @Data
    @ToString(exclude = {"responseBody"})
    class ViventorResponse {
        private ViventorResponseStatus status;
        private int responseStatusCode;
        private String error;
        private String responseBody;
        private String url;

        public static ViventorResponse error(String url, int statusCode, String responseBody, String error) {
            ViventorResponse response = new ViventorResponse();
            response.setUrl(url);
            response.setStatus(ViventorResponseStatus.FAILED);
            response.setResponseStatusCode(statusCode);
            response.setResponseBody(responseBody);
            response.setError(error);
            return response;
        }

        public static ViventorResponse ok(String url, int statusCode, String responseBody) {
            ViventorResponse response = new ViventorResponse();
            response.setUrl(url);
            response.setStatus(ViventorResponseStatus.OK);
            response.setResponseStatusCode(statusCode);
            response.setResponseBody(responseBody);
            return response;
        }
    }

}

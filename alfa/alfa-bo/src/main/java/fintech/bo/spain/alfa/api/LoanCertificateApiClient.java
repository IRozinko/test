package fintech.bo.spain.alfa.api;

import fintech.bo.api.model.CloudFile;
import fintech.spain.alfa.bo.model.LoanCertificateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LoanCertificateApiClient {

    @POST("/api/bo/loan/{loanId}/certificate")
    Call<CloudFile> generateCertificate(@Path("loanId") Long loanId,
                                        @Body LoanCertificateRequest request);
}

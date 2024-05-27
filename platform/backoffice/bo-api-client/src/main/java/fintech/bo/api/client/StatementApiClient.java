package fintech.bo.api.client;

import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.statements.ImportStatementRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface StatementApiClient {

    @POST("/api/bo/statements/add")
    Call<IdResponse> importStatement(@Body ImportStatementRequest request);

}

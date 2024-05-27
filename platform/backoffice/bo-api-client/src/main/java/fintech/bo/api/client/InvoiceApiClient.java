package fintech.bo.api.client;

import fintech.bo.api.model.invoice.GenerateInvoiceRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface InvoiceApiClient {

    @POST("api/bo/invoices/generate")
    Call<Void> generateInvoice(@Body GenerateInvoiceRequest request);

}

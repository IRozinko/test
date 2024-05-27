package fintech.mock;

import okhttp3.Headers;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MockCall<T> implements Call<T> {

    private final T body;
    private final Headers headers;

    public MockCall(T body) {
        this.body = body;
        this.headers = new Headers.Builder().build();
    }

    public MockCall(T body, Headers headers) {
        this.body = body;
        this.headers = headers;
    }

    @Override
    public Response<T> execute() {
        return Response.success(body, headers);
    }

    @Override
    public void enqueue(Callback<T> callback) {
    }

    @Override
    public boolean isExecuted() {
        return true;
    }

    @Override
    public void cancel() {
    }

    @Override
    public boolean isCanceled() {
        return true;
    }

    @Override
    public Call<T> clone() {
        return new MockCall<>(this.body, this.headers);
    }

    @Override
    public Request request() {
        return null;
    }
}

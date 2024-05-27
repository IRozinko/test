package fintech.bo.components.background;


import com.vaadin.ui.UI;
import fintech.bo.components.api.ApiCallException;
import fintech.bo.components.security.LoginService;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
public class BackgroundOperations {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static <T> void run(String title, BackgroundOperation<T> operation, Consumer<T> onSuccess, Consumer<Exception> onError) {
        final GlassPanel glassPanel = new GlassPanel(title);
        final UI ui = UI.getCurrent();
        ui.addWindow(glassPanel);
        final LoginService.LoginData loginData = LoginService.getLoginData();
        executorService.submit(() -> {
            LoginService.setThreadLocalLoginData(loginData);
            log.debug("Executing request as user [{}]", loginData == null ? "NO USER" : loginData.getUser());
            try {
                final T value = operation.run(glassPanel);
                ui.access(() -> onSuccess.accept(value));
            } catch (Exception e) {
                log.error("Error while running background operation", e);
                ui.access(() -> onError.accept(e));
            } finally {
                LoginService.cleanThreadLocalLoginData();
                ui.access(glassPanel::close);
            }
        });
    }

    public static <T> void runSilent(BackgroundOperation<T> operation, Consumer<T> onSuccess, Consumer<Exception> onError) {
        final UI ui = UI.getCurrent();
        final LoginService.LoginData loginData = LoginService.getLoginData();
        executorService.submit(() -> {
            LoginService.setThreadLocalLoginData(loginData);
            log.debug("Executing request as user [{}]", loginData == null ? "NO USER" : loginData.getUser());
            try {
                final T value = operation.run();
                ui.access(() -> onSuccess.accept(value));
            } catch (Exception e) {
                log.error("Error while running background operation", e);
                ui.access(() -> onError.accept(e));
            } finally {
                LoginService.cleanThreadLocalLoginData();
            }
        });
    }

    public static <T> void callApi(String title, Call<T> call, Consumer<T> onSuccess, Consumer<Exception> onError) {
        run(title, feedback -> {
            log.info("Executing  API request: {}", call.request().url().toString());
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                return throwOnRequestFailed(title, response);
            }
        }, onSuccess, onError);
    }

    public static <T> T throwOnRequestFailed(String title, Response<T> response) throws IOException {
        String errorBodyString = response.errorBody().string();
        log.error("API request failed: {}", errorBodyString);
        throw new ApiCallException(String.format("%s: %s", title, "Request failed"), errorBodyString);
    }

    public static <T> void callApiSilent(Call<T> call, Consumer<T> onSuccess, Consumer<Exception> onError) {
        runSilent(feedback -> {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                String errorBodyString = response.errorBody().string();
                log.error("API request failed: {}", errorBodyString);
                throw new ApiCallException(String.format("%s", "Request failed"), errorBodyString);
            }
        }, onSuccess, onError);
    }
}

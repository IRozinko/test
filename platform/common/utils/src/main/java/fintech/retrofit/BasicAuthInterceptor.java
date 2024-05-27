package fintech.retrofit;

import okhttp3.Credentials;

public class BasicAuthInterceptor extends HeaderInterceptor {

    public BasicAuthInterceptor(String user, String password) {
        super("Authorization", Credentials.basic(user, password));
    }

}

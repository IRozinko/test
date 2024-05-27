package fintech.bo.api.client;

import fintech.bo.api.model.users.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsersApiClient {

    @POST("api/bo/users/update")
    Call<Void> updateUser(@Body UpdateUserRequest request);

    @POST("api/bo/users/add")
    Call<Void> addUser(@Body AddUserRequest request);

    @POST("api/bo/users/remove")
    Call<Void> removeUser(@Body RemoveUserRequest request);

    @POST("api/bo/users/change-password")
    Call<Void> changePassword(@Body ChangePasswordRequest request);

    @POST("api/bo/users/change-my-password")
    Call<Void> changeMyPassword(@Body ChangeMyPasswordRequest request);

}

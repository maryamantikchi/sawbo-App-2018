package edu.illinois.entm.sawbodeployer.UserActivity;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;


/**
 * Created by Mahsa on 7/7/2017.
 */

public interface IUserLogs {
    @Headers("Content-Type:application/json")
    @POST("prod/factors")
    public Call<UserActivities> CreateProduct(@Body List<UserActivities> userActivities);


}

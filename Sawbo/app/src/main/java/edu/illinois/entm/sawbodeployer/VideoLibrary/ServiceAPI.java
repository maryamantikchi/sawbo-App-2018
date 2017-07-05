package edu.illinois.entm.sawbodeployer.VideoLibrary;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by navneet on 4/6/16.
 */
public interface ServiceAPI {


    @GET("prod/factors/")
    Call<Video> getVideoDetails();
}

package edu.illinois.entm.sawbodeployer.VideoLibrary;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by navneet on 4/6/16.
 */
public interface ServiceAPI {


    @GET("/prod/topic")
    Call<ArrayList<topic>> getVideoTopics();
    @GET("prod/video/getvideobylanguage")
    Call<ArrayList<Video>> getVideoByLanguages(@Query("language") String language);
}

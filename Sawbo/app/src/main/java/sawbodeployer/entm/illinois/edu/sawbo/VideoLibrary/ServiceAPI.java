package sawbodeployer.entm.illinois.edu.sawbo.VideoLibrary;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Url;

/**
 * Created by navneet on 4/6/16.
 */
public interface ServiceAPI {


    @GET("mobileApp.php")
    Call<Video> getVideoDetails();
}

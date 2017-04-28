package sawbodeployer.entm.illinois.edu.sawbo.VideoLibrary;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by navneet on 4/6/16.
 */
public interface ServiceAPI {


    @GET("mobileApp.php")
    Call<Video> getVideoDetails();
}

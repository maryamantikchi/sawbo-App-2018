package sawbodeployer.entm.illinois.edu.sawbo.VideoLibrary;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by navneet on 4/6/16.
 */
public interface ServiceAPI {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
    */
    @GET("mobile_app/totallist_sources_test.php")
    Call<Video> getVideoDetails();
}

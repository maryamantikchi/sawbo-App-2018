package sawbodeployer.entm.illinois.edu.sawbo;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Mahsa on 4/25/2017.
 */

public interface logService {
    @GET("ip.php")
    Call<String> getIp();
}

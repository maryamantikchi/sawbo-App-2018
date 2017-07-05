package edu.illinois.entm.sawbodeployer.UserActivity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.NetworkOnMainThreadException;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

import edu.illinois.entm.sawbodeployer.UserActivityDB.UserActivityDataSource;
import edu.illinois.entm.sawbodeployer.logService;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Mahsa on 7/4/2017.
 */

public class HelperActivity {
    Context context;
    private String appID = "googleplay";
    UserActivityDataSource dataSource;

    public HelperActivity(Context context){
        this.context = context;
    }

    public void WriteUsrActivity(UserActivities activities,Activity act){
        dataSource = new UserActivityDataSource(context);
        dataSource.open();
        activities.setUsrid(getdID());
        activities.setGPS(getGPS(act));
        activities.setAppid(appID);
        activities.setTimestamp(getTimeStamp());
        dataSource.createUserActivity(activities);
        dataSource.close();
    }

    private String[] getGPS(Activity act) {
        LocationManager lm = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
        final Criteria criteria = new Criteria();

        Boolean acc = getAcc(act);
        String[] gps = new String[2];
        if (!acc) {
            return gps;
        }
        //Alt
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        List<String> providers = lm.getProviders(criteria, true);

        Location l = null;

        for (int i = providers.size() - 1; i >= 0; i--) {
            if (ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                gps[0] = "0";
                gps[1] = "0";
                return gps;
            }
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        if (l != null) {
            gps[0] = String.valueOf(l.getLatitude());
            gps[1] = String.valueOf(l.getLongitude());
        }
        return gps;
    }

    public Boolean getAcc (Activity act){
        SharedPreferences sharedPref = act.getPreferences(Context.MODE_PRIVATE);
        boolean gpsOk = sharedPref.getBoolean("usegps", true);
        Log.v("location", ""+gpsOk);
        if (gpsOk) {
            return true;
        }else{
            return false;
        }
    }

    private String getIP(){
        try {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://www.sawbo-illinois.org/mobile_app/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

            logService service = retrofit.create(logService.class);
            final Call<String> call = service.getIp();
            return call.execute().body();
        }catch (IOException e){
            return "";
        }catch (NetworkOnMainThreadException e){
            return "";
        }
    }

    public String getdID() {
        String result = "";
        String url = context.getFilesDir() + "/id.txt";
        File file = new File(url);
        if(file.exists()){
            Log.v("getdID", "exists");
            try {
                FileInputStream fis = context.openFileInput("id.txt");
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                String line;
                line = bufferedReader.readLine();
                result = line;
            }catch (IOException e){
                Log.v("Error", ":(");
            }
        }else {
            Log.v("getdID", "new");

            //write ID to file
            try {
                result = BluetoothAdapter.getDefaultAdapter().getAddress();
            }catch (NullPointerException e){
                Log.v("Error", e.toString());
            }
            if(result.isEmpty()) {
                TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                result = tManager.getDeviceId();
                if(result.isEmpty() || result.equals("000000000000000")){
                    Random r = new Random();
                    int i1 = r.nextInt(99999999 - 10000000) + 10000000;
                    result = Integer.toString(i1);
                }
            }
            try {
                FileOutputStream fos = context.openFileOutput("id.txt", Context.MODE_APPEND);
                fos.write(result.getBytes());
                fos.close();
            } catch (IOException e){
                Log.e("error", e.toString());
            }
        }
        Log.v("Return", result);
        return result;
    }

    public String getTimeStamp(){
        String timeStamp = String.valueOf(System.currentTimeMillis());
        return timeStamp;
    }


}

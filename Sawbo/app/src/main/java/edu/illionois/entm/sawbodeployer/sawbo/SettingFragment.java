package edu.illionois.entm.sawbodeployer.sawbo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rey.material.widget.TextView;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by Mahsa on 4/21/2017.
 */

public class SettingFragment extends android.support.v4.app.Fragment {
    View view;
    TextView video_library,setting;
    Button language,gps,data;
    public static final int RequestPermissionCode = 1;


    public SettingFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_settings, container, false);
        initialize();
        return view;
    }

    public void initialize(){
        Typeface bebasNeue = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BebasNeue.otf");
        Typeface BentonSans = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Medium.otf");

        video_library = (TextView)view.findViewById(R.id.title_video_setting);
        video_library.setTypeface(bebasNeue);

        setting = (TextView)view.findViewById(R.id.title_setting);
        setting.setTypeface(bebasNeue);

        language = (Button)view.findViewById(R.id.setting_language);
        language.setTypeface(BentonSans);

        gps = (Button)view.findViewById(R.id.setting_gps);
        gps.setTypeface(BentonSans);

        data = (Button)view.findViewById(R.id.setting_data);
        data.setTypeface(BentonSans);

        final CharSequence[] choice = {"NO","YES"};


        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setSingleChoiceItems(choice, 0, null)
                        .setTitle("Would you allow us to track your location using GPS? ")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();

                                if (selectedPosition==1){
                                    if (checkPermission()){
                                        putPref("usegps", true, getContext());

                                    }else {
                                        requestPermission();
                                    }

                                }else {
                                    putPref("usegps", false, getContext());

                                }


                            }
                        })
                        .show();
            }
        });
    }

    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getContext(), ACCESS_COARSE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(getActivity(), new String[]
                {
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION
                }, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean AccessFinePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean AccessCoarseLocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (AccessFinePermission && AccessCoarseLocationPermission ) {
                        putPref("usegps", true, getContext());
                    }
                    else {
                        Toast.makeText(getContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                        putPref("usegps", false, getContext());

                    }
                }

                break;
        }
    }

    public static void putPref(String key, Boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

}

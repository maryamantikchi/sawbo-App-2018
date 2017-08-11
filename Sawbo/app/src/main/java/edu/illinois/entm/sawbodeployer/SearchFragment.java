package edu.illinois.entm.sawbodeployer;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.entm.sawbodeployer.LogFileDB.LogFileDBHelperAssets;
import edu.illinois.entm.sawbodeployer.LogFileDB.LogVideoSource;
import edu.illinois.entm.sawbodeployer.VideoLibrary.ServiceAPI;
import edu.illinois.entm.sawbodeployer.VideoLibrary.Video;
import edu.illinois.entm.sawbodeployer.VideoLibrary.VideoLibraryFragment;
import edu.illinois.entm.sawbodeployer.VideoLibrary.VideoListAdapter;
import edu.illinois.entm.sawbodeployer.VideoLibrary.all;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Mahsa on 6/12/2017.
 */

public class SearchFragment extends android.support.v4.app.Fragment {
    View view;

    LinearLayout clear,search,main;
    TextView search_title,clear_title,title_video,title_filter;
    Button country,language,topic;
    Video videoModel = new Video();
    ProgressBar pbar;

    AsyncTask<Void, Void, Void> getData;
    ArrayList<String> selected_topic = new ArrayList<>();
    ArrayList<String> selected_language = new ArrayList<>();
    ArrayList<String> selected_country = new ArrayList<>();
    private LogVideoSource dataSource;

    public SearchFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        initialize();
        getData = new SearchFragment.getData().execute();
        return view;
    }

    public void initialize() {
        Typeface BentonSans = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Medium.otf");

        Typeface title_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BebasNeue.otf");

        title_video = (TextView)view.findViewById(R.id.title_video_search);
        title_filter = (TextView)view.findViewById(R.id.title_search_filter);
        title_video.setTypeface(title_font);
        title_filter.setTypeface(title_font);

        clear = (LinearLayout) view.findViewById(R.id.btn_search_clear_filters);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_country.clear();
                selected_language.clear();
                selected_topic.clear();
            }
        });
        search = (LinearLayout)view.findViewById(R.id.btn_search_filters);


        main = (LinearLayout)view.findViewById(R.id.main_search);
        pbar = (ProgressBar)view.findViewById(R.id.progress_bar_search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VideoLibraryFragment fragment = new VideoLibraryFragment();
                fragment.selected_country = selected_country;
                fragment.selected_topic = selected_topic;
                fragment.selected_language = selected_language;
                fragment.videoModel = videoModel;

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit();

            }
        });

        search_title = (TextView)view.findViewById(R.id.search_title);
        search_title.setTypeface(BentonSans);
        clear_title = (TextView)view.findViewById(R.id.clear_title);
        clear_title.setTypeface(BentonSans);

        country = (Button)view.findViewById(R.id.btn_search_country);
        language = (Button)view.findViewById(R.id.btn_search_langueage);
        topic = (Button)view.findViewById(R.id.btn_search_topics);

        country.setTypeface(BentonSans);
        language.setTypeface(BentonSans);
        topic.setTypeface(BentonSans);


        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              ShowAlertDialogWithListview(videoModel.getCountry(),"Country");
            }
        });

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ShowAlertDialogWithListview(videoModel.getLanguage(),"Language");

            }
        });

        topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              ShowAlertDialogWithListview(videoModel.getTopic(),"Topic");

            }
        });

    }

    public void getRetrofitObject() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://349j0nfab0.execute-api.us-west-2.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiceAPI service = retrofit.create(ServiceAPI.class);
        final Call<Video> call = service.getVideoDetails();
        try {
            videoModel = call.execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void ShowAlertDialogWithListview(List<String> list, final String title) {
        final CharSequence[] dialogList = list.toArray(new String[list.size()]);
        final android.app.AlertDialog.Builder builderDialog = new android.app.AlertDialog.Builder(getContext());
        builderDialog.setTitle("Select "+title);
        int count = dialogList.length;
       // final ArrayList<String>selectedTopic = new ArrayList<String>();


        boolean[] is_checked = new boolean[count];

        builderDialog.setMultiChoiceItems(dialogList, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton, boolean isChecked) {
                    }
                });

        builderDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView list = ((android.app.AlertDialog) dialog).getListView();

                        for (int i = 0; i < list.getCount(); i++) {
                            boolean checked = list.isItemChecked(i);

                            if (checked) {
                                //selectedTopic.add((String) list.getItemAtPosition(i));
                                if (title.equals("Topic")){
                                    selected_topic.add((String) list.getItemAtPosition(i));
                                }else if(title.equals("Country")){
                                    selected_country.add((String) list.getItemAtPosition(i));
                                }else if (title.equals("Language")){
                                    selected_language.add((String) list.getItemAtPosition(i));

                                }

                            }
                        }
                    }
                });
        android.app.AlertDialog alert = builderDialog.create();
        alert.show();

    }


    private class getData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pbar.setVisibility(View.GONE);
            main.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("OCRTask","extracting..");
            if (isOnline(getContext())){
                getRetrofitObject();
            }else {
                getVideoFromDB();
            }
            return null;
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


    private void getVideoFromDB(){
        dataSource = new LogVideoSource(getContext());
        dataSource.open();
        List<all> videos = dataSource.getAllVideos();
        videoModel = new Video();
        if (videos.size()!=0){
            videoModel.setCountry(dataSource.getAllCountry());
            videoModel.setLanguage(dataSource.getAllLanguage());
            videoModel.setTopic(dataSource.getAllTopics());
            videoModel.setAll(videos);
            dataSource.close();
        }else{
            LogFileDBHelperAssets dbAssets = new LogFileDBHelperAssets(getContext(),getActivity().getFilesDir().getAbsolutePath());
            try {
                dbAssets.prepareDatabase();
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }

            videoModel.setAll(dbAssets.getAllVideos());
            videoModel.setCountry(dbAssets.getCountries());
            videoModel.setLanguage(dbAssets.getLanguages());
            videoModel.setTopic(dbAssets.getTopics());
        }

    }
}

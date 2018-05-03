package edu.illinois.entm.sawbodeployer.VideoLibrary;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import edu.illinois.entm.sawbodeployer.LogFileDB.LogVideoSource;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import edu.illinois.entm.sawbodeployer.R;

/**
 * Created by Mahsa on 4/1/2017.
 */
public class VideoLibraryFragment extends android.support.v4.app.Fragment {
    public VideoLibraryFragment(){
    }

   public ArrayList<Video> videoModel;
    ProgressBar progressBar;
    AsyncTask<Void, Void, Void> getData;
    RecyclerView videoList;
    VideoListAdapter adapter;
    Button choose_topic,clear_filters;
    TextView title_list,title_filters,title;
    FragmentManager fragmentManager;
    private LogVideoSource dataSource;
    ArrayList<topic> videoTopics = new ArrayList<>();


    public ArrayList<String>selected_topic,selected_country,selected_language;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_library, container, false);

        //videoModel = new Video();


        fragmentManager = getFragmentManager();

        Typeface title_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BebasNeue.otf");
        Typeface title_list_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Medium.otf");
        Typeface btn_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Regular.otf");

        title = (TextView)view.findViewById(R.id.title_video_library);
        title.setTypeface(title_font);
        title_list = (TextView)view.findViewById(R.id.title_agriculture);
        title_list.setTypeface(title_list_font);
        title_filters = (TextView)view.findViewById(R.id.title_video_library_filters);
        title_filters.setTypeface(title_font);

        clear_filters = (Button)view.findViewById(R.id.btn_video_clear_filters);

        clear_filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(VideoLibraryFragment.this).attach(VideoLibraryFragment.this).commit();*/

                adapter = new VideoListAdapter(videoModel,getContext(),fragmentManager,VideoLibraryFragment.this);
                videoList.setAdapter(adapter);
                choose_topic.setVisibility(View.VISIBLE);
                title_list.setVisibility(View.VISIBLE);
                title.setTextColor(getResources().getColor(R.color.black));
                //title_filters.animate().alpha(0.0f).setDuration(1000);
                title_filters.animate()
                        .translationY(title_filters.getHeight())
                        .alpha(0.0f)
                        .setDuration(300);


                videoList.setVisibility(View.VISIBLE);
                clear_filters.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);


            }
        });



        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar_video_library);


        videoList = (RecyclerView)view.findViewById(R.id.recycler_view_video_library);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        videoList.setLayoutManager(mLayoutManager);
        videoList.setItemAnimator(new DefaultItemAnimator());
        videoList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
       /* videoList.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new   RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                       //Toast.makeText(getContext(),position+"+++",Toast.LENGTH_SHORT).show();
                   *//*     VideoDetailFragment fragment = new VideoDetailFragment();
                        fragment.videoDetail = videoModel.getAll().get(position);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_container, fragment).addToBackStack(null).commit();
*//*
                        VideoDetailFragment fragment = new VideoDetailFragment();
                        fragment.videoDetail = videoModel.getAll().get(position);
                        fragment.video = videoModel;

                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().hide(VideoLibraryFragment.this)
                                .add(R.id.main_container, fragment)
                                .addToBackStack(null)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();

                    }
                })
        );*/

        //getRetrofitObject();
        getData = new getData().execute();

        choose_topic = (Button)view.findViewById(R.id.btn_video_library_category);
        choose_topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertDialogWithListview();
            }
        });

        return view;
    }

   public void getRetrofitObject() {

       videoModel = new ArrayList<>();
       OkHttpClient client = new OkHttpClient();
       client.setConnectTimeout(50, TimeUnit.SECONDS);
       client.setReadTimeout(50, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://4ict6dhhr1.execute-api.us-east-1.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create()).client(client)
                .build();


        ServiceAPI service = retrofit.create(ServiceAPI.class);
        final Call<ArrayList<Video>> call = service.getVideoByLanguages("English");
        final Call<ArrayList<topic>> call_topics = service.getVideoTopics();
        try {

           videoModel = call.execute().body();
           videoTopics = call_topics.execute().body();
            Collections.sort(videoModel);
           adapter = new VideoListAdapter(videoModel,getContext(),fragmentManager,VideoLibraryFragment.this);
            delete_all_video();
           // addDataBase(videoModel);

        } catch (Exception e) {
           e.printStackTrace();
        }

   }

    private class getData extends AsyncTask<Void, Void, Void> {
        Set<all> filtered_list;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            videoList.setAdapter(adapter);
            videoList.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
           // if (videoModel!=null){

            if (selected_country!=null||selected_language!=null||selected_topic!=null) {

                choose_topic.setEnabled(true);
                choose_topic.setVisibility(View.GONE);
                clear_filters.setVisibility(View.VISIBLE);

            }else {
                choose_topic.setEnabled(true);
                choose_topic.setVisibility(View.VISIBLE);
                clear_filters.setVisibility(View.GONE);
            }

           // }
        /*else if(filtered_list!=null) {
                choose_topic.setEnabled(true);
                choose_topic.setVisibility(View.GONE);
                clear_filters.setVisibility(View.VISIBLE);
            }*/
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (isOnline(getContext()) && (selected_language == null && selected_topic == null &&
                    selected_country == null)){
                getRetrofitObject();

            }else if(!isOnline(getContext()) && (selected_language == null && selected_topic == null &&
                    selected_country == null) ){

            getVideoFromDB();

        } else{
                filtered_list=new HashSet<>();
                if (selected_language.size() > 0 || selected_topic.size() > 0|| selected_country.size() > 0) {
//                    for (int i=0;i<videoModel.getAll().size();i++){
//
//                        if (selected_language.size()!=0){
//                            for (int j=0;j<selected_language.size();j++) {
//                                if (selected_country.size() == 0) {
//                                    if (selected_topic.size() != 0){
//                                        for (int t = 0; t < selected_topic.size(); t++) {
//                                            if (videoModel.getAll().get(i).getLanguage().equals(selected_language.get(j))
//                                                    && videoModel.getAll().get(i).getTopic().equals(selected_topic.get(t))) {
//                                                filtered_list.add(videoModel.getAll().get(i));
//                                            }
//                                        }
//                                    }else{
//
//                                        if (videoModel.getAll().get(i).getLanguage().equals(selected_language.get(j))) {
//                                            filtered_list.add(videoModel.getAll().get(i));
//                                        }
//                                    }
//                                } else {
//
//                                    for (int c = 0; c < selected_country.size(); c++) {
//
//                                        if (selected_topic.size()==0){
//                                            if (videoModel.getAll().get(i).getLanguage().equals(selected_language.get(j))
//                                                    && videoModel.getAll().get(i).getCountry().equals(selected_country.get(c))) {
//                                                filtered_list.add(videoModel.getAll().get(i));
//                                            }
//                                        }else {
//                                            for (int t = 0; t < selected_topic.size(); t++) {
//                                                if (videoModel.getAll().get(i).getLanguage().equals(selected_language.get(j))
//                                                        && videoModel.getAll().get(i).getCountry().equals(selected_country.get(c))
//                                                        && videoModel.getAll().get(i).getTopic().equals(selected_topic.get(t))) {
//                                                    filtered_list.add(videoModel.getAll().get(i));
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }else {
//
//                            if (selected_country.size()!=0){
//
//                                for (int c = 0; c < selected_country.size(); c++) {
//
//                                    if (selected_topic.size()==0){
//                                        if (videoModel.getAll().get(i).getCountry().equals(selected_country.get(c))) {
//                                            filtered_list.add(videoModel.getAll().get(i));
//                                        }
//                                    }else {
//                                        for (int t = 0; t < selected_topic.size(); t++) {
//                                            if ( videoModel.getAll().get(i).getCountry().equals(selected_country.get(c))
//                                                    && videoModel.getAll().get(i).getTopic().equals(selected_topic.get(t))) {
//                                                filtered_list.add(videoModel.getAll().get(i));
//                                            }
//                                        }
//                                    }
//                                }
//                            }else{
//                                if (selected_topic.size()!=0){
//                                    for (int t = 0; t < selected_topic.size(); t++) {
//                                        if ( videoModel.getAll().get(i).getTopic().equals(selected_topic.get(t))) {
//                                            filtered_list.add(videoModel.getAll().get(i));
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                    }
                }


                ArrayList<all> alls = new ArrayList<>(filtered_list);

//                Video filtered_video = new Video();
//                filtered_video.setAll(alls);
//                filtered_video.setLanguage(selected_language);
//                filtered_video.setCountry(selected_country);
//                filtered_video.setTopic(selected_topic);
//                adapter = new VideoListAdapter(filtered_video, getContext(), fragmentManager, VideoLibraryFragment.this);
            }

            return null;

        }
    }

    private void ShowAlertDialogWithListview() {
        List<String> mtopics = new ArrayList<>();
        final Video selected_video = new Video();
        //mtopics = videoModel.getTopic();
        for (topic to:videoTopics ) {
            mtopics.add(to.getTopic());
        }
        final CharSequence[] dialogList = mtopics.toArray(new String[mtopics.size()]);
        final android.app.AlertDialog.Builder builderDialog = new android.app.AlertDialog.Builder(getContext());
        builderDialog.setTitle("Select Topic");
        int count = dialogList.length;


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
                        videoList.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        ListView list = ((android.app.AlertDialog) dialog).getListView();
                       // StringBuilder stringBuilder = new StringBuilder();
                        List<String>selectedTopic = new ArrayList<String>();
                        for (int i = 0; i < list.getCount(); i++) {
                            boolean checked = list.isItemChecked(i);

                            if (checked) {
                                //if (stringBuilder.length() > 0) stringBuilder.append(",");
                                //stringBuilder.append(list.getItemAtPosition(i));
                                selectedTopic.add((String) list.getItemAtPosition(i));
                            }
                        }


                        for (int i=0;i<selectedTopic.size();i++){
//                            for (int j=0;j<videoModel.getAll().size();j++){
//                                if (selectedTopic.get(i).equals(videoModel.getAll().get(j).getTopic())){
//                                    selected_video.getAll().add(videoModel.getAll().get(j));
//                                }
//                            }
                        }

                        //selected_video.setLanguage(videoModel.getLanguage());

                      //  adapter = new VideoListAdapter(selected_video,getContext(),fragmentManager,VideoLibraryFragment.this);
                        videoList.setAdapter(adapter);
                        choose_topic.setVisibility(View.GONE);
                        title_list.setVisibility(View.GONE);
                        title.setTextColor(getResources().getColor(R.color.gray_text));
                        title_filters.setVisibility(View.VISIBLE);
                        title_filters.animate()
                                .translationY(0)
                                .alpha(1.0f)
                                .setDuration(300);
                        videoList.setVisibility(View.VISIBLE);
                        clear_filters.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);


                    }
                });
        android.app.AlertDialog alert = builderDialog.create();
        alert.show();
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

    private void addDataBase(Video videoModel){
        dataSource = new LogVideoSource(getContext());
        dataSource.open();

//        for (all video:videoModel.getAll()) {
//            dataSource.createVideo(video);
//
//        }
//
//        for (String topic:videoModel.getTopic()){
//            dataSource.createTopic(topic);
//
//        }
//        for (String language:videoModel.getLanguage()){
//            dataSource.createLanguage(language);
//
//        }
//        for (String country:videoModel.getCountry()){
//            dataSource.createCountry(country);
//        }
        dataSource.close();
    }

    private void delete_all_video(){
        dataSource = new LogVideoSource(getContext());
        dataSource.open();
        dataSource.deleteAllVideo();
        dataSource.deleteAllCountry();
        dataSource.deleteAllTopics();
        dataSource.deleteAllLanguage();
        dataSource.close();
    }

    private void getVideoFromDB(){
//        dataSource = new LogVideoSource(getContext());
//        dataSource.open();
//        List<all> videos = dataSource.getAllVideos();
//        videoModel = new Video();
//        if (videos.size()!=0){
//            videoModel.setCountry(dataSource.getAllCountry());
//            videoModel.setLanguage(dataSource.getAllLanguage());
//            videoModel.setTopic(dataSource.getAllTopics());
//            videoModel.setAll(videos);
//            dataSource.close();
//        }else{
//            LogFileDBHelperAssets dbAssets = new LogFileDBHelperAssets(getContext(),getActivity().getFilesDir().getAbsolutePath());
//            try {
//                dbAssets.prepareDatabase();
//            } catch (IOException e) {
//                Log.e("tag", e.getMessage());
//            }
//
//            videoModel.setAll(dbAssets.getAllVideos());
//            videoModel.setCountry(dbAssets.getCountries());
//            videoModel.setLanguage(dbAssets.getLanguages());
//            videoModel.setTopic(dbAssets.getTopics());
//        }
//
//        adapter = new VideoListAdapter(videoModel,getContext(),fragmentManager,VideoLibraryFragment.this);
    }

}

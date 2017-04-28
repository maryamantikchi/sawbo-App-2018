package sawbodeployer.entm.illinois.edu.sawbo.VideoLibrary;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import sawbodeployer.entm.illinois.edu.sawbo.R;
import sawbodeployer.entm.illinois.edu.sawbo.VideoDetail.VideoDetailFragment;

/**
 * Created by Mahsa on 4/1/2017.
 */
public class VideoLibraryFragment extends android.support.v4.app.Fragment {
    public VideoLibraryFragment(){
    }

    Video videoModel;
    ProgressBar progressBar;
    AsyncTask<Void, Void, Void> getData;
    RecyclerView videoList;
    VideoListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_library, container, false);

        videoModel = new Video();

        Typeface title_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BebasNeue.otf");
        Typeface title_list_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Medium.otf");
        Typeface btn_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Regular.otf");

        com.rey.material.widget.TextView title = (com.rey.material.widget.TextView)view.findViewById(R.id.title_video_library);
        title.setTypeface(title_font);
        com.rey.material.widget.TextView title_list = (com.rey.material.widget.TextView)view.findViewById(R.id.title_agriculture);
        title_list.setTypeface(title_list_font);

        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar_video_library);


        videoList = (RecyclerView)view.findViewById(R.id.recycler_view_video_library);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        videoList.setLayoutManager(mLayoutManager);
        videoList.setItemAnimator(new DefaultItemAnimator());
        videoList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        videoList.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new   RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                       //Toast.makeText(getContext(),position+"+++",Toast.LENGTH_SHORT).show();
                       /* VideoDetailFragment fragment = new VideoDetailFragment();
                        fragment.videoDetail = videoModel.getAll().get(position);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_container, fragment).addToBackStack(null).commit();*/

                        VideoDetailFragment fragment = new VideoDetailFragment();
                        fragment.videoDetail = videoModel.getAll().get(position);

                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().hide(VideoLibraryFragment.this)
                                .add(R.id.main_container, fragment)
                                .addToBackStack(null)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();

                    }
                })
        );

        getRetrofitObject();
        getData = new getData().execute();

        return view;
    }

   public void getRetrofitObject() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.sawbo-illinois.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        ServiceAPI service = retrofit.create(ServiceAPI.class);
        final Call<Video> call = service.getVideoDetails();
        try {
           videoModel = call.execute().body();
           adapter = new VideoListAdapter(videoModel,getContext());
        } catch (Exception e) {
           e.printStackTrace();
        }

   }

    private class getData extends AsyncTask<Void, Void, Void> {

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
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("OCRTask","extracting..");
            getRetrofitObject();
            return null;
        }
    }


}

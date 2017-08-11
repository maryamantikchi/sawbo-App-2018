package edu.illinois.entm.sawbodeployer.DirectWifi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pixplicity.sharp.Sharp;
import com.stetcho.rxwifip2pmanager.data.wifi.RxWifiP2pManager;
import com.stetcho.rxwifip2pmanager.data.wifi.broadcast.factory.WifiP2pBroadcastObservableManagerFactory;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import edu.illinois.entm.sawbodeployer.R;
import edu.illinois.entm.sawbodeployer.RxWifi.adapter.mapper.DeviceModelMapper;
import edu.illinois.entm.sawbodeployer.RxWifi.adapter.mapper.WifiP2pSingleDeviceMapper;
import edu.illinois.entm.sawbodeployer.RxWifi.domain.discovery.model.DeviceModel;
import edu.illinois.entm.sawbodeployer.RxWifi.framework.discovery.data.DeviceListAdapter;
import edu.illinois.entm.sawbodeployer.RxWifi.framework.discovery.view.DiscoveryActivity;
import edu.illinois.entm.sawbodeployer.VideoLibrary.all;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WiFiDirectActivity extends Activity implements ChannelListener, DeviceListFragment.DeviceActionListener{

    //-------------------------------------------------------------------------------------------------------------------------
    ProgressDialog progress,connect_progress;


    private class DefaultSubscriber<T> extends Subscriber<T> {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(final Throwable e) {
            // Oops, something went wrong here..
            e.printStackTrace();
        }

        @Override
        public void onNext(final T t) {
        }
    }

    /**
     * Subscriber for when we are initiating a new peer discovery
     */
    private class DiscoverPeersSubscription extends DefaultSubscriber<List<DeviceModel>> {

        @Override
        public void onNext(final List<DeviceModel> deviceList) {
            // We got a list with nearby p2p devices
            progress.dismiss();
            // Stop refreshing
            stopDiscoveringUi();

            // Change the screen state
            setFoundNewDevicesScreen(deviceList);
        }

        @Override
        public void onError(final Throwable e) {
            if (e instanceof TimeoutException) {
                progress.dismiss();

                // No devices were found, or the discovery took too long time

                // Stop refreshing
                stopDiscoveringUi();

                setNoDevicesFoundScreen();
            } else {
                // Ooops, something went wrong here
                e.printStackTrace();
            }
        }
    }


    private class ConnectToDeviceSubscriber extends DefaultSubscriber<DeviceModel> {
        private final DeviceModel mDeviceModel;

        ConnectToDeviceSubscriber(DeviceModel deviceModel) {
            mDeviceModel = deviceModel;
        }

        @Override
        public void onCompleted() {
            // Change screen state
            connect_progress.dismiss();
            setConnectedToDeviceScreen(mDeviceModel);
        }
    }

    /**
     * Subscriber for when we are disconnecting from an existing p2p group
     */
    private class DisconnectFromDeviceSubscriber extends DefaultSubscriber<DeviceModel> {
        @Override
        public void onCompleted() {
            // Change screen state. Restore to the default - welcome screen.
            setWelcomeScreen();
           // Snackbar.make(mSwipeRefreshLayout, R.string.disconnected, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Subscriber for when we check if we are already connected to a device
     */
    private class ConnectedToDeviceSubscriber extends DefaultSubscriber<DeviceModel> {
        @Override
        public void onNext(final DeviceModel device) {
            if (device != null) {
                // We got the information for the device we are connected to
                setConnectedToDeviceScreen(device);
            } else {
//                Snackbar.make(mSwipeRefreshLayout, R.string.unknown_error, Snackbar.LENGTH_SHORT)
//                        .show();
            }
        }
    }

    /*
     * Views
     */
    @BindView(R.id.lv_devices)
    protected ListView mLvDevices;
//
//    @BindView(R.id.swipeRefreshLayout)
//    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.iv_discovery_state_not_found)
    protected ImageView mIvDiscoveryStateNotFound;

    @BindView(R.id.vg_discovery_state_not_found)
    protected ViewGroup mVgDiscoveryStateNotFound;

//    @BindView(R.id.vg_discovery_state_initial)
//    protected ViewGroup mVgDiscoveryStateInitial;

//    @BindView(R.id.vg_device_connected)
//    protected ViewGroup mVgDeviceConnected;

//    @BindView(R.id.tv_text)
//    protected TextView mTvText;

//    @BindView(R.id.tv_title)
//    protected TextView mTvTitle;

    @BindView(R.id.tv_device_found_count)
    protected TextView mTvDeviceFoundCount;

    /*
     * Adapters
     */
    protected DeviceListAdapter mDeviceListAdapter;

    /*
     * Subscriptions
     */
    private Subscription mRequestConnectionInfoSubscription;
    private Subscription mDiscoverPeersSubscription;

    /*
     * Other instance variables
     */
    private RxWifiP2pManager mRxWifiP2pManager;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.discovery_activity);
//        ButterKnife.bind(this);
//
//        mRxWifiP2pManager =
//                new RxWifiP2pManager(
//                        getApplicationContext(),
//                        (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE),
//                        new WifiP2pBroadcastObservableManagerFactory(getApplicationContext()));
//
//        mDeviceListAdapter = new DeviceListAdapter(getApplicationContext(), new ArrayList<>());
//        mLvDevices.setAdapter(mDeviceListAdapter);
//
//        // Check if we are connected to another device
//        getConnectedToDeviceObservable()
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DiscoveryActivity.ConnectedToDeviceSubscriber());
//
//        //mSwipeRefreshLayout.setOnRefreshListener(this);
//
//        // Configure the refreshing colors
//       // mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
//    }

    private void setWelcomeScreen() {
        mDeviceListAdapter.clearData();
        //mTvTitle.setText(R.string.welcome);
        //mTvText.setText("");
        mVgDiscoveryStateNotFound.setVisibility(View.GONE);
        //mVgDeviceConnected.setVisibility(View.GONE);
        mLvDevices.setVisibility(View.VISIBLE);
        //mVgDiscoveryStateInitial.setVisibility(View.VISIBLE);
        mTvDeviceFoundCount.setVisibility(View.GONE);
    }

    private void setNoDevicesFoundScreen() {
        mLvDevices.setVisibility(View.GONE);
        mTvDeviceFoundCount.setVisibility(View.GONE);
        //mVgDiscoveryStateInitial.setVisibility(View.GONE);
        //mVgDeviceConnected.setVisibility(View.GONE);
        Sharp.loadResource(getResources(), R.raw.ic_error_outline_black_24px)
                .into(mIvDiscoveryStateNotFound);
        mVgDiscoveryStateNotFound.setVisibility(View.VISIBLE);
    }

    private void setFoundNewDevicesScreen(final List<DeviceModel> deviceList) {
        mTvDeviceFoundCount.setText(String.format(getString(R.string.device_found_1d),
                deviceList.size()));
        //mVgDiscoveryStateInitial.setVisibility(View.GONE);
        mVgDiscoveryStateNotFound.setVisibility(View.GONE);
        //mVgDeviceConnected.setVisibility(View.GONE);
        mLvDevices.setVisibility(View.VISIBLE);
        mTvDeviceFoundCount.setVisibility(View.VISIBLE);

        // Reset the list with previously found devices (if there were any)
        mDeviceListAdapter.clearData();

        ((DeviceListAdapter) mLvDevices.getAdapter()).setData(deviceList);
    }

    private void setConnectedToDeviceScreen(final DeviceModel deviceModel) {
        //mTvTitle.setText(deviceModel.getName());
        mLvDevices.setVisibility(View.GONE);
        //mVgDiscoveryStateInitial.setVisibility(View.GONE);
        mVgDiscoveryStateNotFound.setVisibility(View.GONE);
        //mVgDeviceConnected.setVisibility(View.VISIBLE);
        mTvDeviceFoundCount.setVisibility(View.GONE);
    }


    private Single<DeviceModel> getConnectedToDeviceObservable() {
        return mRxWifiP2pManager.requestConnectionInfo()
                .flatMap(wifiP2pInfo -> {
                    if (wifiP2pInfo == null || wifiP2pInfo.groupOwnerAddress == null) {
                        return Single.error(
                                new RuntimeException(getString(R.string.not_connected)));
                    }

                    // Seems like we are connected, let's request the list with peers
                    return mRxWifiP2pManager.requestPeersList()
                            // Now map the output to produce Single<WifiP2pDevice>
                            // FIXME: Support for only one device.
                            // Its for simplicity, but kinda ugly.
                            .flatMap(new WifiP2pSingleDeviceMapper());
                })
                // Map the output to produce a DeviceModel
                .map(new DeviceModelMapper.Single());
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
     //   safeUnsubscribe();
    }

    private void safeUnsubscribe() {
        safeUnsubscribe(mDiscoverPeersSubscription);
        safeUnsubscribe(mRequestConnectionInfoSubscription);
    }

    private void safeUnsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    private void stopDiscoveringUi() {
        //mSwipeRefreshLayout.setRefreshing(false);
    }

    @OnItemClick(R.id.lv_devices)
    protected void onDeviceClick(int position) {
        final DeviceModel deviceModel = mDeviceListAdapter.getItem(position);
        connect_progress = ProgressDialog.show(this, "connecting",
                "Please wait...", true);
        mRxWifiP2pManager
                .connect(mRxWifiP2pManager.createConfig(deviceModel.getAddress(), WpsInfo.PBC))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ConnectToDeviceSubscriber(deviceModel));
    }

    //-------------------------------------------------------------------------------------------------------------------------



    public static final String TAG = "wifidirectdemo";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;

    Button scan,toggle_wifi;

    String video_url;
    public all videoFile = new all();

    public void setVideoFile(all videoFile) {
        this.videoFile = videoFile;
    }

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        video_url= extras.getString("url");
        videoFile = (all) extras.getSerializable("video");


        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        scan = (Button)findViewById(R.id.scan_wifi_devices);
        toggle_wifi = (Button)findViewById(R.id.turn_wifi_on);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWifiP2pEnabled) {
                    Toast.makeText(WiFiDirectActivity.this, R.string.p2p_off_warning,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                progress = ProgressDialog.show(WiFiDirectActivity.this, "Scaning",
                        "Searching for devices", true);


                mDiscoverPeersSubscription = mRxWifiP2pManager.discoverAndRequestPeersList()
                        .timeout(5, TimeUnit.SECONDS)
                        .map(new DeviceModelMapper())
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DiscoverPeersSubscription());


//                final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
//                        .findFragmentById(R.id.frag_list);
//                fragment.onInitiateDiscovery();
//                manager.discoverPeers(channel, new ActionListener() {
//
//                    @Override
//                    public void onSuccess() {
//                        Toast.makeText(WiFiDirectActivity.this, "Discovery Initiated",
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(int reasonCode) {
//                        Toast.makeText(WiFiDirectActivity.this, "Discovery Failed : " + reasonCode,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });




        mRxWifiP2pManager =
                new RxWifiP2pManager(
                        getApplicationContext(),
                        (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE),
                        new WifiP2pBroadcastObservableManagerFactory(getApplicationContext()));

        mDeviceListAdapter = new DeviceListAdapter(getApplicationContext(), new ArrayList<>());
        mLvDevices.setAdapter(mDeviceListAdapter);

        // Check if we are connected to another device
        getConnectedToDeviceObservable()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ConnectedToDeviceSubscriber());

        toggle_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manager != null && channel != null) {

                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                } else {
                    Log.e(TAG, "channel or manager is null");
                }
            }
        });


    }



    public String getVideoPath() {
        return video_url;
    }


    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        mRxWifiP2pManager.disconnect()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisconnectFromDeviceSubscriber());

    }






    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {

//        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
//                .findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);


//        if (fragmentList != null) {
//            fragmentList.clearPeers();
//        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }


    @Override
    public void showDetails(WifiP2pDevice device) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);

        fragment.showDetails(device);

    }

    @Override
    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WiFiDirectActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);


        fragment.resetViews();
        manager.removeGroup(channel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

            }

            @Override
            public void onSuccess() {
                fragment.getView().setVisibility(View.GONE);
            }

        });
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void cancelDisconnect() {

        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
//        if (manager != null) {
//            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
//                    .findFragmentById(R.id.frag_list);
//            if (fragment.getDevice() == null
//                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
//                disconnect();
//            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
//                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {
//
//                manager.cancelConnect(channel, new ActionListener() {
//
//                    @Override
//                    public void onSuccess() {
//                        Toast.makeText(WiFiDirectActivity.this, "Aborting connection",
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(int reasonCode) {
//                        Toast.makeText(WiFiDirectActivity.this,
//                                "Connect abort request failed. Reason Code: " + reasonCode,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }

    }






}

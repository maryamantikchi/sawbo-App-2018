package edu.illinois.entm.sawbodeployer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Arrays;

import edu.illinois.cs.bluetoothobexopp.BluetoothOppFileSender;
import edu.illinois.entm.sawbodeployer.DirectWifi.WiFiDirectActivity;
import edu.illinois.entm.sawbodeployer.btxfr.ClientThread;
import edu.illinois.entm.sawbodeployer.btxfr.MessageType;
import edu.illinois.entm.sawbodeployer.btxfr.ProgressData;

import static android.content.ContentValues.TAG;

/**
 * Created by Mahsa on 5/16/2017.
 */

public class ShareVideoFragment extends android.support.v4.app.Fragment/* implements View.OnClickListener */{
    View view;
    RelativeLayout sawbo_share,mail_share,facebook_share,twitter_share,
            general_share, bluetooth_share,phone_share;
    public String videoPath;
    public String dialogVideo, videoFilename, url;

    File OBEXfile;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private boolean viaOBEX = false;

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int REQUEST_DISCOVERABLE_BT = 2;
    private final static int REQUEST_CONNECT_DEVICE_SECURE = 3;
    boolean firstTime=true;


    ClientThread ct;

    public ShareVideoFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_share_video, container, false);
        initialize();
        return view;
    }

    ProgressDialog progressDialog;
    ProgressData progressData = new ProgressData();

    Handler clientHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MessageType.READY_FOR_DATA: {
                    firstTime = true;
                    Toast.makeText(getActivity(), "Ready to send", Toast.LENGTH_SHORT).show();
                    try {
                        byte[] dataToSend = org.apache.commons.io.FileUtils.readFileToByteArray(new File(url));
                        byte[] header = new byte[(videoFilename.getBytes().length + 4)];
                        byte[] lheader = ByteBuffer.allocate(4).putInt(videoFilename.getBytes().length).array();
                        byte[] fheader = videoFilename.getBytes();
                        System.arraycopy(lheader, 0, header, 0, 4);
                        System.arraycopy(fheader, 0, header, 4, fheader.length);

                        byte[] lengthfilename = Arrays.copyOfRange(header, 0, 4);
                        int l = ByteBuffer.wrap(lengthfilename).getInt();
                        byte[] filename = Arrays.copyOfRange(header, 4, l+4);


                        Log.v("header", "Length: " + l + ", Filename: " + new String(filename, "UTF-8"));

                        byte[] datawheader = new byte[dataToSend.length + header.length];
                        System.arraycopy(header, 0, datawheader, 0, header.length);
                        System.arraycopy(dataToSend, 0, datawheader, header.length, dataToSend.length);
                        Message msgForSendingData = new Message();
                        msgForSendingData.obj = datawheader;
                        ct.incomingHandler.sendMessage(msgForSendingData);
                    } catch (IOException e) {
                       // Log.d("SENDER", "Failed to open file " + url);
                        Toast.makeText(getActivity(), "Failed to open file " + url, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

                case MessageType.COULD_NOT_CONNECT: {
                    progressDialog.dismiss();
                    firstTime = true;
                    Toast.makeText(getActivity(), "Could not connect to the paired device", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getActivity().getResources().getString(R.string.cannotcon_str))
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    break;
                }

                case MessageType.SENDING_DATA: {
                    if (progressDialog != null && firstTime) {
                        progressDialog.dismiss();
                        progressDialog = null;
                        firstTime = false;
                    }
                    /*progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage(getResources().getString(R.string.sending_str));
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(getActivity().getResources().getString(R.string.stopsending_str))
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            ct.cancel();
                                            if(progressDialog!=null) {
                                                progressDialog.dismiss();
                                            }
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if(progressDialog!=null) {
                                                progressDialog.show();
                                            }
                                            return;
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                    progressDialog.setCanceledOnTouchOutside(true);
                    if(progressDialog!=null) {
                        progressDialog.show();
                    }*/
                    try {
                        progressData = (ProgressData) message.obj;
                        double pctRemaining = 100 - (((double) progressData.remainingSize / progressData.totalSize) * 100);
                        if (progressDialog == null) {
                            progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setMessage(getResources().getString(R.string.sending_str));
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.setProgress(0);
                            progressDialog.setMax(100);
                            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage(getActivity().getResources().getString(R.string.stopsending_str))
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    ct.cancel();
                                                    if (progressDialog != null) {
                                                        progressDialog.dismiss();
                                                        progressDialog = null;
                                                    }
                                                    firstTime = true;
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    if(progressDialog!=null) {
                                                        progressDialog.show();
                                                    }
                                                    return;
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            });
                            progressDialog.setCanceledOnTouchOutside(true);
                            if(progressDialog!=null) {
                                progressDialog.show();
                            }

                        }
                        if(progressDialog!=null) {
                            progressDialog.setProgress((int) Math.floor(pctRemaining));
                        }

                    }catch(Exception e){
                        Log.v("Receiving exception: ", e.getMessage());
                    }
                    break;
                }

                case MessageType.DATA_SENT_OK: {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    firstTime = true;
                    ct.cancel();
                    Toast.makeText(getActivity(), getResources().getString(R.string.sent_str), Toast.LENGTH_SHORT).show();
                    break;
                }

                case MessageType.EXCEPTION: {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    firstTime = true;
                    ct.cancel();
                    Toast.makeText(getActivity(), "Sending Failed. Sharing was most likely canceled.", Toast.LENGTH_LONG).show();
                    break;
                }

                case MessageType.DIGEST_DID_NOT_MATCH: {

                    if(progressDialog!=null){
                        progressDialog.dismiss();
                        progressDialog=null;
                    }
                    firstTime = true;
                    ct.cancel();
                    Toast.makeText(getActivity(), getResources().getString(R.string.sendfail_str), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };

    private void initialize(){

        sawbo_share = (RelativeLayout)view.findViewById(R.id.share_sawbo);
        sawbo_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent intent = new Intent(getActivity(), WiFiDirectActivity.class);
                intent.putExtra("url",getActivity().getFilesDir() + "/" + videoPath);
                startActivity(intent
                );
            }
        });
        mail_share = (RelativeLayout)view.findViewById(R.id.share_email);
        //mail_share.setOnClickListener(this);
        facebook_share = (RelativeLayout)view.findViewById(R.id.share_facebook);
        facebook_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "http://www.google.com");
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                //shareMedia("com.facebook.katana","com.facebook.katana"+urlEncode());
            }
        });
        twitter_share = (RelativeLayout)view.findViewById(R.id.share_twitter);
        twitter_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                File file = new File(getActivity().getFilesDir() , videoPath);
                Uri uri = FileProvider.getUriForFile(getContext(), "edu.illinois.entm.sawbodeployer", file);

                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("video/3gp")
                        .setStream(uri)
                        .setChooserTitle("Choose bar")
                        .createChooserIntent()
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(intent);
                /*Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("video/3gp");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Video");
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+videoPath+"/" + videoPath));
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Enjoy the Video");
                startActivity(Intent.createChooser(sendIntent, "Email:"));*/
             /*   MediaScannerConnection.scanFile(getActivity(), new String[] { path },

                        null, new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Intent shareIntent = new Intent(
                                        android.content.Intent.ACTION_SEND);
                                shareIntent.setType("video*//*");
                                shareIntent.putExtra(
                                        android.content.Intent.EXTRA_SUBJECT, title);
                                shareIntent.putExtra(
                                        android.content.Intent.EXTRA_TITLE, title);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                shareIntent
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                context.startActivity(Intent.createChooser(shareIntent,
                                        getString(R.string.str_share_this_video)));

                            }
                        });*/
            }
        });
        //twitter_share.setOnClickListener(this);
        general_share = (RelativeLayout)view.findViewById(R.id.share_general);
        //general_share.setOnClickListener(this);
        phone_share = (RelativeLayout)view.findViewById(R.id.share_phone);
        //phone_share.setOnClickListener(this);
        bluetooth_share = (RelativeLayout)view.findViewById(R.id.share_bluetooth);
        bluetooth_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.err.println(videoPath+" bbbbb");
                shareNormalAtPos(videoPath);

            }
        });
    }


  /*  @Override
    public void onClick(View v) {

        switch (view.getId())
        {
            case R.id.share_sawbo:

                break;
            case R.id.share_email:
                shareNormalAtPos(videoPath);

                break;
            case R.id.share_facebook:
                shareMedia("com.facebook.katana","https://twitter.com/intent/tweet?text="+urlEncode());
                break;
            case R.id.share_twitter:

                shareMedia("com.twitter.android","https://twitter.com/intent/tweet?text="+urlEncode());


                break;
            case R.id.share_general:


                break;
            case R.id.share_phone:
                shareNormalAtPos(videoPath);

                break;
            case R.id.share_bluetooth:
                shareNormalAtPos(videoPath);

                break;
        }

    }*/

   private void shareMedia(String pacName, String IntentUrl){
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(Intent.EXTRA_TEXT, videoPath);
        tweetIntent.setType("text/plain");

/*        PackageManager packManager = getActivity().getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for(ResolveInfo resolveInfo: resolvedInfoList){
            if(resolveInfo.activityInfo.packageName.startsWith(pacName)){
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name );
                resolved = true;
                break;
            }
        }
        if(resolved){
            startActivity(tweetIntent);
        }else*/{
            Intent i = new Intent();
            i.putExtra(Intent.EXTRA_TEXT, videoPath);
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse(IntentUrl));
            startActivity(i);
        }
    }

 private String urlEncode() {
        try {
            return URLEncoder.encode(videoPath, "UTF-8");
        }catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, "UTF-8 should always be supported", e);
            return "";
        }
    }



    private void shareNormalAtPos( String filename) {
        System.err.println("click 1");
        // OBEX
        OBEXfile = new File(getActivity().getFilesDir() + "/" + filename);
        System.err.println(filename+"@@@@");
        System.err.println("click 2"+OBEXfile.getAbsolutePath());

        Log.d("filepath sharing", getActivity().getFilesDir() + "/" + filename);
        //videoFilename = filename;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.v("Bluetooth", "not available");
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
            } else {
                viaOBEX = true;
                senderBTReady();
            }
        }
        //senderBTReady();
    }

    public void senderBTReady() {
        Intent senderIntent = new Intent(getActivity(), DeviceListActivity.class);
        if(!viaOBEX) {
            senderIntent.putExtra("enableSearch", false);
        }
        startActivityForResult(senderIntent, REQUEST_CONNECT_DEVICE_SECURE);
    }


    private void connectDevice(Intent data) {
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        if(viaOBEX){
            //wl.writeNow(getActivity(), "sharecontentviabluetooth", videoFilename, "", address);
            BluetoothOppFileSender sender = new BluetoothOppFileSender(getActivity(), OBEXfile, address);
            sender.send();
        }else {
            Log.v("remote", address.toString());
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            if (ct != null) {
                ct.cancel();
            }
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getResources().getString(R.string.connecting_str));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getActivity().getResources().getString(R.string.stopsending_str))
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ct.cancel();
                                    if(progressDialog!=null) {
                                        progressDialog.dismiss();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if(progressDialog!=null) {
                                        progressDialog.show();
                                    }
                                    return;
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
            progressDialog.setCanceledOnTouchOutside(true);
            if(progressDialog!=null) {
                progressDialog.show();
            }
            ct = new ClientThread(device, clientHandler);
            ct.start();
            /*wl.writeNow(getActivity(), "sharecontentviaapp", videoFilename, "", address);
            //(new SenderThread(device)).start();
            if (ct != null) {
                ct.cancel();
            }
            ct = new ClientThread(device, clientHandler);
            ct.start();*/
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    senderBTReady();
                }
                break;
            case REQUEST_DISCOVERABLE_BT:
                if (resultCode != Activity.RESULT_CANCELED) {
                    //receiverBTReader();
                }
                break;
            case REQUEST_CONNECT_DEVICE_SECURE:
                Log.v("Bluetooth", "request_connect_secure");
                if (resultCode == Activity.RESULT_OK) {
                    Log.v("Bluetooth", "ready for connection");
                    connectDevice(data);
                }
                break;

        }
    }


}

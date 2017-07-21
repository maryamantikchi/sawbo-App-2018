package edu.illinois.entm.sawbodeployer.DirectWifi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import edu.illinois.entm.sawbodeployer.R;
import edu.illinois.entm.sawbodeployer.VideoDB.MyVideoDataSource;
import edu.illinois.entm.sawbodeployer.VideoLibrary.all;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener  {


	private static View mContentView = null;
	private WifiP2pDevice device;
	private WifiP2pInfo info;
	ProgressDialog progressDialog = null;

	private static ProgressDialog mProgressDialog;

	public static String WiFiClientIp = "";
	static Boolean ClientCheck = false;
	public static String GroupOwnerAddress = "";
	static long ActualFilelength = 0;
	static int Percentage = 0;
	//public static String FolderName = "WiFiDirectDemo";
	public static all videoInfo;


	String fullPath;
	File Selected_file;

	String url;

//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mContentView = inflater.inflate(R.layout.device_detail, null);
		Bundle extras = getActivity().getIntent().getExtras();
		url = extras.getString("url");
		videoInfo = (all) extras.getSerializable("video");

//		mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				WifiP2pConfig config = new WifiP2pConfig();
//				config.deviceAddress = device.deviceAddress;
//				config.wps.setup = WpsInfo.PBC;
//				if (progressDialog != null && progressDialog.isShowing()) {
//					progressDialog.dismiss();
//				}
//				progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
//						"Connecting to :" + device.deviceAddress, true, true);
//				((DeviceListFragment.DeviceActionListener) getActivity()).connect(config);
//
//			}
//		});

		mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();
					}
				});

		mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						File root = new File(Environment.getExternalStorageDirectory(), ".Sawbo");
						if (!root.exists()) {
							root.mkdirs();
						}

						generateNoteOnSD(root,"VideoInfo.txt");


//						File Imgroot = Environment.getExternalStorageDirectory();
//						File ImgDir = new File(Imgroot.getAbsolutePath() +"/.Sawbo/Images");
//						File Imgfile = new File(ImgDir, videoInfo.getImage());


						ArrayList<String> filePath=new ArrayList<>();
						filePath.add(url);
						//filePath.add(Imgfile.getPath());
						filePath.add(root.getPath()+"/VideoInfo.txt");
						zipFolder(filePath,root.toString(),"video_pack.zip");
						sendingFile(root.getPath()+"/video_pack.zip");

					}


				});

		return mContentView;
	}

	public  void sendingFile(String path) {
		Uri uri;
		String Extension = "";
		File f;
		if (path != null) {
			f = new File(path);

			uri = Uri.fromFile(f);
			Long FileLength = f.length();
			ActualFilelength = FileLength;
			try {
				Extension = f.getName();
				Log.e("Name of File-> ", "" + Extension);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			CommonMethods.e("", "path is null");
			return;
		}
		Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
		Intent serviceIntent = new Intent(mContentView.getContext(), FileTransferService.class);
		serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
		serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());

    	        /*
    	         * Choose on which device file has to send weather its server or client
    	         */
		String Ip = SharedPreferencesHandler.getStringValues(
				mContentView.getContext(), "WiFiClientIp");
		String OwnerIp = SharedPreferencesHandler.getStringValues(
				mContentView.getContext(), "GroupOwnerAddress");
		if (OwnerIp != null && OwnerIp.length() > 0) {
			CommonMethods.e("", "inside the check -- >");
			String host = null;
			int sub_port = -1;

			String ServerBool = SharedPreferencesHandler.getStringValues(mContentView.getContext(), "ServerBoolean");
			if (ServerBool != null && !ServerBool.equals("") && ServerBool.equalsIgnoreCase("true")) {

				//-----------------------------
				if (Ip != null && !Ip.equals("")) {
					CommonMethods.e(
							"in if condition",
							"Sending data to " + Ip);
					host = Ip;
					sub_port = FileTransferService.PORT;
					serviceIntent
							.putExtra(
									FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
									Ip);

				}


			} else {
				CommonMethods.e(
						"in else condition",
						"Sending data to " + OwnerIp);

				FileTransferService.PORT = 8888;

				host = OwnerIp;
				sub_port = FileTransferService.PORT;
				serviceIntent
						.putExtra(
								FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
								OwnerIp);


			}


			serviceIntent.putExtra(FileTransferService.Extension, Extension);

			serviceIntent.putExtra(FileTransferService.Filelength,
					ActualFilelength + "");
			serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, FileTransferService.PORT);
			if (host != null && sub_port != -1) {
				CommonMethods.e("Going to intiate service", "service intent for initiating transfer");
				showprogress("Sending...");
				mContentView.getContext().startService(serviceIntent);
			} else {
				CommonMethods.DisplayToast(mContentView.getContext(),
						"Host Address not found, Please Re-Connect");
				DismissProgressDialog();
			}

		} else {
			DismissProgressDialog();
			CommonMethods.DisplayToast(mContentView.getContext(),
					"Host Address not found, Please Re-Connect");
		}
	}


	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {

		//System.err.println(info+"    infooooo");
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		Log.v("onConnectionInfoAv","onConnectionInfoAvailable");
		this.info = info;
		this.getView().setVisibility(View.VISIBLE);

		// The owner IP is now known.
		TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(getResources().getString(R.string.group_owner_text)
				+ ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
				: getResources().getString(R.string.no)));

		// InetAddress from WifiP2pInfo struct.
		view = (TextView) mContentView.findViewById(R.id.device_info);
		if (info.groupOwnerAddress.getHostAddress() != null)
			view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());
		else {
			CommonMethods.DisplayToast(getActivity(), "Host Address not found");
		}
		// After the group negotiation, we assign the group owner as the file
		// server. The file server is single threaded, single connection server
		// socket.
		try {
			String GroupOwner = info.groupOwnerAddress.getHostAddress();
			if (GroupOwner != null && !GroupOwner.equals(""))
				SharedPreferencesHandler.setStringValues(getActivity(),
						"GroupOwnerAddress", GroupOwner);
			mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
			if (info.groupFormed && info.isGroupOwner) {
        	/*
        	 * set shaerdprefrence which remember that device is server.
        	 */
				SharedPreferencesHandler.setStringValues(getActivity(),
						"ServerBoolean", "true");
				FileServerAsyncTask FileServerobj = new FileServerAsyncTask(
						getActivity(), FileTransferService.PORT);
				if (FileServerobj != null) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						FileServerobj.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{null});
					} else
						FileServerobj.execute();
				}
			} else {
				if (!ClientCheck) {
					firstConnectionMessage firstObj = new firstConnectionMessage(
							GroupOwnerAddress);
					if (firstObj != null) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							firstObj.executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR,
									new String[]{null});
						} else
							firstObj.execute();
					}
				}

				FileServerAsyncTask FileServerobj = new FileServerAsyncTask(getActivity(), FileTransferService.PORT);
				if (FileServerobj != null) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						FileServerobj.executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR,
								new String[]{null});
					} else
						FileServerobj.execute();

				}

			}
		} catch (Exception e) {

		}

	}


	/**
	 * Updates the UI with device data
	 *
	 * @param device the device to be displayed
	 */
	public void showDetails(WifiP2pDevice device) {
		this.device = device;
		this.getView().setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(device.deviceAddress);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(device.toString());

	}

	/**
	 * Clears the UI fields after a disconnect or direct mode disable operation.
	 */
	public void resetViews() {
		//mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.status_text);
		view.setText(R.string.empty);
		mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
		this.getView().setVisibility(View.GONE);
        /*
         * Remove All the prefrences here
         */
		SharedPreferencesHandler.setStringValues(getActivity(),
				"GroupOwnerAddress", "");
		SharedPreferencesHandler.setStringValues(getActivity(),
				"ServerBoolean", "");
		SharedPreferencesHandler.setStringValues(getActivity(),
				"WiFiClientIp", "");
	}

	/**
	 * A simple server socket that accepts connection and writes some data on
	 * the stream.
	 */
	static Handler handler;



	public class FileServerAsyncTask extends AsyncTask<String, String, String> {

		private Context mFilecontext;
		private String Extension, Key;
		private File EncryptedFile;
		private long ReceivedFileLength;
		private int PORT;

		/**
		 * @param context
		 */
		public FileServerAsyncTask(Context context, int port) {
			this.mFilecontext = context;
			handler = new Handler();
			this.PORT = port;
			if (mProgressDialog == null)
				mProgressDialog = new ProgressDialog(mFilecontext,
						ProgressDialog.THEME_HOLO_LIGHT);
		}


		@Override
		protected String doInBackground(String... params) {
			try {
				CommonMethods.e("File Async task port", "File Async task port-> " + PORT);
				// init handler for progressdialog
				ServerSocket serverSocket = new ServerSocket(PORT);

				Log.d(CommonMethods.Tag, "Server: Socket opened");
				Socket client = serverSocket.accept();

				WiFiClientIp = client.getInetAddress().getHostAddress();

				ObjectInputStream ois = new ObjectInputStream(
						client.getInputStream());
				WiFiTransferModal obj = null;
				// obj = (WiFiTransferModal) ois.readObject();
				String InetAddress;
				try {
					obj = (WiFiTransferModal) ois.readObject();
					InetAddress = obj.getInetAddress();
					if (InetAddress != null
							&& InetAddress
							.equalsIgnoreCase(FileTransferService.inetaddress)) {
						CommonMethods.e("File Async Group Client Ip", "port-> "
								+ WiFiClientIp);
						SharedPreferencesHandler.setStringValues(mFilecontext,
								"WiFiClientIp", WiFiClientIp);
						CommonMethods
								.e("File Async Group Client Ip from SHAREDPrefrence",
										"port-> "
												+ SharedPreferencesHandler
												.getStringValues(
														mFilecontext,
														"WiFiClientIp"));
						//set boolean true which identifiy that this device will act as server.
						SharedPreferencesHandler.setStringValues(mFilecontext,
								"ServerBoolean", "true");
						ois.close(); // close the ObjectOutputStream object
						// after saving
						serverSocket.close();

						return "Demo";
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				final Runnable r = new Runnable() {

					public void run() {
						// TODO Auto-generated method stub

						if (mProgressDialog == null) {
							mProgressDialog = new ProgressDialog(GlobalApplication.getGlobalAppContext(),
									ProgressDialog.THEME_HOLO_LIGHT);
						}

							mProgressDialog.setMessage("Receiving...");
							mProgressDialog.setIndeterminate(false);
							mProgressDialog.setMax(100);
							mProgressDialog.setProgress(0);
							mProgressDialog.setProgressNumberFormat(null);
							mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
							mProgressDialog.show();

					}
				};

				handler.post(r);

				Log.e("FileNameFromSocket", obj.getFileName());

				fullPath = GlobalApplication.getGlobalAppContext().getFilesDir()+ "/video_pack";
				Selected_file = new File(GlobalApplication.getGlobalAppContext().getFilesDir()+"/"+obj.getFileName());

				File dirs = new File(Selected_file.getParent());
				if (!dirs.exists())
					dirs.mkdirs();
				Selected_file.createNewFile();


				/*
				 * Recieve file length and copy after it
				 */
				this.ReceivedFileLength = obj.getFileLength();

				InputStream inputstream = client.getInputStream();


				copyRecievedFile(inputstream, new FileOutputStream(Selected_file), ReceivedFileLength);
				ois.close(); // close the ObjectOutputStream object after saving
				// file to storage.
				serverSocket.close();

				/*
				 * Set file related data and decrypt file in postExecute.
				 */
				this.Extension = obj.getFileName();
				this.EncryptedFile = Selected_file;

				unZipIt(Selected_file.getPath(),fullPath);

				return Selected_file.getAbsolutePath();
			} catch (IOException e) {
				Log.e(WiFiDirectActivity.TAG, e.getMessage());
				return null;
			}
		}

		/*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				{
            		/*
					 * To initiate socket again we are intiating async task
					 * in this condition.
					 */
					FileServerAsyncTask FileServerobj = new
							FileServerAsyncTask(mFilecontext, FileTransferService.PORT);
					if (FileServerobj != null) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							FileServerobj.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{null});

						} else FileServerobj.execute();




					}
				}

			}

		}

		/*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
		@Override
		protected void onPreExecute() {
			if (mProgressDialog == null) {
				mProgressDialog = new ProgressDialog(mFilecontext);
			}
		}

	}

	public static boolean copyFile(InputStream inputStream, OutputStream out) {
		long total = 0;
		long test = 0;
		byte buf[] = new byte[FileTransferService.ByteSize];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				out.write(buf, 0, len);
				try {
					total += len;
					if (ActualFilelength > 0) {
						Percentage = (int) ((total * 100) / ActualFilelength);
					}
					mProgressDialog.setProgress(Percentage);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					Percentage = 0;
					ActualFilelength = 0;
				}
			}
			if (mProgressDialog != null) {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}

			inputStream.close();
		} catch (IOException e) {
			Log.d(WiFiDirectActivity.TAG, e.toString());
			return false;
		}

		return true;
	}


	public static boolean copyRecievedFile(InputStream inputStream,
										   OutputStream out, Long length) {

		byte buf[] = new byte[FileTransferService.ByteSize];
		int len;
		long total = 0;
		int progresspercentage = 0;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				try {
					out.write(buf, 0, len);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					total += len;
					if (length > 0) {
						progresspercentage = (int) ((total * 100) / length);
					}
					mProgressDialog.setProgress(progresspercentage);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					if (mProgressDialog != null) {
						if (mProgressDialog.isShowing()) {
							mProgressDialog.dismiss();
						}
					}
				}
			}

			if (mProgressDialog != null) {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
			out.close();
			inputStream.close();
		} catch (IOException e) {
			Log.d(WiFiDirectActivity.TAG, e.toString());
			return false;
		}
		return true;
	}

	public static void showprogress(final String task) {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(mContentView.getContext(),
					ProgressDialog.THEME_HOLO_LIGHT);
		}
		Handler handle = new Handler();
		final Runnable send = new Runnable() {

			public void run() {
				// TODO Auto-generated method stub

				if (mProgressDialog == null) {
					mProgressDialog = new ProgressDialog(GlobalApplication.getGlobalAppContext(),
							ProgressDialog.THEME_HOLO_LIGHT);
				}

					mProgressDialog.setMessage(task);
					mProgressDialog.setIndeterminate(false);
					mProgressDialog.setMax(100);
					mProgressDialog.setProgressNumberFormat(null);
					mProgressDialog
							.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					mProgressDialog.show();


			}
		};
		handle.post(send);
	}

	public static void DismissProgressDialog() {
		try {
			if (mProgressDialog != null) {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}


	/*
     * Async class that has to be called when connection establish first time. Its main motive is to send blank message
     * to server so that server knows the IP address of client to send files Bi-Directional.
     */
	class firstConnectionMessage extends AsyncTask<String, Void, String> {

		String GroupOwnerAddress = "";

		public firstConnectionMessage(String owner) {
			// TODO Auto-generated constructor stub
			this.GroupOwnerAddress = owner;

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			CommonMethods.e("On first Connect", "On first Connect");

			Intent serviceIntent = new Intent(getActivity(),
					WiFiClientIPTransferService.class);

			serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);

			if (info.groupOwnerAddress.getHostAddress() != null) {
				serviceIntent.putExtra(
						FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
						info.groupOwnerAddress.getHostAddress());

				serviceIntent.putExtra(
						FileTransferService.EXTRAS_GROUP_OWNER_PORT,
						FileTransferService.PORT);
				serviceIntent.putExtra(FileTransferService.inetaddress,
						FileTransferService.inetaddress);

			}

			getActivity().startService(serviceIntent);

			return "success";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				if (result.equalsIgnoreCase("success")) {
					CommonMethods.e("On first Connect",
							"On first Connect sent to asynctask");
					ClientCheck = true;
				}
			}

		}

	}

	private void addDataBase(all video) {
		MyVideoDataSource dataSource = new MyVideoDataSource(GlobalApplication.getGlobalAppContext());
		dataSource.open();
		all newVideo = new all();
		newVideo = video;
		Boolean isLight = true;
		if (video.getVideolight().isEmpty() || video.getVideolight() == null) {
			isLight = false;
		}
		if (isLight)
			newVideo.setGp_file("");
		else newVideo.setVideolight("");

		dataSource.createVideo(newVideo);
		dataSource.close();
	}


	public void generateNoteOnSD(File dir, String sFileName) {
		ObjectOutput out = null;

		try {
			out = new ObjectOutputStream(new FileOutputStream(new File(dir,"")+File.separator+sFileName));
			out.writeObject(DeviceDetailFragment.videoInfo);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}




	public void unZipIt(String zipFile, String outputFolder){

		byte[] buffer = new byte[1024];

		try{

			//create output directory is not exists
			File folder = new File(fullPath);
			if(!folder.exists()){
				folder.mkdir();
			}

			//get the zip file content
			ZipInputStream zis =
					new ZipInputStream(new FileInputStream(zipFile));
			//get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while(ze!=null){

				String fileName = ze.getName();
				File newFile = new File(outputFolder + File.separator + fileName);

				System.out.println("file unzip : "+ newFile.getAbsoluteFile());

				//create all non exists folders
				//else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();


				//System.err.println(jsonString);

				//all newVideo =  new Gson().fromJson(jsonString, all.class);

				if (newFile.getPath().contains(".txt")){
					//read file


					all newVideo = readFile(newFile);
					//add to db
					addDataBase(newVideo);

				}else if (newFile.getPath().contains(".3gp")){
					//copy to videos
				//	System.err.println(getActivity().getFilesDir() + "/"+newFile.getName());

					File source = new File(GlobalApplication.getGlobalAppContext().getFilesDir() + "/"+newFile.getName());

					FileUtils.copyFile(newFile, source);

				}/*else if(newFile.getPath().contains(".jpg")){
					//copy to images

					File Imgroot = Environment.getExternalStorageDirectory();
					File ImgDir = new File(Imgroot.getAbsolutePath() +"/.Sawbo/Images");
					File Imgfile = new File(ImgDir.getAbsolutePath()+"/"+ newFile.getName());
					FileUtils.copyFile(newFile, Imgfile);

				}*/

				newFile.delete();



				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			System.out.println("Done");

		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	public void zipFolder(ArrayList<String> _files, String destination, String fileName) {
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(destination+"/"+fileName);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			byte data[] = new byte[1024];

			for (int i = 0; i < _files.size(); i++) {
				File f = new File(_files.get(i));
				if (f.exists()) {
					FileInputStream fi = new FileInputStream(_files.get(i));
					origin = new BufferedInputStream(fi, data.length);

					ZipEntry entry = new ZipEntry(_files.get(i).substring(_files.get(i).lastIndexOf("/") + 1));
					out.putNextEntry(entry);
					int count;

					while ((count = origin.read(data, 0, data.length)) != -1) {
						out.write(data, 0, count);
					}
					origin.close();
				}
			}

			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private all readFile(File file){


		ObjectInputStream input;
		//String filename = "testFilemost.srl";

		all myPersonObject = new all();

		try {
			input = new ObjectInputStream(new FileInputStream(file/*new File(new File(getFilesDir(),"")+File.separator+filename)*/));
			myPersonObject = (all) input.readObject();
			input.close();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return myPersonObject;


	}

	@Override
	public void onPause() {
		super.onPause();
		((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();

	}
}
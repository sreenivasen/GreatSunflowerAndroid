package org.greatsunflower.android;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.hardware.Camera;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.provider.ContactsContract.Directory;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class PreviewDemo extends SherlockFragmentActivity implements
		OnClickListener,GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,LocationListener {

	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private Camera camera = null;
	private boolean inPreview = false;
	ImageView image;
	Bitmap bmp, itembmp;
	static Bitmap mutableBitmap;
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	File imageFileName = null;
	File imageFileFolder = null;
	private MediaScannerConnection msConn;
	Display d;
	int screenhgt, screenwdh;
	ProgressDialog dialog;
	int sessionId = -1;
	String extraData;
	final PreviewDemo cameraPreview = this;
	private ObservationsDataSource datasource;
	private SQLiteObservations observation = null;
	private String[] location = new String[2];
	
	private SharedPreferences pref;
	
	LocationClient mLocationClient = null;
	LocationRequest mLocationRequest = null;	
	private static final int MILLISECONDS_PER_SECOND = 1000;

	public static final int UPDATE_INTERVAL_IN_SECONDS = 600;
	private static final long UPDATE_INTERVAL =
	          MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	private static final long FASTEST_INTERVAL =
	          MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

	ImageView closeCamera;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview);

		Intent sender = getIntent();
		//sessionId = sender.getExtras().getString("SESSIONID");
		extraData = sender.getExtras().getString("OBSERVATION_TYPE");

		datasource = new ObservationsDataSource(this);
		datasource.open();
		
		closeCamera = (ImageView) findViewById(R.id.closeCamera);
		
		pref = getApplicationContext().getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE);
		Log.d("SESSION DETAILS", "Session Id: " + pref.getInt("SESSION_ID", -1));
		sessionId = pref.getInt("SESSION_ID", -1);
		if (pref.getString("OBSERVATION_TYPE", null).equals("Stationary")){
			cameraPreview.setTitle("Step 1 of 5");
		}
		else
			cameraPreview.setTitle("Step 1 of 4");
		// image=(ImageView)findViewById(R.id.image);
		preview = (SurfaceView) findViewById(R.id.surface);

		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		previewHolder.setFixedSize(getWindow().getWindowManager()
				.getDefaultDisplay().getWidth(), getWindow().getWindowManager()
				.getDefaultDisplay().getHeight());
		

		// Add a listener to the Capture button
		Button captureButton = (Button) findViewById(R.id.capture);
		
		closeCamera.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buildDialog();
			}
		});
		
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// get an image from the camera
				camera.takePicture(null, null, photoCallback);
			}
		});

		
		mLocationClient = new LocationClient(this, this, this);
		//mLocationClient.connect();
		mLocationClient.registerConnectionCallbacks(this);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = new MenuInflater(getApplicationContext());
		inflater.inflate(R.menu.camera_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
//		case R.id.menu_exit:
//			camera.stopPreview();
//			inPreview = false;
//			//Intent intent = new Intent(this, IdentifyActivity.class);
//			Intent intent = new Intent(this, ActivityConfirmation.class);
//			//Intent intent = new Intent(this, MultiSelectGridview.class);
//			intent.putExtra("SESSIONID", sessionId);
//			intent.putExtra("OBSERVATION_TYPE", extraData);
//			datasource.updateSessionEndDateTime(Integer.valueOf(sessionId), getCurrentDate());
//			startActivity(intent);
//			return true;
		case R.id.menu_info:
			Log.d("CAMERA", "Entered menu info item");
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setTitle("Information")
			.setMessage("Snap a picture each time a pollinator visits.")
			.setCancelable(true);
			alertDialogBuilder.setPositiveButton("Close",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					dialog.cancel();
				}
			});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			return true;
		default:
			Log.d("CAMERA", "On select, nothing is being handled");
			return true;
		}
	}

	@Override
	public void onResume() {
		mLocationClient.connect();
		super.onResume();
		camera = Camera.open();
	}

	@Override
	public void onPause() {
		if (inPreview) {
			camera.stopPreview();
		}

		camera.release();
		camera = null;
		inPreview = false;
		if (mLocationClient != null){
			Log.d("LOCATION", "onPAUSE location client disconnected");
		mLocationClient.unregisterConnectionCallbacks(this);
		mLocationClient.disconnect();
		}
		super.onPause();
	}

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;
					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}
		return (result);
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera.setPreviewDisplay(previewHolder);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
				Toast.makeText(PreviewDemo.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = getBestPreviewSize(width, height, parameters);

			if (size != null) {
				parameters.setPreviewSize(size.width, size.height);
				camera.setParameters(parameters);
				camera.startPreview();
				if (Integer.parseInt(Build.VERSION.SDK) >= 8)
					setDisplayOrientation(camera,90);
			    else
			    {
			        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			        {
			        	parameters.set("orientation", "portrait");
			        	parameters.set("rotation", 90);
			        }
			        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			        {
			        	parameters.set("orientation", "landscape");
			        	parameters.set("rotation", 90);
			        }
			    } 
				//camera.setDisplayOrientation(90);
				inPreview = true;
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// no-op
		}
	};
	
	protected void setDisplayOrientation(Camera camera, int angle){
	    Method downPolymorphic;
	    try
	    {
	        downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[] { int.class });
	        if (downPolymorphic != null)
	            downPolymorphic.invoke(camera, new Object[] { angle });
	    }
	    catch (Exception e1)
	    {
	    }
	}

	Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
		public void onPictureTaken(final byte[] data, final Camera camera) {
			dialog = ProgressDialog.show(PreviewDemo.this, "", "Saving Photo");
			new Thread() {
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (Exception ex) {
					}
					onPictureTake(data, camera);

				}
			}.start();
		}
	};

	public void onPictureTake(byte[] data, Camera camera) {

		bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
		savePhoto(mutableBitmap);
		dialog.dismiss();
		Camera.Parameters parameters = camera.getParameters();
		camera.startPreview();
		//camera.setDisplayOrientation(90);
		if (Integer.parseInt(Build.VERSION.SDK) >= 8)
			setDisplayOrientation(camera,90);
	    else
	    {
	        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
	        {
	        	parameters.set("orientation", "portrait");
	        	parameters.set("rotation", 90);
	        }
	        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
	        {
	        	parameters.set("orientation", "landscape");
	        	parameters.set("rotation", 90);
	        }
	    } 
		inPreview = true;
	}
	
	

	class SavePhotoTask extends AsyncTask<byte[], String, String> {
		@Override
		protected String doInBackground(byte[]... jpeg) {
			File photo = new File(Environment.getExternalStorageDirectory(),
					"photo.jpg");
			if (photo.exists()) {
				photo.delete();
			}
			try {
				FileOutputStream fos = new FileOutputStream(photo.getPath());
				fos.write(jpeg[0]);
				fos.close();
			} catch (java.io.IOException e) {
				Log.e("PictureDemo", "Exception in photoCallback", e);
			}
			return (null);
		}
	}

	public void savePhoto(Bitmap bmp) {
		//imageFileFolder = new File(Environment.getExternalStorageDirectory(),"GreatSunflowerProject");
		imageFileFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"GreatSunflowerProject");
		imageFileFolder.mkdir();
		FileOutputStream out = null;

		String date = getCurrentDateForPhoto();
		imageFileName = new File(imageFileFolder, date.toString() + ".jpg");
		location = getCurrentLocation();
		datasource.createImages(pref.getInt("SESSION_ID", -1), -1, location[0], location[1], imageFileName.getPath(), date);
		//observation = datasource.createObservations("sunflower", "bumblebee",  imageFileName.getPath(), Integer.parseInt(sessionId), date);
		try {
			out = new FileOutputStream(imageFileName);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			scanPhoto(imageFileName.toString());
			out = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getCurrentDate(){
		return java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
	}
	
	public String getCurrentDateForPhoto(){
		Calendar c = Calendar.getInstance();
		String date = fromInt(c.get(Calendar.MONTH))
				+ fromInt(c.get(Calendar.DAY_OF_MONTH))
				+ fromInt(c.get(Calendar.YEAR))
				+ fromInt(c.get(Calendar.HOUR_OF_DAY))
				+ fromInt(c.get(Calendar.MINUTE))
				+ fromInt(c.get(Calendar.SECOND));
		return date;
	}

	public String fromInt(int val) {
		return String.valueOf(val);
	}

	public void scanPhoto(final String imageFileName) {
		msConn = new MediaScannerConnection(PreviewDemo.this,
				new MediaScannerConnectionClient() {
					public void onMediaScannerConnected() {
						msConn.scanFile(imageFileName, null);
						Log.i("msClient obj  in Photo Utility",
								"connection established");
					}

					public void onScanCompleted(String path, Uri uri) {
						msConn.disconnect();
						Log.i("msClient obj in Photo Utility", "scan completed");
					}
				});
		msConn.connect();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
			onBack();
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onBack() {
		Log.e("onBack :", "yes");
		camera.takePicture(null, null, photoCallback);
		inPreview = false;
	}

	@Override
	public void onClick(View v) {

	}
	
	   @Override
	   public void onBackPressed() 
	   {
		   // do nothing

//	       this.finish();
////	       overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
//	       return;
	   }

	   @Override
		public void onConnectionFailed(ConnectionResult arg0) {
			// TODO Auto-generated method stub
			Log.d("LOCATION", "service failed" + arg0.getErrorCode());

		}

		@Override
		public void onConnected(Bundle arg0) {
			// TODO Auto-generated method stub
			Log.d("LOCATION", "service connected");
		    mLocationRequest = LocationRequest.create();
		    mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		    mLocationRequest.setInterval(UPDATE_INTERVAL);
		    mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		    mLocationClient.requestLocationUpdates(mLocationRequest, this);
			// mLocationClient.requestLocationUpdates(mLocationRequest,
			// (com.google.android.gms.location.LocationListener) this);

		}

		@Override
		public void onDisconnected() {
			// TODO Auto-generated method stub
			Log.d("LOCATION", "service disconnected");
		}
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Log.d("LOCATION", "Location has changed");
			
		}
		
		@Override
		protected void onStart() {
			super.onStart();
			mLocationClient.connect();

		}

		@Override
		protected void onStop() {
			if (mLocationClient != null){
			mLocationClient.unregisterConnectionCallbacks(this);
			mLocationClient.disconnect();
			}
			super.onStop();
		}

		public String[] getCurrentLocation() {
			// Get the current location's latitude & longitude
			Log.d("LOCATION", "trying to get current location");
			String[] coordinates = new String[2];
			if (mLocationClient.getLastLocation() == null) {
				coordinates[0] = "";
				coordinates[1] = "";
			} else {
				Location currentLocation = mLocationClient.getLastLocation();
				coordinates[0] = currentLocation.getLatitude() + "";
				coordinates[1] = currentLocation.getLongitude() + "";
			}
			Log.d("LOCATION", "lat: " + coordinates[0] + " long: " + coordinates[1] );
			return coordinates;

		}
		
		public void buildDialog(){
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					cameraPreview);
			alertDialogBuilder.setTitle("Exit Camera");
			alertDialogBuilder
					.setMessage("This will end your observation session and take you to Step 2.")
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									camera.stopPreview();
									inPreview = false;
									Intent intent = new Intent(cameraPreview, ActivityConfirmation.class);
									//Intent intent = new Intent(this, MultiSelectGridview.class);
									intent.putExtra("SESSIONID", sessionId);
									intent.putExtra("OBSERVATION_TYPE", extraData);
									datasource.updateSessionEndDateTime(Integer.valueOf(sessionId), getCurrentDate());
									startActivity(intent);
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}

}

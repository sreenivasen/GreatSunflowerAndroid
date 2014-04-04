package org.greatsunflower.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.greatsunflower.android.StartActivity.DrawerItemClickListener;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class ObservationNotification extends SherlockFragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private View v;
	private Button startButton;
	private ObservationsDataSource datasource;
	private SQLiteObservations observation = null;

	private SharedPreferences pref;

	private CSVtoSQLiteDataSource csvsource;
	private CSVtoSQLiteTaxas taxa = null;

	private Uri uriSavedImage;
	private int session;

	private LocationDemand locDemand;

	private LocationClient customClient;
	private LocationRequest customRequest;

	protected String latitude, longitude;
	protected boolean gps_enabled, network_enabled;
	TextView txtLat, txtLong;
	private String extraData;
	
	private TextView step1, step2, step3, step4, step5, step5Header;
	private View finalLine;

	protected String[] drawerListViewItems;
	protected DrawerLayout drawerLayout;
	protected ListView drawerListView;
	protected ActionBarDrawerToggle actionBarDrawerToggle;
	protected ActionBar actionBar;
	final ObservationNotification notification = this;

	LocationClient mLocationClient = null;
	LocationRequest mLocationRequest = null;	
	private static final int MILLISECONDS_PER_SECOND = 1000;

	public static final int UPDATE_INTERVAL_IN_SECONDS = 60;
	private static final long UPDATE_INTERVAL =
	          MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	private static final long FASTEST_INTERVAL =
	          MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

	private Intent intent, intent1;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			displayCurrentLocation();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_notification);

		setNavigationDrawer();

		txtLat = (TextView) findViewById(R.id.Lat);
		txtLong = (TextView) findViewById(R.id.Long);
		step1 = (TextView) findViewById(R.id.Step1Text);
		step2 = (TextView) findViewById (R.id.Step2Text);
		step3 = (TextView) findViewById(R.id.Step3Text);
		step4 = (TextView) findViewById(R.id.Step4Text);
		step5 = (TextView) findViewById(R.id.Step5Text);
		step5Header = (TextView) findViewById(R.id.Step5);
		finalLine = (View) findViewById(R.id.finalLine);
		
		// mLocationClient.connect();

//		int isGooglePlayServiceAvilable = GooglePlayServicesUtil
//				.isGooglePlayServicesAvailable(this);
//		if (isGooglePlayServiceAvilable == ConnectionResult.SUCCESS) {
//
//			new Thread(new Runnable() {
//				public void run() {
//					// your computer is too fast, sleep 1 second
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					mHandler.sendMessage(mHandler.obtainMessage());
//				}
//			}).start();
//
//		} else {
//			// GooglePlayServicesUtil.getErrorDialog(isGooglePlayServiceAvilable,
//			// MainMenu.this, REQUEST_DIALOG).show();
//			txtLat.setText("Unable to fetch");
//			txtLong.setText("Unable to fetch");
//			Log.d("LOCATION", "did not connect to location services");
//		}


		datasource = new ObservationsDataSource(this);
		datasource.open();

		pref = getApplicationContext().getSharedPreferences("APP_PREFERENCES",
				MODE_PRIVATE);
		Log.d("SESSION DETAILS", "Session Id: " + pref.getInt("SESSION_ID", -1));

		getWindow().getDecorView().setBackgroundColor(
				getResources().getColor(R.color.listbackground));

		Intent sender = getIntent();
		extraData = sender.getExtras().getString("OBSERVATION_TYPE");
		Log.d("NOTIFICATION SCREEN", extraData);

		TextView ObservationText = (TextView) findViewById(R.id.optionNotification1);
		TextView LocationText = (TextView) findViewById(R.id.optionNotification2);
		ObservationText.setText(extraData + " Observation");
		ObservationText.setPaintFlags(ObservationText.getPaintFlags()
				| Paint.UNDERLINE_TEXT_FLAG);
		LocationText.setPaintFlags(LocationText.getPaintFlags()
				| Paint.UNDERLINE_TEXT_FLAG);
		
		if (extraData.equals("Stationary")){
			step1.setText("Take photos of pollinators");
			step2.setText("Confirm pollinators visited");
			step3.setText("Identify pollinators");
			step4.setText("Identify plant");
			step5.setText("Submit pollinators count");
		}
		else{
			step1.setText("Take photos of pollinators");
			step2.setText("Confirm pollinators visited");
			step3.setText("Identify pollinators");
			step4.setText("Submit pollinators count");
			step5.setText("");
			step5Header.setText("");
			finalLine.setBackgroundColor(color.white);
		}

		// TextView confirmationText = (TextView)
		// findViewById(R.id.optionNotification2);
		StringBuilder fileContents = new StringBuilder();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					getAssets().open("notification.txt"), "UTF-8"));

			// do reading, usually loop until end of file reading
			String mLine = "";
			while ((mLine = reader.readLine()) != null) {
				// process line
				fileContents.append("\n" + mLine);
				Log.d("NOTIF SCREEN", mLine);
			}

			// confirmationText.setText(fileContents);
			reader.close();
		} catch (IOException e) {
			// log the exception
			Log.d("NOTIF SCREEN", "unable to find / open the text file");
		}

		// EdiText confirmationText = (EdiText)
		// findViewById(R.id.optionNotification);

		// v = inflater.inflate(R.layout.activity_start, container, false);

		startButton = (Button) findViewById(R.id.startButton);
		

		intent = new Intent(this, PreviewDemo.class);
		intent1 = new  Intent(this, ActivityConfirmation.class);

		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// create Intent to take a picture and return control to the
				// calling
				// application
				Log.d("CAMERA", "Entering to get last session ID");
				session = datasource.getLastSessionId() + 1;
				Log.d("CAMERA", "current session ID: " + session);
				intent.putExtra("SESSIONID", String.valueOf(session));
				intent.putExtra("OBSERVATION_TYPE", extraData);
				
				intent1.putExtra("SESSIONID", String.valueOf(session));
				intent1.putExtra("OBSERVATION_TYPE", extraData);
				datasource.updateSessionEndDateTime(session, getCurrentDate());
				if (notification.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			        // this device has a camera
					startActivity(intent);
			    } else {
			        // no camera on this device
			    	buildDialogForCameraNotFound();
			    }


			}
		});

		mLocationClient = new LocationClient(this, this, this);
		//mLocationClient.connect();
		mLocationClient.registerConnectionCallbacks(this);

	}

	// @Override
	// public void onLocationChanged(Location location) {

	// txtLat.setText(location.getLatitude() + "");
	// txtLong.setText(location.getLongitude() + "");
	// }

	public String fromInt(int val) {
		return String.valueOf(val);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();

	}

	@Override
	protected void onStop() {
		if (mLocationClient != null){
			Log.d("LOCATION", "onSTOP location client disconnected");
		mLocationClient.unregisterConnectionCallbacks(this);
		mLocationClient.disconnect();
		}
		super.onStop();
	}

	@Override
	protected void onPause() {
		if (mLocationClient != null){
			Log.d("LOCATION", "onPAUSE location client disconnected");
		mLocationClient.unregisterConnectionCallbacks(this);
		mLocationClient.disconnect();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		mLocationClient.connect();
		super.onResume();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		Log.d("CAMERA", "requestCode:" + requestCode + "resultCode:"
				+ resultCode);
		Log.d("CAMERA", uriSavedImage.getPath());

		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
						.format(new Date());
				// observation = datasource.createObservations("sunflower",
				// "bumblebee", uriSavedImage.getPath(), session, timeStamp);
				Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File imagesFolder = new File(
						Environment.getExternalStorageDirectory(),
						"GreatSunflowerProject");
				imagesFolder.mkdirs(); // <----
				timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
						.format(new Date());
				String imageFileName = "gsp_" + timeStamp + ".jpg";
				File image = new File(imagesFolder, imageFileName);
				uriSavedImage = Uri.fromFile(image);
				imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
				startActivityForResult(imageIntent, 0);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// User cancelled the image capture
				session = datasource.getLastSessionId();
				Log.d("NOTIFICATION", "sessionId: " + session);
				Intent intent = new Intent(this, AnnotationActivity.class);
				// Intent intent = new
				// Intent(this,ObservationListFragment.class);
				intent.putExtra("SESSIONID", String.valueOf(session));
				startActivity(intent);
			} else {
				// Image capture failed, advise user
				session = datasource.getLastSessionId();
				Log.d("NOTIFICATION", "sessionId: " + session);
				Intent intent = new Intent(this, AnnotationActivity.class);
				// Intent intent = new
				// Intent(this,ObservationListFragment.class);
				intent.putExtra("SESSIONID", String.valueOf(session));
				startActivity(intent);
			}
		}

	}

	@Override
	public void onBackPressed() {

		this.finish();
		return;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		actionBarDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			Log.d("MAIN", "Home Item selected in the menu");
			if (drawerLayout.isDrawerOpen(drawerListView)) {
				drawerLayout.closeDrawer(drawerListView);
				Log.d("MAIN", "Drawer closed now");
			} else {
				drawerLayout.openDrawer(drawerListView);
				Log.d("MAIN", "Drawer opened now");
			}
		}
		if (item.getItemId() == R.id.menu_info) {
			Log.d("CAMERA", "Entered menu info item");
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder
					.setTitle("Notification")
					.setMessage(
							"An overview of the steps you will have to complete before submitting your observation in this session")
					.setCancelable(true);
			alertDialogBuilder.setPositiveButton("Close",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			Log.d("MAIN", "Drawer Item selection check");
			switch (position) {
			case 0:
				Intent intent1 = new Intent(notification, StartActivity.class);
				startActivity(intent1);
				drawerLayout.closeDrawer(drawerListView);
				break;
			 case 1:
				 Intent intent2 = new Intent(notification, ObservationListFragment.class);
				 startActivity(intent2);
			 drawerLayout.closeDrawer(drawerListView);
			 break;
			 case 2:
				 Intent intent3 = new Intent(notification, ActivityAbout.class);
				 startActivity(intent3);
				 drawerLayout.closeDrawer(drawerListView);
				 break;
			default:
				drawerLayout.closeDrawer(drawerListView);

			}

		}
	}

	private void setNavigationDrawer() {
		// get list items from strings.xml
		drawerListViewItems = getResources().getStringArray(R.array.items);
		// get ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		drawerListView.setAdapter(new ArrayAdapter<String>(notification,
				R.layout.drawer_listview_item, drawerListViewItems));

		// 2. App Icon
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		// 2.1 create ActionBarDrawerToggle
		actionBarDrawerToggle = new ActionBarDrawerToggle(notification, /*
																		 * host
																		 * Activity
																		 */
		drawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		);

		// 2.2 Set actionBarDrawerToggle as the DrawerListener
		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		// 2.3 enable and show "up" arrow
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// just styling option
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		drawerListView.setOnItemClickListener(new DrawerItemClickListener());
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
		//displayCurrentLocation();
		// mLocationClient.requestLocationUpdates(mLocationRequest,
		// (com.google.android.gms.location.LocationListener) this);

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Log.d("LOCATION", "service disconnected");
	}

	public void displayCurrentLocation() {
		// Get the current location's latitude & longitude
		if (mLocationClient.getLastLocation() == null) {
			txtLat.setText("Unable to fetch");
			txtLong.setText("Unable to fetch");
		} else {
			Location currentLocation = mLocationClient.getLastLocation();
			txtLat.setText(currentLocation.getLatitude() + "");
			txtLong.setText(currentLocation.getLongitude() + "");
		}

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null ){
			txtLat.setText(location.getLatitude() + "");
			txtLong.setText(location.getLongitude() + "");
		}
		Log.d("LOCATION", "Location has changed");
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = new MenuInflater(getApplicationContext());
		inflater.inflate(R.menu.activity_notification, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public void buildDialogForCameraNotFound(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		alertDialogBuilder
				.setTitle("Notification")
				.setMessage(
						"Oops! Unable to detect rear camera on this device. You can still continue to step 2 of your observation.")
				.setCancelable(true);
		alertDialogBuilder.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
				    	startActivity(intent1);
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	public String getCurrentDate(){
		return java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
	}

}

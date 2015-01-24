package org.greatsunflower.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class StartActivity extends SherlockFragmentActivity {

	private Button startButton;
	private String text = "Stationary";
	protected String[] drawerListViewItems;
	protected DrawerLayout drawerLayout;
	protected ListView drawerListView;
	protected ActionBarDrawerToggle actionBarDrawerToggle;
	protected ActionBar actionBar;
	final StartActivity startActivity = this;
	private ProgressBar mProgress;
	private boolean mProgressStatus = false;
	private CSVtoSQLiteDataSource csvsource;
	private CSVtoSQLiteTaxas taxa = null;

	private ObservationsDataSource datasource;
	private SQLiteObservations observation = null;
	private SharedPreferences pref;
	private ImageView helpIcon;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			setContentView(R.layout.app_start);

//			 check if you are connected or not
			 if(isConnected()){
			 Log.d("HOME - NETWORK", "connected");
			 // call AsynTask to perform network operation on separate thread
			 new
			 HttpAsyncTask().execute("http://demo.greatsunflower.org/sunflower7/sunflower_app/data");
			 }
			 else{
			 Log.d("HOME - NETWORK", "not connected");
			 }

			datasource = new ObservationsDataSource(startActivity);
			datasource.open();

			pref = getApplicationContext().getSharedPreferences(
					"APP_PREFERENCES", MODE_PRIVATE);

			setNavigationDrawer();
			startButton = (Button) findViewById(R.id.startButton);
			helpIcon = (ImageView) findViewById(R.id.helpIcon);
			RadioGroup radioGroup = (RadioGroup) findViewById(R.id.optionRadioGroup);
			radioGroup
					.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							RadioButton checkedRadioButton = (RadioButton) findViewById(checkedId);
							text = checkedRadioButton.getText().toString();
							Log.d("START SCREEN", text);
						}
					});

			startButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.d("START SCREEN", "acknowledging click");
					Intent intent = new Intent(startActivity,
							ObservationNotification.class);
					intent.putExtra("OBSERVATION_TYPE", text);
					Editor editor = pref.edit();
					editor.putString("OBSERVATION_TYPE", text);
					editor.putInt("SESSION_ID",
							datasource.getLastSessionId() + 1);
					editor.commit();
					Log.d("SESSION DETAILS",
							"Session Id: " + pref.getInt("SESSION_ID", -1));

					String startDate = java.text.DateFormat
							.getDateTimeInstance().format(
									Calendar.getInstance().getTime());
					datasource.createSession(pref.getInt("SESSION_ID", -1),
							text, 0, startDate, null);

					startActivity(intent);
				}
			});

			// getSupportFragmentManager().beginTransaction()
			// .replace(android.R.id.content, new LoginFragment()).commit();
			getWindow().getDecorView().setBackgroundColor(
					getResources().getColor(R.color.listbackground));
			LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				// Toast.makeText(this, "GPS Disabled",
				// Toast.LENGTH_SHORT).show();
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						startActivity);
				alertDialogBuilder.setTitle("GPS is Disabled!");
				alertDialogBuilder
						.setMessage("Enable GPS?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										startActivity(new Intent(
												Settings.ACTION_LOCATION_SOURCE_SETTINGS));
									}
								})
						.setNegativeButton("No",
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

			helpIcon.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					Log.d("HELP ALERT", "inside on click");
					buildHelpAlertDialog();
					return false;
				}
			});

			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

			}

		}
	};

	public String fromInt(int val) {
		return String.valueOf(val);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_check);

		csvsource = new CSVtoSQLiteDataSource(this);
		csvsource.open();

		new Thread(new Runnable() {
			public void run() {
				while (!mProgressStatus) {

					// populate taxonomy database for the first time;
					tryPopulatingDatabase();
					// your computer is too fast, sleep 0.1 second
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// commented for now
					mHandler.post(new Runnable() {
						public void run() {
							// mProgress.setProgress(mProgressStatus);
						}
					});
				}

				// database has been populated
				if (mProgressStatus) {
					// close the progress bar dialog
					mHandler.sendMessage(mHandler.obtainMessage());
				}
			}
		}).start();
		// csvsource.close();
		setNavigationDrawer();

	}

	private void setNavigationDrawer() {
		// get list items from strings.xml
		drawerListViewItems = getResources().getStringArray(R.array.items);
		// get ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		drawerListView.setAdapter(new ArrayAdapter<String>(startActivity,
				R.layout.drawer_listview_item, drawerListViewItems));

		// 2. App Icon
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		// 2.1 create ActionBarDrawerToggle
		actionBarDrawerToggle = new ActionBarDrawerToggle(startActivity, /*
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

	public void tryPopulatingDatabase() {
		if (csvsource.getRowCount() == 0) {
			mProgressStatus = csvsource.populateDatabase();
			csvsource.close();
		} else
			mProgressStatus = true;
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
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
					.setTitle("New Observation")
					.setMessage(
							"Start a new session by selecting your type of observation.")
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
				drawerLayout.closeDrawer(drawerListView);
				break;
			case 1:
				Intent intent2 = new Intent(StartActivity.this,
						ObservationListFragment.class);
				startActivity(intent2);
				drawerLayout.closeDrawer(drawerListView);
				break;
			case 2:
				Intent intent3 = new Intent(StartActivity.this,
						ActivityAbout.class);
				startActivity(intent3);
				drawerLayout.closeDrawer(drawerListView);
				break;
			default:
				drawerLayout.closeDrawer(drawerListView);

			}

		}
	}

	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
		return;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = new MenuInflater(getApplicationContext());
		inflater.inflate(R.menu.activity_start, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void buildHelpAlertDialog() {
		AlertDialog.Builder helpAlert = new AlertDialog.Builder(this);
		helpAlert
				.setTitle("Observation Types")
				.setMessage(
						"Stationary: Timed observations on single plant species. \n\n"
								+ "Travelling: Observations made over a known distance.")
				.setCancelable(true);
		helpAlert.setPositiveButton("Close",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = helpAlert.create();
		alertDialog.show();
	}

	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			Log.d("HOME - NETWORK", "entered doInBackground method");
			String testString = "Hello Ping from GreatSunflowerProject Android App!";

			return POST(urls[0], testString);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			Log.d("HOME - NETWORK", "Result: " + result);
			Log.d("HOME - NETWORK", "data sent to the server");
		}
	}

	public static String POST(String url, String testString) {
		InputStream inputStream = null;
		String result = "";
		String username = "demo", password = "demo";
		try {


			
			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			Log.d("HOME - NETWORK", "http client created");

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

		    /* authentication */
		    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
		        username + ":" + password);
		    ((AbstractHttpClient) httpclient).getCredentialsProvider().setCredentials(
		        new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
		        credentials);
			
			Log.d("HOME - NETWORK", "trying to make a post request");

			String json = "";
			String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

			// 3. build jsonObject
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("PING @ "+timeStamp, testString);

			// 4. convert JSONObject to JSON to String
			json = jsonObject.toString();
			Log.d("HOME - NETWORK", "JSON object: " + jsonObject.toString());

			// 5. set json to StringEntity
			StringEntity se = new StringEntity(json);
			Log.d("HOME - NETWORK", "json set to string entity");

			// 6. set httpPost Entity
			httpPost.setEntity(se);
			Log.d("HOME - NETWORK", "setting http post entity");

			// 7. Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			Log.d("HOME - NETWORK",
					"setting headers to inform server about the content");

			// 8. Execute POST request to the given URL
			Log.d("HOME - NETWORK", "trying to execute http client");
			HttpResponse httpResponse = httpclient.execute(httpPost);
			Log.d("HOME - NETWORK", "http response: " + httpResponse.toString());

			// 9. receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// 10. convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("HOME - NETWORK", "entering catch in POST method");
			Log.d("HOME - NETWORK", e.getLocalizedMessage());
		}

		// 11. return result
		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

}

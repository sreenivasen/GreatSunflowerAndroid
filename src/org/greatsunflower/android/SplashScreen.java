package org.greatsunflower.android;

import org.greatsunflower.android.StartActivity.DrawerItemClickListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SplashScreen extends SherlockFragmentActivity {

	private static final int PROGRESS = 0x1;

	private ProgressBar mProgress;
	private boolean mProgressStatus = false;
	protected String[] drawerListViewItems;
	protected DrawerLayout drawerLayout;
	protected ListView drawerListView;
	protected ActionBarDrawerToggle actionBarDrawerToggle;
	protected ActionBar actionBar;
	final SplashScreen startActivity = this;

	private Handler mHandler = new Handler(){
	    @Override
	    public void handleMessage ( Message message )
	    {
	    	mProgress.setVisibility(8);
	    	openDrawer();
	    }
	};

	private CSVtoSQLiteDataSource csvsource;
	private CSVtoSQLiteTaxas taxa = null;
	final SplashScreen sPlashScreen = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);

		mProgress = (ProgressBar) findViewById(R.id.ProgressBar);
		

		csvsource = new CSVtoSQLiteDataSource(this);
		csvsource.open();

		// start the progress bar

		new Thread(new Runnable() {
			public void run() {
				while (!mProgressStatus) {

					// populate taxonomy database for the first time;
					tryPopulatingDatabase();
					// your computer is too fast, sleep 0.5 second
					try {
						Thread.sleep(100);
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
					
					// sleep for 500 milliseconds, so that user can see that it has finished
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Load main activity
					//openDrawer();
					
					
				}
			}
		}).start();
		
		 // get list items from strings.xml
		 drawerListViewItems = getResources().getStringArray(R.array.items);
		 // get ListView defined in activity_main.xml
		 drawerListView = (ListView) findViewById(R.id.left_drawer);
		
		 // Set the adapter for the list view
		 drawerListView.setAdapter(new ArrayAdapter<String>(this,
		 R.layout.drawer_listview_item, drawerListViewItems));
		
		 // 2. App Icon
		 drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		 // 2.1 create ActionBarDrawerToggle
		 actionBarDrawerToggle = new ActionBarDrawerToggle(
		 this, /* host Activity */
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
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,GravityCompat.START);

	drawerListView.setOnItemClickListener(new DrawerItemClickListener());

	}
	
	public void openDrawer(){
		drawerLayout.openDrawer(drawerListView);
	}
	
	public void runNextActivity(){
		Intent intent = new Intent();
		//intent.setClass(sPlashScreen, MainActivity.class);
		intent.setClass(sPlashScreen, StartActivity.class);
		startActivity(intent);
	}

	public void tryPopulatingDatabase() {
		if (csvsource.getRowCount() == 0)
			mProgressStatus = csvsource.populateDatabase();
		else
			mProgressStatus = true;
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
		if (item.getItemId() ==R.id.menu_info) {
		Log.d("CAMERA", "Entered menu info item");
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Welcome")
		.setMessage("Start Exploring!")
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
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = new MenuInflater(getApplicationContext());
		inflater.inflate(R.menu.activity_start, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	

	public class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			Log.d("MAIN", "Drawer Item selection check");
			switch (position) {
			case 0:
				runNextActivity();
				drawerLayout.closeDrawer(drawerListView);
				break;
			default:
				Toast.makeText(startActivity, ((TextView) view).getText(),
						Toast.LENGTH_LONG).show();
				drawerLayout.closeDrawer(drawerListView);

			}

		}
	}

}

package org.greatsunflower.android;

import org.greatsunflower.android.ObservationNotification.DrawerItemClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class IdentifyActivity extends SherlockFragmentActivity {

	EditText pollinatorCount;
	AutoCompleteTextView pollinatorSearch;
	Button buttonAdd;
	LinearLayout container;
	ScrollView mScroll;
	ImageView imageAdd;
	
	protected String[] drawerListViewItems;
	protected DrawerLayout drawerLayout;
	protected ListView drawerListView;
	protected ActionBarDrawerToggle actionBarDrawerToggle;
	protected ActionBar actionBar;
	final IdentifyActivity identify = this;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_identify);
		setNavigationDrawer();
		getWindow().getDecorView().setBackgroundColor(
				getResources().getColor(R.color.listbackground));
		pollinatorSearch = (AutoCompleteTextView) findViewById(R.id.pollinatorSearch);
		pollinatorCount = (EditText) findViewById(R.id.numberInput);
		//buttonAdd = (Button) findViewById(R.id.addButton);
		imageAdd = (ImageView) findViewById(R.id.addImage);
		container = (LinearLayout) findViewById(R.id.container);
		mScroll = new ScrollView(this);

		imageAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!pollinatorCount.getText().toString().equals("") && !pollinatorSearch.getText().toString().equals(""))
				{LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final View addView = layoutInflater.inflate(R.layout.dynamic_row, null);
				TextView textOut1 = (TextView) addView
						.findViewById(R.id.textout1);
				TextView textOut2 = (TextView) addView
						.findViewById(R.id.textout2);
				textOut1.setText(pollinatorSearch.getText().toString());
				textOut2.setText(pollinatorCount.getText().toString());
				pollinatorSearch.setText("");
				pollinatorSearch.setHint("Search Another Pollinator");
				pollinatorCount.setText("");
//				Button buttonRemove = (Button) addView
//						.findViewById(R.id.removeButton);
				ImageView imageRemove = (ImageView) addView.findViewById(R.id.removeImage);
				imageRemove.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						((LinearLayout) addView.getParent())
								.removeView(addView);
					}
				});

				container.addView(addView);
				
			}
			}
		});
		

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
					Intent intent2 = new Intent(identify, StartActivity.class);
					startActivity(intent2);
					drawerLayout.closeDrawer(drawerListView);
					break;
//				case 1:
//					Intent intent2 = new Intent(StartActivity.this, identify_activity.class);
//					startActivity(intent2);
//					drawerLayout.closeDrawer(drawerListView);
//					break;
				default:
					Toast.makeText(identify, ((TextView) view).getText(),
							Toast.LENGTH_LONG).show();
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
			drawerListView.setAdapter(new ArrayAdapter<String>(identify,R.layout.drawer_listview_item, drawerListViewItems));

			// 2. App Icon
			drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

			// 2.1 create ActionBarDrawerToggle
			actionBarDrawerToggle = new ActionBarDrawerToggle(identify, /*
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

}

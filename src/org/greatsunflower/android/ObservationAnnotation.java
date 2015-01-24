package org.greatsunflower.android;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * Activity used to annotate observations. The activity acts as fragment container 
 * for multiple fragments. These fragments are added to the view pager to make sure
 * the user is able to drill down the list elegantly. This allows user to drill through
 * multiple tabs.
 */

public class ObservationAnnotation extends SherlockFragmentActivity implements FragmentCommunicator{

	static ViewPager sViewPager;
	TabsAdapter mTabsAdapter;
	TextView tabCenter;
	TextView tabText;
	ActionBar bar;
	private SharedPreferences pref;
	private int mSessionId;
	private String mTaxa = null, mMiddleLevel = null, 
			mCommonName = null, mVisitorGenus = null, 
			mVisitorSpecies = null;
	
	final ObservationAnnotation annotation = this;
	private SQLiteObservations mObservation = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		pref = getApplicationContext().getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE);
		Log.d("SESSION DETAILS", "Session Id: " + pref.getInt("SESSION_ID", -1));
		mSessionId = pref.getInt("SESSION_ID", -1);
		
		if (pref.getString("OBSERVATION_TYPE", null).equals("Stationary")){
			annotation.setTitle("Step 3 of 5");
		}
		else
			annotation.setTitle("Step 3 of 4");
		
		sViewPager = new ViewPager(this);
		sViewPager.setId(R.id.pollinatorPager);

		setContentView(sViewPager);
		bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		mTabsAdapter = new TabsAdapter(this, sViewPager);

		mTabsAdapter.addTab(bar.newTab().setText("Pollinators"),
				FragmentTabOne.class, null);

		
		mTabsAdapter.addTab(bar.newTab().setText("Level 1"),
				FragmentTabTwo.class, null);
		mTabsAdapter.addTab(bar.newTab().setText("Level 2"),
				FragmentTabThree.class, null);


	}
	
	/** Responds to user's selection of an element in the list from current tab. */
	public void respond (String data){
		if(mTabsAdapter.getItem(sViewPager.getCurrentItem()) instanceof FragmentTabTwo){
			Editor editor = pref.edit();
			editor.putString("TAXA", data );
			editor.commit();
			FragmentTabTwo fragmentLevelTwo = (FragmentTabTwo)sViewPager.getAdapter().instantiateItem(sViewPager, sViewPager.getCurrentItem());
			fragmentLevelTwo.changeText(data);
			}
		else if(mTabsAdapter.getItem(sViewPager.getCurrentItem()) instanceof FragmentTabThree){
			Editor editor = pref.edit();
			editor.putString("MIDDLE_LEVEL", data );
			editor.commit();
			FragmentTabThree fragmentLevelThree = (FragmentTabThree)sViewPager.getAdapter().instantiateItem(sViewPager, sViewPager.getCurrentItem());
			fragmentLevelThree.changeText(data);
		}		
	}

	/** Sets the current tab in the activity */
	public boolean setCurrentTab(int id) { 
		sViewPager.setCurrentItem(id);
		return true;
		 }

	/** Returns current session id  */
	public int getCurrentSession(){
		return mSessionId;
	}
	
	/** Returns a dialog if there are no images for the current session  */
	public void buildWarningDialog(Context context){
		AlertDialog.Builder helpAlert = new AlertDialog.Builder(context);
		helpAlert
				.setTitle("Notification")
				.setMessage(
						"No pictures found for this session")
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
	
	/** Returns a dialog to the user to notify based on the API level of the device */
	public void buildAPILevelDialogWarning(Context context){
		AlertDialog.Builder helpAlert = new AlertDialog.Builder(context);
		helpAlert
				.setTitle("Notification")
				.setMessage(
						"Sorry. This feature is currently not supported on this device.")
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
	
	/** Inserts a row into SQLite database based on the values selected by user */
	public SQLiteObservations insertPollinators(int mSessionId, Context context,ObservationsDataSource source,int count){
		Log.d("CREATE POLLINATOR", count +  " COUNT");
		pref = context.getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE);
		mTaxa = pref.getString("TAXA", "");
		mMiddleLevel = pref.getString("MIDDLE_LEVEL", "");
		mCommonName = pref.getString("COMMON_NAME", "");
		mVisitorGenus = pref.getString("VISITOR_GENUS", "");
		mVisitorSpecies = pref.getString("VISITOR_SPECIES", "");
		mObservation = source.createPollinator(count,mTaxa, mMiddleLevel, mCommonName, mVisitorGenus, mVisitorSpecies,mSessionId,getCurrentDate());
		Editor editor = pref.edit();
		editor.putString("TAXA", null );
		editor.putString("MIDDLE_LEVEL", null );
		editor.putString("COMMON_NAME", null );
		editor.putString("VISITOR_GENUS", null);
		editor.commit();
		return mObservation;
	}
	
	/** Returns a current date */
	public String getCurrentDate(){
		return java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
	}

	
	public static class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mActionBar = activity.getSupportActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
		}

		public void onPageScrollStateChanged(int state) {
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
		
	
	}
	
	
	
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = new MenuInflater(getApplicationContext());
		inflater.inflate(R.menu.activity_start, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.menu_info) {
			Log.d("CAMERA", "Entered menu info item");
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder
					.setTitle("Identify Pollinators")
					.setMessage(
							"Add all the pollinators that visited during this observation.")
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
}

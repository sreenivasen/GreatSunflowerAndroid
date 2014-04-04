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
import android.widget.TabHost;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class PlantAnnotation extends SherlockFragmentActivity implements FragmentCommunicator{

	static ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	TextView tabCenter;
	TextView tabText;
	ActionBar bar;
	static TabHost tabHost;
	FragmentTab2 f2;
	private SharedPreferences pref;
	private int sessionId;
	private String plant_family = null, plant_genus = null, plant_species = null, plant_var_subspecies = null;
	final PlantAnnotation annotation = this;
	private ObservationsDataSource datasource;
	private SQLiteObservations observation = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		
		pref = getApplicationContext().getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE);
		Log.d("SESSION DETAILS", "Session Id: " + pref.getInt("SESSION_ID", -1));
		sessionId = pref.getInt("SESSION_ID", -1);
		annotation.setTitle("Step 4 of 5");
		

		
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.plantPager);

		setContentView(mViewPager);
		bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		mTabsAdapter = new TabsAdapter(this, mViewPager);

		mTabsAdapter.addTab(bar.newTab().setText("Plants"),
				PlantTab1.class, null);

		
		mTabsAdapter.addTab(bar.newTab().setText("Level 1"),
				PlantTab2.class, null);
//		mTabsAdapter.addTab(bar.newTab().setText("Level 2"),
//				PlantTab3.class, null);
//		mTabsAdapter.addTab(bar.newTab().setText("Level 3"),
//				PlantTab4.class, null);

	}
	
	public void respond (String data){
		if(mTabsAdapter.getItem(mViewPager.getCurrentItem()) instanceof PlantTab2){
			  // FragmentTab1 f1 = (FragmentTab1) mViewPager.getCurrentItem();
			Editor editor = pref.edit();
			editor.putString("PLANT_FAMILY", data );
			editor.commit();
			PlantTab2 frag2= (PlantTab2)mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());
			frag2.changeText(data);
			}
		else if(mTabsAdapter.getItem(mViewPager.getCurrentItem()) instanceof PlantTab3){
			Editor editor = pref.edit();
			editor.putString("PLANT_GENUS", data );
			editor.commit();
			PlantTab3 frag3= (PlantTab3)mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());
			frag3.changeText(data);
		}
		else if(mTabsAdapter.getItem(mViewPager.getCurrentItem()) instanceof PlantTab4){
			Editor editor = pref.edit();
			editor.putString("PLANT_SPECIES", data );
			editor.commit();
			PlantTab4 frag4= (PlantTab4)mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());
			frag4.changeText(data);
		}
		else if(mTabsAdapter.getItem(mViewPager.getCurrentItem()) instanceof PlantTab1){
			Editor editor = pref.edit();
			editor.putString("PLANT_VAR_SUBSPECIES", data );
			editor.commit();
//			PlantTab1 frag1= (PlantTab1)mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());
//			frag1.changeText(data);
		}
		

	}

	
	public boolean setCurrentTab(int id) { 
		mViewPager.setCurrentItem(id);
		return true;
		 }
	
	public int getCurrentSession(){
		return sessionId;
	}
	
	
	public SQLiteObservations insertPlants(int sessionId, Context context,ObservationsDataSource source){

		pref = context.getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE);
		plant_family = pref.getString("PLANT_FAMILY", "");
		plant_genus = pref.getString("PLANT_GENUS", "");
		plant_species = pref.getString("PLANT_SPECIES", "");
		plant_var_subspecies = pref.getString("PLANT_VAR_SUBSPECIES", "");
		observation = source.createPlants(plant_family, plant_genus, plant_species, plant_var_subspecies,sessionId,getCurrentDate());
		Editor editor = pref.edit();
		editor.putString("TAXA", null );
		editor.putString("MIDDLE_LEVEL", null );
		editor.putString("COMMON_NAME", null );
		editor.putString("VISITOR_GENUS", null);
		editor.commit();
		return observation;
	}
	
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
		// TODO Auto-generated method stub
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
					.setTitle("Identify Plant")
					.setMessage(
							"Add the plant that was observed during this session.")
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

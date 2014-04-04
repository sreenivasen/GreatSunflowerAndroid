package org.greatsunflower.android;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ObservationAnnotation extends SherlockFragmentActivity implements FragmentCommunicator{

	static ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	TextView tabCenter;
	TextView tabText;
	ActionBar bar;
	static TabHost tabHost;
	FragmentTab2 f2;
	private SharedPreferences pref;
	private int sessionId;
	private String taxa = null, middle_level = null, common_name = null, visitor_genus = null, visitor_species = null;
	
	final ObservationAnnotation annotation = this;
	private ObservationsDataSource datasource;
	private SQLiteObservations observation = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		
		pref = getApplicationContext().getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE);
		Log.d("SESSION DETAILS", "Session Id: " + pref.getInt("SESSION_ID", -1));
		sessionId = pref.getInt("SESSION_ID", -1);
		
		if (pref.getString("OBSERVATION_TYPE", null).equals("Stationary")){
			annotation.setTitle("Step 3 of 5");
		}
		else
			annotation.setTitle("Step 3 of 4");
		
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.pollinatorPager);

		setContentView(mViewPager);
		bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		mTabsAdapter = new TabsAdapter(this, mViewPager);

		mTabsAdapter.addTab(bar.newTab().setText("Pollinators"),
				FragmentTab1.class, null);

		
		mTabsAdapter.addTab(bar.newTab().setText("Level 1"),
				FragmentTab2.class, null);
		mTabsAdapter.addTab(bar.newTab().setText("Level 2"),
				FragmentTab3.class, null);
//		mTabsAdapter.addTab(bar.newTab().setText("Level 3"),
//				FragmentTab4.class, null);
//		mTabsAdapter.addTab(bar.newTab().setText("Level 4"),
//				FragmentTab5.class, null);


	}
	
	public void respond (String data){
		if(mTabsAdapter.getItem(mViewPager.getCurrentItem()) instanceof FragmentTab2){
			  // FragmentTab1 f1 = (FragmentTab1) mViewPager.getCurrentItem();
			Editor editor = pref.edit();
			editor.putString("TAXA", data );
			editor.commit();
			FragmentTab2 frag2= (FragmentTab2)mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());
			frag2.changeText(data);
			}
		else if(mTabsAdapter.getItem(mViewPager.getCurrentItem()) instanceof FragmentTab3){
			Editor editor = pref.edit();
			editor.putString("MIDDLE_LEVEL", data );
			editor.commit();
			FragmentTab3 frag3= (FragmentTab3)mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());
			frag3.changeText(data);
		}
		else if(mTabsAdapter.getItem(mViewPager.getCurrentItem()) instanceof FragmentTab4){
			Editor editor = pref.edit();
			editor.putString("COMMON_NAME", data );
			editor.commit();
			FragmentTab4 frag4= (FragmentTab4)mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());
			frag4.changeText(data);
		}
		else if(mTabsAdapter.getItem(mViewPager.getCurrentItem()) instanceof FragmentTab5){

			Editor editor = pref.edit();
			editor.putString("VISITOR_GENUS", data );
			editor.commit();
			FragmentTab5 frag5= (FragmentTab5)mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());
			frag5.changeText(data);
		}
		

	}

	
	public boolean setCurrentTab(int id) { 
		mViewPager.setCurrentItem(id);
		return true;
		 }
	
	public int getCurrentSession(){
		return sessionId;
	}
	
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
	
	public void showDialogForImages(int sessionId, Context context){
		final CharSequence[] items = {"one", "two", "three"};
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pick image(s)")
        .setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which,
                    boolean isChecked) {
                Log.d("FRAGMENT 1", items[which] + " " + isChecked);
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @SuppressLint("NewApi")
			@Override
            public void onClick(DialogInterface dialog, int which) {
                //reading content of the dialog
                AlertDialog a = ((AlertDialog)dialog);
                Log.d("FRAGMENT 1", "" + a.getListView().getCheckedItemCount());
                for(long i: a.getListView().getCheckItemIds()){
                    Log.d("FRAGMENT 1", i + "");
                }
            }
        });
        
        final Dialog dlg = builder.create();
        dlg.show();
	}

	
	public SQLiteObservations insertPollinators(int sessionId, Context context,ObservationsDataSource source,int count){
		Log.d("CREATE POLLINATOR", count +  " COUNT");
		pref = context.getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE);
		taxa = pref.getString("TAXA", "");
		middle_level = pref.getString("MIDDLE_LEVEL", "");
		common_name = pref.getString("COMMON_NAME", "");
		visitor_genus = pref.getString("VISITOR_GENUS", "");
		visitor_species = pref.getString("VISITOR_SPECIES", "");
		observation = source.createPollinator(count,taxa, middle_level, common_name, visitor_genus, visitor_species,sessionId,getCurrentDate());
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

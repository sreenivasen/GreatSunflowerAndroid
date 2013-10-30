package org.greatsunflower.android;

import org.greatsunflower.android.LocationDemand;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity {

    private LocationDemand locDemand;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, new LoginFragment()).commit();
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.listbackground));
	       LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	       if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	            //Toast.makeText(this, "GPS Disabled", Toast.LENGTH_SHORT).show();
	            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	            alertDialogBuilder.setTitle("GPS is Disabled!");
	            alertDialogBuilder
	                    .setMessage("Enable GPS?")
	                    .setCancelable(false)
	                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
	                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	                        }
	                    })
	                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
	                            dialog.cancel();
	                        }
	                    });

	            AlertDialog alertDialog = alertDialogBuilder.create();
	            alertDialog.show();
	        }
	        
	    
	        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

	            Intent intent = new Intent(this, LocationDemand.class);
	            startService(intent);
	        }

	}
	
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            locDemand = ((LocationDemand.MyBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locDemand = null;
        }
    };

	@Override
//	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
//		// TODO Auto-generated method stub
//		return super.onCreateOptionsMenu(menu);
//	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = new MenuInflater(getApplicationContext());
		inflater.inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int nextInt;

		switch (item.getItemId()) {
		case R.id.menu_information:
			Intent intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);
			return true;
		default:
			return true;
		}
	}
	
    @Override
    protected void onStart() {
        super.onStart();

 //       bindService(new Intent(this, LocationDemand.class), mConnection,
 //               Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {

        stopService(new Intent(this, LocationDemand.class));
        super.onStop();
    }
    
    @Override
    protected void onPause() {
 //       unbindService(mConnection);
        stopService(new Intent(this, LocationDemand.class));

        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, LocationDemand.class);
        startService(intent);
        
 //       bindService(new Intent(this, LocationDemand.class), mConnection,
 //               Context.BIND_AUTO_CREATE);


    }

}

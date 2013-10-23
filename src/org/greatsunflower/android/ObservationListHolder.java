package org.greatsunflower.android;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class ObservationListHolder extends SherlockFragmentActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.listbackground));
		setContentView(R.layout.listholder);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: 
	    	Intent homeIntent = new Intent(this, MainActivity.class);
	    	homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
	    	startActivity(homeIntent);
		
	        return true;
		}
	    return super.onOptionsItemSelected(item);
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

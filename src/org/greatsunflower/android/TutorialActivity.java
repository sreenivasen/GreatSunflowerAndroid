package org.greatsunflower.android;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class TutorialActivity extends SherlockFragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_horizontaltutorial);
		
		final TextView tv = new TextView(this);
		tv.setMovementMethod(new ScrollingMovementMethod());

	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// TODO Auto-generated method stub
//		MenuInflater inflater = new MenuInflater(getApplicationContext());
//		inflater.inflate(R.menu.activity_information, menu);
//		return super.onCreateOptionsMenu(menu);
//	}

}

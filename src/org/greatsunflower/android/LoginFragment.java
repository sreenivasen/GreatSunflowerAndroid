package org.greatsunflower.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.PorterDuff;

import com.actionbarsherlock.app.SherlockFragment;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;



public class LoginFragment extends SherlockFragment {

	private View v;
	private EditText emailID;
	private Button startButton;
	private static String userID;
	private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	v = inflater.inflate(R.layout.activity_start, container, false);
	emailID = (EditText) v.findViewById(R.id.inputEmail);
	startButton = (Button) v.findViewById(R.id.startButton);
	//startButton.setBackgroundColor(getResources().getColor(R.color.skyblue));
	//startButton.getBackground().setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.MULTIPLY);

		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				if(checkEmailID()) {
					new Thread ( new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							//Intent intent = new Intent(getSherlockActivity(), ObservationListHolder.class);
							
							Intent intent = new Intent(getSherlockActivity(), ObservationListFragment.class);
				            startActivity(intent);
							
						}
					}).start();
					
				}

			}

		});

		return v;

	}

	private boolean checkEmailID() {
		// TODO Auto-generated method stub
		userID = emailID.getText().toString();
		Log.d("Email ID", "Email entered is: " + userID);
		InputMethodManager inputManager = ((InputMethodManager) getSherlockActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE));
		if (inputManager.isActive()) {
			inputManager.hideSoftInputFromWindow(emailID.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);

		}
		// onClick of button perform this simplest code.
		if (userID.matches(emailPattern)) {
			//Crouton.showText(getSherlockActivity(), "Valid email", Style.CONFIRM, R.id.alternate_view_group);
			return true;
		} else {
			Crouton.showText(getSherlockActivity(), "Invalid email", Style.ALERT, R.id.alternate_view_group);
			return false;
		}
		

	}

}

package org.greatsunflower.android;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.PorterDuff;

import com.actionbarsherlock.app.SherlockFragment;

public class LoginFragment extends SherlockFragment {

	private View v;
	private EditText emailID;
	private Button startButton;
	private static String userID;
	private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
	private String text = "Casual";
	
	
	

	
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
//	emailID = (EditText) v.findViewById(R.id.inputEmail);
	startButton = (Button) v.findViewById(R.id.startButton);
	
	
	
	
	
	//String radiovalue = v.findViewById(rg.getCheckedRadioButtonId()).getTag().toString();  
	//Log.d("START SCREEN",radiovalue);
	
	//startButton.setBackgroundColor(getResources().getColor(R.color.skyblue));
	//startButton.getBackground().setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.MULTIPLY);
//
//	RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.optionRadioGroup);
//    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//        @Override
//        public void onCheckedChanged(RadioGroup group, int checkedId) 
//        {
//            RadioButton checkedRadioButton = (RadioButton) v.findViewById(checkedId);
//            text = checkedRadioButton.getText().toString();
//            Log.d("START SCREEN", text);
//        }
//    });
   
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				if(true){//checkEmailID()) {
					new Thread ( new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getSherlockActivity(), ObservationNotification.class);
							//Intent intent = new Intent(getSherlockActivity(), SubmissionActivity.class);
				            intent.putExtra("OBSERVATION_TYPE", text + " Observation");
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
		
		String result = "";
		//the year data to send
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userID",userID));
		Log.d("SREENI", "the namevaluepair created:" + nameValuePairs);
		InputStream is = null;
		
		ConnectivityManager cm = (ConnectivityManager) getSherlockActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
		    // There are no active networks.
		    return false;
		}
		
		//http post
		try{
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new HttpPost("http://sfsuswe.com/~rsreen/getEmailIds.php?");
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));		        
		        HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        is = entity.getContent();
		        
		}catch(Exception e){
		        Log.d("SREENI", "Error in http connection "+e.toString());
		}
		
		//convert response to string
		try{
		        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);		        
		        result = reader.readLine();
		        Log.d("SREENI", "result string is: "+ result);
		}catch(Exception e){
		        Log.d("SREENI", "Error converting result "+e.toString());
		}

		InputMethodManager inputManager = ((InputMethodManager) getSherlockActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE));
		if (inputManager.isActive()) {
			inputManager.hideSoftInputFromWindow(emailID.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		}
		
			if(result.equals("true")) {
				//do nothing
				Log.d("SREENI", "entering IF condition");
				return true;
			}
			else{
				Log.d("SREENI", "entering ELSE condition");
//				Crouton.showText(getSherlockActivity(), "Invalid email", Style.ALERT, R.id.alternate_view_group);
				return true;
			
		//parse json data
//		try{
//			
//			Log.d("SREENI", "boolean value: " + result.equals("true"));
//			
//			if(result.equals("true")) {
//				//do nothing
//				Log.d("SREENI", "entering IF condition");
//				return true;
//			}
//			else{
//				Log.d("SREENI", "entering ELSE condition");
//				Crouton.showText(getSherlockActivity(), "Invalid email", Style.ALERT, R.id.alternate_view_group);
//				return true;
//				
////		        JSONArray jArray = new JSONArray(result);
//		        
////		        for(int i=0;i<jArray.length();i++){
////		                JSONObject json_data = jArray.getJSONObject(i);
////		                Log.i("log_tag","id: "+json_data.getInt("id")+
////		                        ", name: "+json_data.getString("emailId")+
////		                        ", age: "+json_data.getInt("age")
////		                );
//////		                if(json_data.getString("emailId").equals(userID)){
//////		                	return true;
//////		                }
//////		                else {
//////		        			Crouton.showText(getSherlockActivity(), "Invalid email", Style.ALERT, R.id.alternate_view_group);
//////		        			return false;
//////		        		}
////		                	
////		        }
//			}
//		}
//		catch(Exception e){
//			Log.e("log_tag", "Error parsing data "+e.toString());
//		}
//		catch(JSONException e){
//		        Log.e("log_tag", "Error parsing data "+e.toString());
//		}
		


//		}
		// onClick of button perform this simplest code.
//		if (userID.matches(emailPattern)) {
//			//Crouton.showText(getSherlockActivity(), "Valid email", Style.CONFIRM, R.id.alternate_view_group);
//			return true;
//		} else {
//			Crouton.showText(getSherlockActivity(), "Invalid email", Style.ALERT, R.id.alternate_view_group);
//			return false;
//		}
//		return true;

	}
		}
	
}

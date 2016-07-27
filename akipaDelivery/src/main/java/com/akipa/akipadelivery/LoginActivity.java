package com.akipa.akipadelivery;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.akipa.listview.utils.ReusableClass;
import com.akipa.navigation.DashBoardActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	EditText editTextEmail;
	EditText editTextPassword;
	private ProgressDialog pgLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		editTextEmail = (EditText) findViewById(R.id.editTextEmail);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);

	}

	public void loginNow(View v) {
		v.findViewById(R.id.buttonLogin).startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.image_click));

		if (!MandatoryValidation(editTextEmail).equalsIgnoreCase("") && !MandatoryValidation(editTextPassword).equalsIgnoreCase("")) {
			//Showing progress dialog 
			pgLogin = new ProgressDialog(LoginActivity.this);
			pgLogin.setMessage("Please wait a min ...");
			pgLogin.setIndeterminate(true);
			pgLogin.setCancelable(true);
			pgLogin.setCanceledOnTouchOutside(false);

			pgLogin.show();

			new MyAsyncTaskForLogin().execute(editTextEmail.getText().toString(), editTextPassword.getText().toString());
		}
	}


	//===================================================================================================================================
	//END Sending userName, Password to server and checking login successful or not
	//===================================================================================================================================

	private class MyAsyncTaskForLogin extends AsyncTask<String, Integer, Double> {

		String responseBody;
		int responseCode;

		@Override
		protected Double doInBackground(String... params) {
			postData(params[0], params[1]);
			return null;
		}

		protected void onPostExecute(Double result) {
			//The HTTP status messages in the 200 series reflect that the request was successful. 
			if (responseCode == 200) {
				Log.d("Log", responseBody);
				processLoginResponce(responseBody);
			}
			//Not getting proper response
			else {
				if (pgLogin.isShowing()) {
					pgLogin.cancel();
					pgLogin.dismiss();
				}
				Toast.makeText(LoginActivity.this, "Sorry!! No responce from server.", Toast.LENGTH_LONG).show();
			}

		}

		protected void onProgressUpdate(Integer... progress) {

		}

		public void postData(String email, String pass) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet("http://www.renzovilela.tk/akipa/rest/validate_user?username=" + email + "&password=" + pass);

			try {
				Log.i("TAG", "URL: " + "http://www.renzovilela.tk/akipa/rest/validate_user?username=" + email + "&password=" + pass);
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httpGet);

				responseCode = response.getStatusLine().getStatusCode();
				responseBody = EntityUtils.toString(response.getEntity());
			} catch (Throwable t) {
				Log.d("Error Time of Login", t + "");
			}
		}
	}

	//===================================================================================================================================
	//END Sending userName, Password to server and checking login successful or not  
	//===================================================================================================================================

	//===================================================================================================================================
	//processing the XML got from server 
	//===================================================================================================================================
	private void processLoginResponce(String responceFromServer) {
		if (pgLogin.isShowing()) {
			pgLogin.cancel();
			pgLogin.dismiss();
		}

		int userId = Integer.parseInt(responceFromServer.substring(1, responceFromServer.length() - 1));

		if (userId == 0) {
			Toast.makeText(this, "Please check your credentials !!.", Toast.LENGTH_LONG).show();
		} else {
			ReusableClass.saveInPreference("isLogedIn", "yes", LoginActivity.this);
			Intent i = new Intent(LoginActivity.this, DashBoardActivity.class);
			finish();
			startActivity(i);
		}
	}
	//===================================================================================================================================
	//processing the XML 
	//===================================================================================================================================


	public String MandatoryValidation(EditText edt) {
		String text = "";
		if (edt.getText().toString().length() <= 0) {
			edt.setError("It's mandatory.");
			text = "";
		} else {
			text = edt.getText().toString();
			edt.setError(null);
		}
		return text;
	}
}

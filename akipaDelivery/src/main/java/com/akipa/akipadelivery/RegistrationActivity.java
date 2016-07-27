package com.akipa.akipadelivery;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.akipa.akipadelivery.R;

public class RegistrationActivity extends Activity {

	EditText editTextFirstName;
	EditText editTextLastName;
	EditText editTextEmailAddress;
	EditText editTextPhoneNo;
	EditText editTextPassword;
	EditText editTextFbId;
	RadioGroup radioSexGroup;
	RadioButton radioSexButton;
	private ProgressDialog pgLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_activity);

		editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
		editTextLastName = (EditText) findViewById(R.id.editTextLastName);
		radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
		editTextEmailAddress = (EditText) findViewById(R.id.editTextEmailAddress);
		editTextPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		editTextFbId = (EditText) findViewById(R.id.editTextFbId);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			editTextFirstName.setText(extras.getString("first_name"));
			editTextLastName.setText(extras.getString("last_name"));

			if (extras.getString("sex").equalsIgnoreCase("male"))
				radioSexGroup.check(R.id.radioMale);
			else
				radioSexGroup.check(R.id.radioFemale);

			editTextEmailAddress.setText(extras.getString("email"));
			editTextFbId.setText(extras.getString("fb_id"));
		}
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(this, MainActivity.class);
		finish();
		startActivity(i);
	}

	public void registerMe(View v) {
		v.findViewById(R.id.buttonReg).startAnimation(AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.image_click));

		Log.d("TAG", "validation: " + MandatoryValidation(editTextFirstName));
		if (!MandatoryValidation(editTextFirstName).equalsIgnoreCase("") && !MandatoryValidation(editTextLastName).equalsIgnoreCase("") && !Is_Valid_Email(editTextEmailAddress).equalsIgnoreCase("") && !MandatoryValidation(editTextPhoneNo).equalsIgnoreCase("") && !MandatoryValidation(editTextPassword).equalsIgnoreCase("")) {
			//Showing progress dialog 
			pgLogin = new ProgressDialog(RegistrationActivity.this);
			pgLogin.setMessage("Please wait registering you ...");
			pgLogin.setIndeterminate(true);
			pgLogin.setCancelable(true);
			pgLogin.setCanceledOnTouchOutside(false);

			pgLogin.show();

			int selectedId = radioSexGroup.getCheckedRadioButtonId();
			radioSexButton = (RadioButton) findViewById(selectedId);

			new MyAsyncTaskForAllEnquire().execute(editTextFirstName.getText().toString(), editTextLastName.getText().toString(), editTextEmailAddress.getText().toString(), editTextPhoneNo.getText().toString(), editTextPassword.getText().toString(), editTextFbId.getText().toString(), radioSexButton.getText().toString(), toString());
		} else {
			Toast.makeText(this, "Please check your credentials !!", Toast.LENGTH_LONG).show();
		}
	}

	private class MyAsyncTaskForAllEnquire extends AsyncTask<String, Integer, Double> {

		String responseBody;
		int responseCode;

		@Override
		protected Double doInBackground(String... params) {
			postData(params[0], params[1], params[2], params[3], params[4], params[5], params[6]);
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
				Toast.makeText(RegistrationActivity.this, "Sorry network problem", Toast.LENGTH_LONG).show();
			}

		}

		protected void onProgressUpdate(Integer... progress) {

		}

		public void postData(String first_name, String last_name, String email, String phone_no, String pass, String fb_id, String sex) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://www.renzovilela.tk/akipa/rest/save_user");

			try {
				// Data that I am sending
				List nameValuePairs = new ArrayList();
				nameValuePairs.add(new BasicNameValuePair("nombre", first_name));
				nameValuePairs.add(new BasicNameValuePair("apellido", last_name));
				nameValuePairs.add(new BasicNameValuePair("correo", email));
				nameValuePairs.add(new BasicNameValuePair("telefono", phone_no));
				nameValuePairs.add(new BasicNameValuePair("password", pass));
				nameValuePairs.add(new BasicNameValuePair("facebook", fb_id));
				nameValuePairs.add(new BasicNameValuePair("sexo", sex));


				Log.e("nombre", first_name);
				Log.e("apellido", last_name);
				Log.e("correo", email);
				Log.e("telefono", phone_no);
				Log.e("password", pass);
				Log.e("facebook", fb_id);
				Log.e("sexo", sex);


				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);

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
		Toast.makeText(this, "User id: " + responceFromServer, Toast.LENGTH_LONG).show();
	}
	//===================================================================================================================================
	//processing the XML 
	//===================================================================================================================================


	//===================================================================================================================================
	//Validation
	//===================================================================================================================================

	//Email
	public String Is_Valid_Email(EditText edt) {
		String valid_email = "";
		if (edt.getText().toString().length() <= 0) {
			edt.setError("It's mandatory.");
			valid_email = "";
		} else if (android.util.Patterns.EMAIL_ADDRESS.matcher(edt.getText().toString()).matches() == false) {
			edt.setError("Invalid Email Address");
			valid_email = "";
		} else {
			valid_email = edt.getText().toString();
		}
		return valid_email;
	}

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

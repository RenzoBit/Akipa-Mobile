package com.akipa.akipadelivery;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.akipa.akipadelivery.R;
import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class MainActivity extends Activity{

	// Your Facebook APP ID
	private static String APP_ID = "657472391020033"; 

	// Instance of Facebook Class
	private Facebook facebook = new Facebook(APP_ID);
	private AsyncFacebookRunner mAsyncRunner;
	String FILENAME = "AndroidSSO_data";
	private SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
	}

	public void fetchingFbData(View v) 
	{
		v.findViewById(R.id.fbButton).startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.image_click));

		mAsyncRunner = new AsyncFacebookRunner(facebook);
		
		Session session = Session.getActiveSession(); 

		if(session!=null && session.isOpened())
		{    
		    Toast.makeText(this, "You already registered using Facebook !!\nPlease try to login.", Toast.LENGTH_LONG).show();
		}
		else
		{
			loginToFacebook();
		}
	}
	
	public void goingToRegister(View v) 
	{
		v.findViewById(R.id.buttonRegister).startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.image_click));

		Intent i = new Intent(MainActivity.this, RegistrationActivity.class);
		finish();
		startActivity(i);
	}
	
	public void loginNow(View v) 
	{
		Intent i = new Intent(this,LoginActivity.class);
		startActivity(i);
		finish();
	}
	
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// FaceBook
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public void printHashKey() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.akipa.akypadelivery",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("TEMPTAGHASH KEY:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }
	
	public boolean isLoggedIn() 
	{
	    Session session = Session.getActiveSession();
	    return (session != null && session.isOpened());
	}
	
	@SuppressWarnings("deprecation")
	private void loginToFacebook() {

		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);

		if (access_token != null) {
			facebook.setAccessToken(access_token);
			Log.d("FB Sessions", "" + facebook.isSessionValid());
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(this,
					new String[] { "email", "publish_stream" },
					new DialogListener() {

				@Override
				public void onCancel() {
					// Function to handle cancel event
				}

				@Override
				public void onComplete(Bundle values) {
					// Function to handle complete event
					// Edit Preferences and update facebook acess_token
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token",
							facebook.getAccessToken());
					editor.putLong("access_expires",
							facebook.getAccessExpires());
					editor.commit();
					getProfileInformation();
				}

				@Override
				public void onError(DialogError error) {
					// Function to handle error

				}

				@Override
				public void onFacebookError(FacebookError fberror) {
					// Function to handle Facebook errors

				}
			});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}


	/**
	 * Get Profile information by making request to Facebook Graph API
	 * */
	@SuppressWarnings("deprecation")
	public void getProfileInformation() 
	{
		mAsyncRunner.request("me", new RequestListener() 
		{
			@Override
			public void onComplete(String response, Object state) 
			{
				Log.d("Profile", response);
				String json = response;
				try 
				{
					
					/*{"id":"768449386576115","email":"mrbumba.jana1\u0040gmail.com","first_name":"Bumba",
					 * "gender":"male","last_name":"Rock","link":"https:\/\/www.facebook.com\/app_scoped_user_id\/768449386576115\/",
					 * "locale":"en_US","name":"Bumba Rock","timezone":5.5,"updated_time":"2014-11-21T02:36:21+0000","verified":true}
					 */
					String email = "";
					JSONObject profile = new JSONObject(json);
					final String first_name = profile.getString("first_name");
					final String last_name = profile.getString("last_name");
					try
					{
						email = profile.getString("email");
					}
					catch(Throwable t)
					{
						Toast.makeText(MainActivity.this, "No email address found !!", Toast.LENGTH_SHORT).show();
					}
					final String gender = profile.getString("gender");
					final String id = profile.getString("id");
					
					Intent i = new Intent(MainActivity.this, RegistrationActivity.class);
					i.putExtra("first_name", first_name);
					i.putExtra("last_name", last_name);
					i.putExtra("email", email);
					i.putExtra("sex", gender);
					i.putExtra("fb_id", email);
					
					finish();
					startActivity(i);
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}
	
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// FaceBook For
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

}

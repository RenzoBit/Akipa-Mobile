package com.akipa.listview.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;


public class ReusableClass 
{
	public static String base_url = "http://www.renzovilela.tk/akipa/rest/";
	public static String asset_url = "http://www.renzovilela.tk/akipa/assets/images/platos/";
	
	public static boolean isConnectingToInternet(Activity activity)
	{
		ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) 
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) 
				for (int i = 0; i < info.length; i++) 
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}

		}
		return false;
	}
	
	//===================================================================================================================================
	//Preference variable
	//===================================================================================================================================

	//--------------------------------------------
	// method to save variable in preference
	//--------------------------------------------
	public static void saveInPreference(String name, String content, Activity myActivity) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(myActivity);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(name, content);
		editor.commit();
	}

	//--------------------------------------------
	// getting content from preferences
	//--------------------------------------------
	public static String getFromPreference(String variable_name, Activity myActivity) {
		String preference_return;
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(myActivity);
		preference_return = preferences.getString(variable_name, "");

		return preference_return;
	}

	public static Typeface getFontStyle(Context c) {
		return Typeface.createFromAsset(c.getAssets(),"fonts/theinhardtthin-webfont-webfont.ttf");
	}


	//===================================================================================================================================
	//Preference variable
	//===================================================================================================================================

}

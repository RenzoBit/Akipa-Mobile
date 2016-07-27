package com.akipa.akipadelivery;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.akipa.akipadelivery.R;
import com.akipa.listview.utils.ReusableClass;
import com.akipa.navigation.DashBoardActivity;


public class SpashScreen extends Activity {

	LinearLayout logo_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.spash_screen);
		
		Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
		logo_layout = (LinearLayout)findViewById(R.id.linearLayoutLogoLayout);

		logo_layout.setAnimation(slideUp);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() 
			{
				if (ReusableClass.getFromPreference("isLogedIn", SpashScreen.this).equalsIgnoreCase("yes")) 
				{
					Intent i = new Intent(SpashScreen.this, DashBoardActivity.class);
					finish();
					startActivity(i);
					overridePendingTransition(R.anim.fadein,R.anim.fadeout);
				}
				else
				{
					Intent myIntent = new Intent(SpashScreen.this, MainActivity.class);
					finish();
					startActivity(myIntent);
					overridePendingTransition(R.anim.fadein,R.anim.fadeout);
				}
			}
		}, 3500);
	}
}

/* SplashActivity.java
 * Electric Sheep - K.Hall, C.Munoz, A.Reaves
 * Splash page for Panama City Portal application
 */

package com.android.projecte.townportal;

import java.util.Timer;
import java.util.TimerTask;

import com.android.projecte.townportal.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {
	private long splashDelay = 5000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				finish();
				Intent mainIntent = new Intent(SplashActivity.this,
						MainActivity.class);
				startActivity(mainIntent);
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, splashDelay);
	}

}

package tic_tac_toe.com.gamettt;

import android.app.Application;

public class BaseApplication extends Application {
	public static BaseApplication instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}
}

package com.cgy.lockscreen;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.cgy.lockscreen.LockScreenView.OnUnLockScreenListener;

public class MainActivity extends Activity {

	private LockScreenView mLockScreenView;
	private boolean isFirst = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLockScreenView = (LockScreenView) findViewById(R.id.lock_screenview);
	
		mLockScreenView.setOnUnLockScreenListener(new OnUnLockScreenListener() {
			
			@Override
			public void onSuccess() {
				Toast.makeText(MainActivity.this, "unlock success", Toast.LENGTH_SHORT).show();
				
			}
			
			@Override
			public void onFinish(List<Integer> list) {
				if(isFirst){
					mLockScreenView.setLockedList(list);
					isFirst = false;
				}
			}
			
			@Override
			public void onFail() {
				Toast.makeText(MainActivity.this, "unlock fail", Toast.LENGTH_SHORT).show();
			}
		});
	}
}

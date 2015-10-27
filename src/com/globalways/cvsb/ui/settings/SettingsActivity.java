package com.globalways.cvsb.ui.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.globalways.cvsb.R;
import com.globalways.cvsb.ui.BaseFragmentActivity;

/**
 * 系统设置
 * 
 * @author james
 *
 */
public class SettingsActivity extends BaseFragmentActivity implements OnClickListener, OnCheckedChangeListener {

	private TextView textCenter;
	private ImageButton imgBtnLeft;
	//radio
	private RadioButton radioUpdate,radioDevices, radioFeedBack, radioAbout;
	//fragments
	private static final int FRAGMENT_UPDATE = 0;
	private static final int FRAGMENT_DEVICES = 1;
	private static final int FRAGMENT_FEEDBACK = 2;
	private static final int FRAGMENT_ABOUT = 3;
	private int current = FRAGMENT_DEVICES;
	private Fragment[] fragmentArray;
	private UpdateFragment mUpdateFragment;
	private DevicesFragment mDevicesFragment;
	private FeedBackFragment mFeedBackFragment;
	private AboutFragment mAboutFragment;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.settings_activity);
		findViewById(R.id.view).setFitsSystemWindows(true);
		initView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBtnLeft:
			finish();
			break;
		}
	}

	/** 初始化UI、设置监听 */
	private void initView() {
		imgBtnLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
		imgBtnLeft.setOnClickListener(this);
		imgBtnLeft.setVisibility(View.VISIBLE);

		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("系统设置");
		
		//radio buttons
		radioUpdate = (RadioButton) findViewById(R.id.radioAppUpdate);
		radioUpdate.setOnCheckedChangeListener(this);
		radioDevices = (RadioButton) findViewById(R.id.radioDevices);
		radioDevices.setOnCheckedChangeListener(this);
		radioFeedBack = (RadioButton) findViewById(R.id.radioFeedback);
		radioFeedBack.setOnCheckedChangeListener(this);
		radioAbout =  (RadioButton) findViewById(R.id.radioAbout);
		radioAbout.setOnCheckedChangeListener(this);
		
		//fragments
		mUpdateFragment = new UpdateFragment();
		mDevicesFragment = new DevicesFragment();
		mFeedBackFragment = new FeedBackFragment();
		mAboutFragment = new AboutFragment();
		fragmentArray = new Fragment[] { mUpdateFragment, mDevicesFragment, mFeedBackFragment, mAboutFragment };
		switchToFragment(FRAGMENT_UPDATE);
	}
	
	/**
	 * switch fragment
	 * @param targetFragment
	 */
	private void switchToFragment(int targetFragment){
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.hide(fragmentArray[current]);
		if( !fragmentArray[targetFragment].isAdded() ){
			ft.add(R.id.viewContainer, fragmentArray[targetFragment]);
		}
		ft.show(fragmentArray[targetFragment]).commit();
		current = targetFragment;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.radioAppUpdate:
			if(isChecked && current != FRAGMENT_UPDATE){
				switchToFragment(FRAGMENT_UPDATE);
				textCenter.setText("");
			}
			break;
		case R.id.radioDevices:
			if(isChecked && current != FRAGMENT_DEVICES ){
				switchToFragment(FRAGMENT_DEVICES);
				textCenter.setText("外接设备");
			}
			break;
		case R.id.radioFeedback:
			if(isChecked && current != FRAGMENT_FEEDBACK){
				switchToFragment(FRAGMENT_FEEDBACK);
				textCenter.setText("在线客服");
			}
			break;
		case R.id.radioAbout:
			if(isChecked && current != FRAGMENT_ABOUT){
				switchToFragment(FRAGMENT_ABOUT);
				textCenter.setText("");
			}
			break;
		default:
			break;
		}
		
	}
}

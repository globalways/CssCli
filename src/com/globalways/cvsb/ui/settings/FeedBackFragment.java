package com.globalways.cvsb.ui.settings;

import java.util.Map;

import com.globalways.cvsb.R;
import com.globalways.cvsb.Config;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.ui.BaseFragment;
import com.mechat.mechatlibrary.MCClient;
import com.mechat.mechatlibrary.MCOnlineConfig;
import com.mechat.mechatlibrary.MCUserConfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FeedBackFragment extends BaseFragment implements OnClickListener {
	private View fragmentView;
	private Button btnQA, btnAdvice, btnCooperation;

	// 美恰相关
	private MCOnlineConfig onlineConfig;
	private MCUserConfig mcUserConfig;
	private Map<String, String> userInfo;
	private Map<String, String> userInfoExtra;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (fragmentView == null) {
			fragmentView = inflater.inflate(R.layout.settings_feedback_fragment, container, false);
		}
		initView();
		
		userInfo = MyApplication.getmContext().getUserInfo();
		userInfoExtra = MyApplication.getmContext().getUserInfoExtra();
		mcUserConfig = new MCUserConfig();

		return fragmentView;
	}

	private void initView() {
		btnQA = (Button) fragmentView.findViewById(R.id.btnQA);
		btnQA.setOnClickListener(this);
		btnAdvice = (Button) fragmentView.findViewById(R.id.btnAdvice);
		btnAdvice.setOnClickListener(this);
		btnCooperation = (Button) fragmentView.findViewById(R.id.btnCooperation);
		btnCooperation.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnQA:
			// 设置用户上线参数
			onlineConfig = new MCOnlineConfig();
			onlineConfig.setChannel(Config.MEIQIA_PLATFORM_INNER_NAME); // 设置渠道
			onlineConfig.setSpecifyGroup(btnQA.getText().toString()); // 设置指定分组

			userInfoExtra.remove("工单号");
			userInfoExtra.put("咨询意向", btnQA.getText().toString());
			mcUserConfig.setUserInfo(getActivity(), userInfo, userInfoExtra, null);

			// 启动对话界面
			MCClient.getInstance().startMCConversationActivity(onlineConfig);
			break;
		case R.id.btnAdvice:
			// 设置用户上线参数
			onlineConfig = new MCOnlineConfig();
			onlineConfig.setChannel(Config.MEIQIA_PLATFORM_INNER_NAME); // 设置渠道
			onlineConfig.setSpecifyGroup(btnAdvice.getText().toString()); // 设置指定分组

			userInfoExtra.remove("工单号");
			userInfoExtra.put("咨询意向", btnAdvice.getText().toString());
			mcUserConfig.setUserInfo(getActivity(), userInfo, userInfoExtra, null);

			// 启动对话界面
			MCClient.getInstance().startMCConversationActivity(onlineConfig);
			break;
		case R.id.btnCooperation:
			// 设置用户上线参数
			onlineConfig = new MCOnlineConfig();
			onlineConfig.setChannel(Config.MEIQIA_PLATFORM_INNER_NAME); // 设置渠道
			onlineConfig.setSpecifyGroup(btnCooperation.getText().toString()); // 设置指定分组

			userInfoExtra.remove("工单号");
			userInfoExtra.put("咨询意向", btnCooperation.getText().toString());
			mcUserConfig.setUserInfo(getActivity(), userInfo, userInfoExtra, null);

			// 启动对话界面
			MCClient.getInstance().startMCConversationActivity(onlineConfig);
			break;
		default:
			break;
		}
	}

}

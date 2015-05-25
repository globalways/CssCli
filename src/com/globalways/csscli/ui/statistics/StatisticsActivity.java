package com.globalways.csscli.ui.statistics;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alertdialogpro.AlertDialogPro.Builder;
import com.globalways.csscli.R;
import com.globalways.csscli.entity.StoreEntity;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.http.manager.StatisticsManager;
import com.globalways.csscli.tools.MyApplication;
import com.globalways.csscli.ui.BaseFragmentActivity;
import com.globalways.csscli.view.CommonDialogManager;

/**
 * 统计
 * 
 * @author james
 *
 */
public class StatisticsActivity extends BaseFragmentActivity implements OnClickListener, OnCheckedChangeListener {

	private static final int CODE_SALL = 0;
	private static final int CODE_BUY = 1;
	private int currentItem = CODE_SALL;

	private Context context;
	private TextView textCenter;
	private ImageButton imgBtnLeft;
	private Button btnRight;
	private RadioButton radioSallStatistics, radioBuyStatistics;
	private StatisticsFragment buyFragment, sellFragment;
	private Fragment[] fragmentArray;
	
	public static ArrayList<Long> toStatStoreIds = new ArrayList<Long>();
	public static ArrayList<StoreEntity> allChainStores = new ArrayList<StoreEntity>();
	

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.statistics_activity);
		context = this;
		// 布局内容会从view以下开始
		findViewById(R.id.view).setFitsSystemWindows(true);
		toStatStoreIds.add(MyApplication.getStoreid());
		allChainStores.add(MyApplication.getCurrentEntity());
		initView();
		initFragment();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBtnLeft:
			finish();
			break;
	    // add by wyp
		case R.id.btnRight:
			showSubStoresDialog();
			break;
		// end
		}
	}

	/** 初始化UI、设置监听 */
	private void initView() {
		imgBtnLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
		imgBtnLeft.setOnClickListener(this);
		imgBtnLeft.setVisibility(View.VISIBLE);
		

		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("统计");
		
		// add by wyp
		btnRight = (Button) findViewById(R.id.btnRight);
		btnRight.setOnClickListener(this);
		btnRight.setVisibility(View.VISIBLE);
		btnRight.setPadding(0, 0, 20, 0);
		btnRight.setText("统计多个店铺");
		// end

		radioSallStatistics = (RadioButton) findViewById(R.id.radioSallStatistics);
		radioSallStatistics.setOnCheckedChangeListener(this);
		radioBuyStatistics = (RadioButton) findViewById(R.id.radioBuyStatistics);
		radioBuyStatistics.setOnCheckedChangeListener(this);
	}

	/**
	 * 初始化Fragment，默认显示销售内容
	 */
	private void initFragment() {
		sellFragment = new StatisticsFragment();
		Bundle argsSell = new Bundle();
		argsSell.putInt(StatisticsFragment.KEY_STAT_TYPE, StatisticsFragment.StatType.TYPE_SELL);
		sellFragment.setArguments(argsSell);

		buyFragment = new StatisticsFragment();
		Bundle argsBuy = new Bundle();
		argsBuy.putInt(StatisticsFragment.KEY_STAT_TYPE, StatisticsFragment.StatType.TYPE_BUY);
		buyFragment.setArguments(argsBuy);

		fragmentArray = new Fragment[] { sellFragment, buyFragment };
		radioSallStatistics.setChecked(true);
		getSupportFragmentManager().beginTransaction().add(R.id.viewContainer, sellFragment).show(sellFragment)
				.commit();

		textCenter.setText("销售统计");
	}

	private void switchFragment(int targetFragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.hide(fragmentArray[currentItem]);
		if (!fragmentArray[targetFragment].isAdded()) {
			transaction.add(R.id.viewContainer, fragmentArray[targetFragment]);
		}
		transaction.show(fragmentArray[targetFragment]);
		transaction.commit();
		currentItem = targetFragment;
	}
	
	
	/**
	 * show sub-stores to select
	 * @author wyp
	 */
	private void showSubStoresDialog()
	{
	
		StatisticsManager.getInstance().getSubStores(String.valueOf(MyApplication.getStoreid()), new ManagerCallBack<List<StoreEntity>>() {

			@Override
			public void onSuccess(final List<StoreEntity> returnContent) {
				super.onSuccess(returnContent);
				
				if(allChainStores.size()-1 != returnContent.size())
				{
					allChainStores.clear();
					allChainStores.add(MyApplication.getCurrentEntity());
					allChainStores.addAll(returnContent);
				}
				
				String[] items = new String[allChainStores.size()];
				boolean[] selected = new boolean[allChainStores.size()];
				
				for(int i=0;i<allChainStores.size();i++)
				{
					items[i] = allChainStores.get(i).getStore_name();
					if(toStatStoreIds.contains(allChainStores.get(i).getStore_id()))
					{
						selected[i] = true;
					}
				}
				
				
				Builder builder = CommonDialogManager.createDialogBuilder(context);
				
//				commonBuilder.
				
				// new & show dialog
				//new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT)
				builder
				.setTitle("选择多个店铺")
				.setMultiChoiceItems(items,selected, new DialogInterface.OnMultiChoiceClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						if(isChecked && !toStatStoreIds.contains(allChainStores.get(which).getStore_id()))
						{
							toStatStoreIds.add(allChainStores.get(which).getStore_id());
						}else{
							toStatStoreIds.remove(allChainStores.get(which).getStore_id());
						}
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(toStatStoreIds.size() == 0)
						{
							Toast.makeText(context, "请至少选择一个店铺", Toast.LENGTH_SHORT).show();
						}else{
							((StatisticsFragment)fragmentArray[currentItem]).onStoreIDChanged();
							dialog.dismiss();
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.show();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
			
		});
		
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.radioSallStatistics:
			if (isChecked && currentItem != CODE_SALL) {
				switchFragment(CODE_SALL);
				textCenter.setText("销售统计");
			}
			break;
		case R.id.radioBuyStatistics:
			if (isChecked && currentItem != CODE_BUY) {
				switchFragment(CODE_BUY);
				textCenter.setText("采购统计");
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		allChainStores.clear();
		toStatStoreIds.clear();
	}
}

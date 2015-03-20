package com.globalways.csscli.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.globalways.csscli.R;
import com.globalways.csscli.ui.BaseActivity;
import com.globalways.csscli.ui.cashier.CashierActivity;
import com.globalways.csscli.ui.employee.EmployeeActivity;
import com.globalways.csscli.ui.gallery.GalleryActivity;
import com.globalways.csscli.ui.order.OrderActivity;
import com.globalways.csscli.ui.product.ProductActivity;
import com.globalways.csscli.ui.purchase.PurchaseActivity;
import com.globalways.csscli.ui.settings.SettingsActivity;
import com.globalways.csscli.ui.statistics.StatisticsActivity;
import com.globalways.csscli.ui.store.StoreActivity;

public class MainActivity extends BaseActivity implements OnItemClickListener {

	private GridView gridView;
	private MainButtonAdapter mainBtnAdapter;
	private Class<?>[] mainClass = { StoreActivity.class, ProductActivity.class, EmployeeActivity.class,
			CashierActivity.class, PurchaseActivity.class, OrderActivity.class, StatisticsActivity.class,
			SettingsActivity.class };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		initView();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, GalleryActivity.class);
		intent.putExtra("purpose", GalleryActivity.Purpose.MULTI_PIC);
		startActivity(intent);
		// switch (position) {
		// case 1:
		// case 3:
		// Intent intent = new Intent(this, mainClass[position]);
		// startActivity(intent);
		// break;
		// default:
		// Toast.makeText(this, "正在开发，敬请期待……", Toast.LENGTH_SHORT).show();
		// break;
		// }
	}

	private void initView() {
		gridView = (GridView) findViewById(R.id.gridView);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mainBtnAdapter = new MainButtonAdapter(this);
		gridView.setAdapter(mainBtnAdapter);
		mainBtnAdapter.setData(getResources().getStringArray(R.array.mainViewButton));
		gridView.setOnItemClickListener(this);
	}

}

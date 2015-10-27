package com.globalways.cvsb.ui.statistics;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.ProductEntity;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.http.manager.ProductManager;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.ui.BaseFragment;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.ui.product.ProductListAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

/**
 * 商品对比前筛选商品
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年6月26日 下午2:26:07
 */
public class ProductFilterFragment extends BaseFragment implements OnRefreshListener<ListView>, OnItemClickListener, OnClickListener {
	
	private int selectedNum;
	private View fragmentView;
	private PullToRefreshListView refreshListView;
	private ListView lvProductList, lvProductSelectedList;
	private ProductListAdapter mProductListAdapter;
	private ProuductFilterSelectedAdapter mSelectedAdapter;
	private TextView tvSelectedCount;
	private Button btnToCompare;
	ProductCompareFragment mProductCompareFragment;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(null == fragmentView)
		{
			fragmentView = inflater.inflate(R.layout.statistics_product_filter_fragment, container, false);
		}
		initView();
		initData();
		return fragmentView;
	}
	private void initView(){
		//all product list
		refreshListView = (PullToRefreshListView) fragmentView.findViewById(R.id.refreshListView);
		refreshListView.setOnRefreshListener(this);
		refreshListView.setMode(Mode.BOTH);
		lvProductList = refreshListView.getRefreshableView();
		mProductListAdapter = new ProductListAdapter(getActivity());
		lvProductList.setAdapter(mProductListAdapter);
		lvProductList.setOnItemClickListener(this);
		//selected product list
		lvProductSelectedList = (ListView) fragmentView.findViewById(R.id.lvSelectedProductList);
		mSelectedAdapter = new ProuductFilterSelectedAdapter(getActivity());
		lvProductSelectedList.setAdapter(mSelectedAdapter);
		//views
		tvSelectedCount = (TextView) fragmentView.findViewById(R.id.tvSelectedCount);
		btnToCompare = (Button) fragmentView.findViewById(R.id.btnShowCompareFragment);
		btnToCompare.setOnClickListener(this);
	}
	
	private void initData(){
		loadProductList(true);
		lvProductSelectedList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSelectedAdapter.removeItem(position);
			}
		});
	}
	private void loadProductList(final boolean isReload){
		refreshListView.setRefreshing();
		ProductManager.getInstance().getProductList(MyApplication.getStoreid(), mProductListAdapter.getPage(isReload),
				new ManagerCallBack<List<ProductEntity>>() {
					@Override
					public void onSuccess(List<ProductEntity> returnContent) {
						super.onSuccess(returnContent);
						mProductListAdapter.setData(isReload, returnContent);
						refreshListView.onRefreshComplete();
					}
		
					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
						refreshListView.onRefreshComplete();
					}
		});
	}
	
	public void clearCompareFragment(){
		if(mProductCompareFragment != null){
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.hide(mProductCompareFragment);
			ft.remove(mProductCompareFragment);
			ft.commit();
		}
		
	}
	
	/**
	 * 启动对比fragment
	 */
	private void showCompareInfo()
	{
		if(mSelectedAdapter.getList() == null || mSelectedAdapter.getList().size() < 1){
			UITools.ToastMsg(getActivity(), getString(R.string.statistics_product_filter_notice));
			return;
		}
		StringBuilder qrs = new StringBuilder();
		for(int i=0;i<mSelectedAdapter.getList().size();i++)
		{
			qrs.append(mSelectedAdapter.getList().get(i).getProduct_qr()).append(",");
		}
		Bundle bundle = new Bundle();
		bundle.putString("product_qrs", qrs.toString().substring(0, qrs.length()-1));
		
		mProductCompareFragment = new ProductCompareFragment();
		mProductCompareFragment.setArguments(bundle);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		
		ft.add(R.id.viewContainer, mProductCompareFragment);
		ft.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
		ft.hide(this);
		ft.show(mProductCompareFragment);
		ft.commit();
	}
	
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		loadProductList(refreshListView.isHeaderShown());
		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(mSelectedAdapter.addData(mProductListAdapter.getItemByPosition(position-1)))
		{
			selectedNum++;
			tvSelectedCount.setText("选择了 "+String.valueOf(selectedNum) + " 个商品");
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnShowCompareFragment:
			showCompareInfo();
			break;
		default:
			break;
		}
		
	}
}

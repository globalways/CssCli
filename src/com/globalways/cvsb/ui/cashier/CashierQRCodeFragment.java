package com.globalways.cvsb.ui.cashier;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alertdialogpro.AlertDialogPro;
import com.globalways.cvsb.R;
import com.globalways.cvsb.android.AmbientLightManager;
import com.globalways.cvsb.android.BeepManager;
import com.globalways.cvsb.android.CaptureActivityHandler;
import com.globalways.cvsb.android.FinishListener;
import com.globalways.cvsb.android.InactivityTimer;
import com.globalways.cvsb.android.IntentSource;
import com.globalways.cvsb.android.PreferencesActivity;
import com.globalways.cvsb.android.ScanCodeInterface;
import com.globalways.cvsb.android.ViewfinderView;
import com.globalways.cvsb.android.camera.CameraManager;
import com.globalways.cvsb.entity.ProductEntity;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.http.manager.ManagerCallBack2;
import com.globalways.cvsb.http.manager.ProductManager;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.tools.MyLog;
import com.globalways.cvsb.tools.PicassoImageLoader;
import com.globalways.cvsb.tools.Tool;
import com.globalways.cvsb.ui.BaseFragment;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.ui.product.ProductSelectionActivity;
import com.globalways.cvsb.ui.product.ProductType;
import com.globalways.cvsb.view.ClearableEditText;
import com.globalways.cvsb.view.CustomKeyboard;
import com.globalways.cvsb.view.DialogCallback;
import com.globalways.cvsb.view.MyDialogManager;
import com.globalways.cvsb.view.TextWatcherDelay;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

/**
 * @author James
 *
 */
public class CashierQRCodeFragment extends BaseFragment implements OnClickListener, SurfaceHolder.Callback,
		ScanCodeInterface, OnItemClickListener {
	private static final String TAG = CashierQRCodeFragment.class.getSimpleName();
	private ProductEntity productEntity;

	private View layoutView;
	private ImageView imageProductAva;
	private TextView textProductName, textProductPrice, textNumber;
	private Button btnLess, btnAdd, btnAddCashier, btnOrder, btnRefreshCamera,btnShowHelpCode, btnManualSelect;
	private EditText etBarCode;
	private CustomKeyboard mCustomKeyboard;
	private ListView lvCodeResult;
	private SearchResultAdapter mResultAdapter;
	private RelativeLayout rlResultList;
	
	// scanner variable
	private boolean isBlockCamera = false;
	private InactivityTimer inactivityTimer;
	private BeepManager beepManager;
	private AmbientLightManager ambientLightManager;
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType, ?> decodeHints;
	private String characterSet;
	private boolean hasSurface;
	private IntentSource source;
	private Result savedResultToShow;
	private FrameLayout cameraView;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null == layoutView) {
			layoutView = inflater.inflate(R.layout.cashier_qrcode_fragment, container, false);
		}
		// cameraView.setLayoutParams(new
		// LinearLayout.LayoutParams(cameraAreaWidth, cameraAreaHeight));

		cameraView = (FrameLayout) layoutView.findViewById(R.id.camera_view);
		Point screen = new Point();
		getActivity().getWindowManager().getDefaultDisplay().getRealSize(screen);
		double tmp = (double) screen.y / (double) screen.x;
		int cameraAreaWidth = cameraView.getLayoutParams().width;
		int cameraAreaHeight = (int) (cameraAreaWidth * tmp);
		cameraView.getLayoutParams().height = cameraAreaHeight;
		cameraView.getLayoutParams().width = cameraAreaWidth;
		return layoutView;
	}

	public void setTotalPrice(long totalPrice) {
		if (totalPrice > 0) {
			btnOrder.setText("商品总价：￥" + Tool.fenToYuan(totalPrice));
		}else if(totalPrice == 0){
			//清空了List里面的商品
			btnOrder.setText("去结算");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// scanner
		inactivityTimer = new InactivityTimer(this.getActivity());
		beepManager = new BeepManager(this.getActivity());
		ambientLightManager = new AmbientLightManager(this.getActivity());
		hasSurface = false;
		PreferenceManager.setDefaultValues(this.getActivity(), R.xml.preferences, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		refreshView();
	}

	/**
	 * 处理扫描获取到的数据
	 * 
	 * @param result
	 */
	public void handleResult(Result result) {
		MyLog.d(TAG, result.getText());
		addCashierProduct();
		loadProductDetail(result.getText());
		// 重置扫码
		restartPreviewAfterDelay(1000L);
	}

	/**
	 * 订单结束后调用
	 */
	public void orderFinish() {
		btnOrder.setText("去结算");
	}

	/**
	 * 扫码成功后调用这个方法，获取商品的详细信息
	 * 
	 * @param type
	 *            二维码还是条形码
	 * @param productCode
	 *            二维码或者条形码的内容
	 */
	private void loadProductDetail(String productCode) {
		ProductManager.getInstance().getProductDetail(MyApplication.getStoreid(), productCode,
				new ManagerCallBack<ProductEntity>() {
					@Override
					public void onSuccess(ProductEntity returnContent) {
						super.onSuccess(returnContent);
						productEntity = returnContent;
						productEntity.setShoppingNumber(1);
						refreshView();
					}

					@Override
					public void onFailure(int code, String msg) {
						super.onFailure(code, msg);
						UITools.ToastMsg(getActivity(), msg);
					}
				});
	}

	/**
	 * 刷新当前商品显示
	 */
	private void refreshView() {
		if (productEntity != null) {
			//未上架
			if(productEntity.getStatus() == 2){
				UITools.ToastMsg(getActivity(), "商品:【"+productEntity.getProduct_name()+"】未上架，暂时不能销售");
				return;
			}
			textNumber.setText("1");
			textNumber.setVisibility(View.VISIBLE);
			new PicassoImageLoader(getActivity()).showImage(productEntity.getProduct_avatar(), R.drawable.logo,
					R.drawable.logo, imageProductAva);
			textProductName.setText(productEntity.getProduct_name());
			textProductPrice.setText("￥ " + Tool.div(String.valueOf(productEntity.getProduct_retail_price()), "100", Tool.RETAIL_APR_SCALE) + " / "
					+ productEntity.getProduct_unit() + "  ");
		}
	}

	private void addCashierProduct() {
		if (productEntity != null && productEntity.getShoppingNumber() != 0) {
			textNumber.setText("");
			textNumber.setFocusable(false);
			((CashierActivity) getActivity()).addCashierProduct(productEntity);
			reset();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textNumber:
			if (productEntity != null) {
				showNumberDialog();
			}
			break;
		case R.id.btnLess:
			if (productEntity != null) {
				
				if(productEntity.isDanti()){
					String text1 = textNumber.getText().toString().trim();
					if (Integer.valueOf(textNumber.getText().toString().trim()) > 1) {
						productEntity.setShoppingNumber(Integer.valueOf(text1) - 1);
						//单体显示整数
						textNumber.setText(String.valueOf((int)productEntity.getShoppingNumber()));
					}
					btnLess.setEnabled(!textNumber.getText().toString().trim().equals("1"));
				}else{
					String text1 = textNumber.getText().toString().trim();
					if (Float.valueOf(textNumber.getText().toString().trim()) > 1) {
						productEntity.setShoppingNumber(Float.parseFloat(Tool.subtract(text1, "1")));
						textNumber.setText(String.valueOf(productEntity.getShoppingNumber()));
					}
					btnLess.setEnabled(!textNumber.getText().toString().trim().equals("1"));
				}
			}
			break;
		case R.id.btnAdd:
			if (productEntity != null) {
				if(productEntity.isDanti()){
					productEntity.setShoppingNumber(productEntity.getShoppingNumber() + 1);
					textNumber.setText(String.valueOf((int)productEntity.getShoppingNumber()));
					btnLess.setEnabled(!textNumber.getText().toString().trim().equals("1"));
				}else{
					String text1 = textNumber.getText().toString().trim();
					String textadded = Tool.add(text1, "1");
					productEntity.setShoppingNumber(Float.parseFloat(textadded));
					textNumber.setText(textadded);
					btnLess.setEnabled(!textNumber.getText().toString().trim().equals("1"));
				}
			}
			break;
		case R.id.btnAddCashier:
			addCashierProduct();
			break;
		case R.id.btnSingle:
			if (productEntity != null) {
				addCashierProduct();
			} else {
				if (((CashierActivity) getActivity()).getCount() > 0) {
					((CashierActivity) getActivity()).showSignDialog();
					stopScaner();
				}
			}
			break;
		case R.id.btnRefreshCamera:
			if(!isBlockCamera){
				stopScaner();
				startScaner();
			}
			break;
		case R.id.btnManualSelect:
			startActivityForResult(new Intent(getActivity(),ProductSelectionActivity.class), ProductSelectionActivity.SELECT_RPODUCT_REQUEST);
			break;
		case R.id.btnShowHelpCode:
			if(etBarCode.isShown()){
				etBarCode.setVisibility(View.INVISIBLE);
				btnShowHelpCode.setText("编码查找");
				lvCodeResult.setVisibility(View.INVISIBLE);
				rlResultList.setVisibility(View.INVISIBLE);
				startScaner();
			}
			else{
				etBarCode.setText("");
				etBarCode.setVisibility(View.VISIBLE);
				rlResultList.setVisibility(View.VISIBLE);
				btnShowHelpCode.setText("关闭输入");
				stopScaner();
			}
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == ProductSelectionActivity.SELECT_RPODUCT_REQUEST && resultCode == FragmentActivity.RESULT_OK){
			productEntity = (ProductEntity)data.getExtras().getSerializable(ProductSelectionActivity.DATA);
			productEntity.setShoppingNumber(1);
			refreshView();
		}
	}

	private void reset() {
		productEntity = null;
		textNumber.setText("1");
		textProductName.setText("");
		textProductPrice.setText("");
		imageProductAva.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.logo));
	}

	private void initView() {
		imageProductAva = (ImageView) layoutView.findViewById(R.id.imageProductAva);
		textProductName = (TextView) layoutView.findViewById(R.id.textProductName);
		textProductPrice = (TextView) layoutView.findViewById(R.id.textProductPrice);
		textNumber = (TextView) layoutView.findViewById(R.id.textNumber);
		textNumber.setOnClickListener(this);
		
		btnLess = (Button) layoutView.findViewById(R.id.btnLess);
		btnLess.setOnClickListener(this);
		btnAdd = (Button) layoutView.findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);
		btnAddCashier = (Button) layoutView.findViewById(R.id.btnAddCashier);
		btnAddCashier.setOnClickListener(this);
		btnOrder = (Button) layoutView.findViewById(R.id.btnSingle);
		btnOrder.setOnClickListener(this);
		btnRefreshCamera = (Button) layoutView.findViewById(R.id.btnRefreshCamera);
		btnRefreshCamera.setOnClickListener(this);
		
		etBarCode =  (EditText) layoutView.findViewById(R.id.etBarCode);
		
//      采用AutoCompleteText案例
//		String[] strs = {"621054","6524","986524",  
//		        "6243","Belarus","USA","China1","China2","USA1"};  
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line,strs);
//		etBarCode.setAdapter(adapter);
//		etBarCode.setThreshold(1);
		
		etBarCode.addTextChangedListener(new TextWatcherDelay(new Handler(){
			@Override
			public void handleMessage(Message msg) {
				showCodeSearchReult(msg.getData().getString(TextWatcherDelay.DATA));
			}
		}));
		mCustomKeyboard = new CustomKeyboard(getActivity(), R.id.viewCustomKeyboard, R.xml.number_keyboard_layout);
		mCustomKeyboard.registerEditText(R.id.etBarCode);
		
		lvCodeResult = (ListView) layoutView.findViewById(R.id.lvCodeResult);
		mResultAdapter = new SearchResultAdapter(getActivity());
		lvCodeResult.setAdapter(mResultAdapter);
		lvCodeResult.setOnItemClickListener(this);
		rlResultList = (RelativeLayout) layoutView.findViewById(R.id.rlResultList);
		
		btnShowHelpCode = (Button) layoutView.findViewById(R.id.btnShowHelpCode);
		btnShowHelpCode.setOnClickListener(this);
		btnManualSelect = (Button) layoutView.findViewById(R.id.btnManualSelect);
		btnManualSelect.setOnClickListener(this);
	}
	
	private void showCodeSearchReult(final String barcode){
		if(barcode.isEmpty() || barcode == null){
			mResultAdapter.setData(null,barcode);
			lvCodeResult.setVisibility(View.INVISIBLE);
		}
		ProductManager.getInstance().cashierSearch(barcode, new ManagerCallBack2<List<ProductEntity>, Integer>() {
			@Override
			public void onSuccess(List<ProductEntity> returnContent, Integer params) {
				super.onSuccess(returnContent, params);
				if(returnContent.size() == 0){
					lvCodeResult.setVisibility(View.INVISIBLE);
				}else{
					mResultAdapter.setData(returnContent,barcode);
					lvCodeResult.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				mResultAdapter.setData(null,barcode);
				lvCodeResult.setVisibility(View.INVISIBLE);
				if(msg != null)
					UITools.ToastMsg(getActivity(), msg);
			}
		});
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ProductEntity e = (ProductEntity) mResultAdapter.getItem(position);
		try {
			//防止收银列表对象重复
			productEntity = (ProductEntity) e.clone();
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();
		}
		productEntity.setShoppingNumber(1);
		refreshView();
	}
	

	/** 弹出输入数量的对话框 */
	@SuppressLint("InflateParams")
	private void showNumberDialog() {
		if(productEntity.isDanti()){
			//单体型 数量为整数
			MyDialogManager.showNumEditInteger(getActivity(),textNumber.getText(),new DialogCallback<Integer>() {
				
				@Override
				public void onValueSet(Integer value) {
					if (productEntity != null) {
						productEntity.setShoppingNumber(value);
					}
					textNumber.setText(String.valueOf(value));
				}
			});
		}else{
			MyDialogManager.showNumEditFloat(getActivity(),textNumber.getText(),new DialogCallback<Float>() {
				@Override
				public void onValueSet(Float value) {
					if (productEntity != null) {
						productEntity.setShoppingNumber(value);
					}
					textNumber.setText(String.valueOf(value));
				}
			});
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.
		startScaner();
	}

	/*
	 * ****************************************
	 * scanner function
	 * 
	 * ****************************************
	 */
	
	private void startScaner(){
		cameraManager = new CameraManager(this.getActivity().getApplication());

		if(viewfinderView != null){
			viewfinderView.setVisibility(View.VISIBLE);
		}else{
			viewfinderView = (ViewfinderView) this.getActivity().findViewById(R.id.viewfinder_view);
		}
		
		viewfinderView.setCameraManager(cameraManager);

		SurfaceView surfaceView = (SurfaceView) this.getActivity().findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
		}
		beepManager.updatePrefs();
		ambientLightManager.start(cameraManager);
		inactivityTimer.onResume();
		source = IntentSource.NONE;
		decodeFormats = null;
		characterSet = null;
		isBlockCamera = false;
	}
	
	private void stopScaner(){
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		ambientLightManager.stop();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) this.getActivity().findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		//不显示透明层,解决透明层闪烁问题
		viewfinderView.setVisibility(View.INVISIBLE);
		//禁用【刷新】按钮
		isBlockCamera = true;
	}
	
	/**
	 * 
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			// Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			// "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 *
	 * @param rawResult
	 *            The contents of the barcode.
	 * @param scaleFactor
	 *            amount by which thumbnail was scaled
	 * @param barcode
	 *            A greyscale bitmap of the camera data which was decoded.
	 */
	@Override
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		inactivityTimer.onActivity();
		// ResultHandler resultHandler =
		// ResultHandlerFactory.makeResultHandler(this, rawResult);

		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {
			// historyManager.addHistoryItem(rawResult, resultHandler);
			// Then not from history, so beep/vibrate and we have an image to
			// draw on
			beepManager.playBeepSoundAndVibrate();
			// drawResultPoints(barcode, scaleFactor, rawResult);
		}

		switch (source) {
		/*
		 * case NATIVE_APP_INTENT: case PRODUCT_SEARCH_LINK:
		 * handleDecodeExternally(rawResult, resultHandler, barcode); break;
		 * case ZXING_LINK: if (scanFromWebPageManager == null ||
		 * !scanFromWebPageManager.isScanFromWebPage()) {
		 * handleDecodeInternally(rawResult, resultHandler, barcode); } else {
		 * handleDecodeExternally(rawResult, resultHandler, barcode); } break;
		 */
		case NONE:
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
			if (fromLiveScan && prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
				/*
				 * Toast.makeText(getApplicationContext(),
				 * getResources().getString(R.string.msg_bulk_mode_scanned) +
				 * " (" + rawResult.getText() + ')', Toast.LENGTH_SHORT).show();
				 */
				// Wait a moment or else it will scan the same barcode
				// continuously about 3 times
				// restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
			} else {
				// change by yangping.wang
				// handleDecodeInternally(rawResult, resultHandler, barcode);
				// handleResultTest(rawResult, resultHandler, barcode);
				handleResult(rawResult);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	@Override
	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	@Override
	public CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public Handler getHandler() {
		return handler;
	}

	@Override
	public Context getContext() {
		return getActivity();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	private void resetStatusView() {
		viewfinderView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onPause() {
		stopScaner();
		super.onPause();
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton("OK", new FinishListener(this.getActivity()));
		builder.setOnCancelListener(new FinishListener(this.getActivity()));
		builder.show();
	}

}

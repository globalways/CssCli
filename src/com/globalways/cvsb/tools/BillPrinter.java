package com.globalways.cvsb.tools;

import java.util.List;

import com.alertdialogpro.AlertDialogPro.Builder;
import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.OrderEntity;
import com.globalways.cvsb.entity.ProductEntity;
import com.globalways.cvsb.tools.PrinterBase.PrinterStatus;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.view.MyDialogManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Paint;
import android.widget.Toast;
import zpSDK.zpSDK.zpSDK;
import zpSDK.zpSDK.zpSDK.BARCODE2D_TYPE;

/**
 * 票据打印机
 * @author wyp
 *
 */
public class BillPrinter {
	private Context context;
	private PrinterBase pb;
	private Paint paint;
	private int currentY = 0;
	private static final int TIME_OUT = 3000;
	public BillPrinter(Context context) {
		this.context = context;
		this.pb = new PrinterBase(context);
		this.paint = new Paint();
	}
	public boolean OpenPrinter(String BDAddress) {
		if (BDAddress == "" || BDAddress == null) {
			Toast.makeText(context, "没有选择打印机", Toast.LENGTH_LONG).show();
			return false;
		}
		BluetoothDevice myDevice;
		BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (myBluetoothAdapter == null) {
			Toast.makeText(context, "读取蓝牙设备错误", Toast.LENGTH_LONG).show();
			return false;
		}
		myDevice = myBluetoothAdapter.getRemoteDevice(BDAddress);
		if (myDevice == null) {
			Toast.makeText(context, "读取蓝牙设备错误", Toast.LENGTH_LONG).show();
			return false;
		}
		if (zpSDK.zp_open(myBluetoothAdapter, myDevice) == false) {
			notice(PrinterStatus.UNKNOWN);
			return false;
		}
		return true;
	}
	
	
	@SuppressWarnings("static-access")
	public void testPrint() {
		if(PrinterBase.pairedBillPrinter == null){
			pb.scanAndPair();
			return;
		}
		if(!pb.connect(PrinterBase.pairedBillPrinter)){
			return;
		}
		if (!OpenPrinter(PrinterBase.pairedBillPrinter.getAddress())) {
			return;
		}
		if (!zpSDK.zp_page_create(58, 45)) {
			Toast.makeText(context, "创建打印页面失败", Toast.LENGTH_LONG).show();
			return;
		}
		currentY = 10;
		zpSDK.zp_draw_text(6, currentY, "打印测试");
		zpSDK.zp_page_print(false);
		zpSDK.zp_printer_status_detect();
		// zpSDK.zp_goto_mark_right(150);
		if (zpSDK.zp_printer_status_get(8000) != 0) {
			Toast.makeText(context, zpSDK.ErrorMessage, Toast.LENGTH_LONG).show();
		}
		zpSDK.zp_page_free();
		zpSDK.zp_close();
	}
	
	/**收银打印
	 * @param order 订单
	 * @param products 商品列表
	 */
	public void printCashier(OrderEntity order, List<ProductEntity> products) {
		PrinterBase.pairedBillPrinter = pb.findPairedPrinter(context);
		if(PrinterBase.pairedBillPrinter == null){
			UITools.ToastMsg(context, context.getString(R.string.cashier_noitce_no_printer));
			return;
		}
		if(order==null || products==null){
			return;
		}
		int produts_size = products.size()*10;
		int base_size = 55;
		if (!OpenPrinter(PrinterBase.pairedBillPrinter.getAddress())) {
			return;
		}
		if (!zpSDK.zp_page_create(58, base_size+produts_size)) {
			UITools.ToastMsg(context, "创建打印页面失败");
			return;
		}
		currentY = 10;
		zpSDK.zp_draw_text(6, currentY, order.getStore_name()==null? "环途便利店" : order.getStore_name());
		zpSDK.zp_draw_text_ex(0, currentY=currentY+4, "收银 : 00001", null, 2.4, 0, false, false, false);
		zpSDK.zp_draw_text_ex(17.7, currentY, "时间 :  "+Tool.formateDateTimeByFormat(order.getOrder_time()*1000, "yyyy-MM-dd  HH:mm:ss"), null, 2.4, 0, false, false, false);
		zpSDK.zp_draw_text_ex(0, currentY=currentY+3, "总价:￥"+Tool.fenToYuan(order.getOrder_amount()), null, 2.4, 0, false, false, false);
		zpSDK.zp_draw_text_ex(17.7, currentY, "订单号:"+order.getOrder_id(),null,2.4,0,false,false,false);
		zpSDK.zp_draw_text_ex(0, currentY=currentY+4, "-----------------------------------------------------------",null,3,0,false,false,false);
		zpSDK.zp_draw_text_ex(0, currentY=currentY+4, "品名             零售价         数量     金额",null,3,0,false,false,false);
		currentY=currentY+4;
		long total = 0;
		for(ProductEntity p : products){
			total += (long) (p.getShoppingNumber()*p.getProduct_retail_price());
			zpSDK.zp_draw_text_ex(16, currentY, Tool.fenToYuan(p.getProduct_retail_price())+"/"+p.getProduct_unit(),null,2.4,0,false,false,false);
			zpSDK.zp_draw_text_ex(32, currentY, p.getShoppingNumber()+"",null,2.4,0,false,false,false);
			zpSDK.zp_draw_text_ex(40, currentY, Tool.fenToYuan((long) (p.getShoppingNumber()*p.getProduct_retail_price())),null,2.4,0,false,false,false);
			draw_text_pinming(p.getProduct_name(), currentY);
		}
		zpSDK.zp_draw_text_ex(0, currentY, "-----------------------------------------------------------",null,3,0,false,false,false);
		zpSDK.zp_draw_text_ex(0, currentY=currentY+4, "合计: ￥"+Tool.fenToYuan(total), null, 2.4, 0, false, false, false);
		zpSDK.zp_draw_text_ex(0, currentY=currentY+4, "优惠: ￥"+Tool.fenToYuan(order.getDiscount_amount()), null, 2.4, 0, false, false, false);
		zpSDK.zp_draw_text_ex(0, currentY=currentY+4, "实收: ￥"+Tool.fenToYuan(order.getOrder_amount()), null, 2.4, 0, false, false, false);
		zpSDK.zp_draw_text_ex(0, currentY=currentY+4, "欢迎再次光临", null, 4, 0, false, false, false);
		
		zpSDK.zp_page_print(false);
		zpSDK.zp_printer_status_detect();
		// zpSDK.zp_goto_mark_right(150);
		notice(PrinterStatus.codeOf(zpSDK.zp_printer_status_get(TIME_OUT)));
		zpSDK.zp_page_free();
		zpSDK.zp_close();
	}
	
	/**
	 * 纸卷走到标签开头,再打印
	 */
	public void gotoLabel(){
		PrinterBase.pairedBillPrinter = pb.findPairedPrinter(context);
		if(PrinterBase.pairedBillPrinter == null){
			UITools.ToastMsg(context, context.getString(R.string.cashier_noitce_no_printer));
			return;
		}
		if(!OpenPrinter(PrinterBase.pairedBillPrinter.getAddress()))return;
		zpSDK.zp_goto_mark_label(150);
		zpSDK.zp_close();
	}
	
	/**打印商品标签
	 * @param order 订单
	 * @param e 商品列表
	 */
	public void printLabel(ProductEntity e) {
		PrinterBase.pairedBillPrinter = pb.findPairedPrinter(context);
		if(PrinterBase.pairedBillPrinter == null){
			UITools.ToastMsg(context, context.getString(R.string.cashier_noitce_no_printer));
			return;
		}
		if(e==null || e==null){
			return;
		}
		if (!OpenPrinter(PrinterBase.pairedBillPrinter.getAddress())) {
			return;
		}
		int base_size = 40;
		if (!zpSDK.zp_page_create(58, base_size)) {
			UITools.ToastMsg(context, "创建打印页面失败");
			return;
		}
		currentY = 0;
		//title
		zpSDK.zp_draw_text_ex(0, currentY=currentY+4, e.getProduct_name(), "宋体", 4, 0, false, false, false);
		zpSDK.zp_draw_line(0, currentY=currentY+1, 58, currentY, 2);
		
		//body
		zpSDK.zp_draw_barcode2d(29.3, currentY+1.5, e.getProduct_qr(), BARCODE2D_TYPE.BARCODE2D_QRCODE, 3, 5, 0);
		
		zpSDK.zp_draw_text_ex(0, currentY+4, "规格：500g", null, 3, 0, false, false, false);
		zpSDK.zp_draw_text_ex(0, currentY+4+4, "产地：北京", null, 3, 0, false, false, false);
		zpSDK.zp_draw_text_ex(0, currentY+4+8, "原价："+Tool.fenToYuan(e.getProduct_original_price()), null, 3, 0, false, false, false);
		zpSDK.zp_draw_text_ex(0, currentY+4+14, Tool.fenToYuan(e.getProduct_retail_price()), null, 6, 0, false, false, false);
		
		zpSDK.zp_draw_line(0, currentY=currentY+21, 58, currentY, 2);
		
		//footer
		zpSDK.zp_draw_text_ex(0, currentY=currentY+4, e.getProduct_desc(), null, 3, 0, false, false, false);
		
		zpSDK.zp_page_print(false);
		zpSDK.zp_printer_status_detect();
		// zpSDK.zp_goto_mark_right(150);
		notice(PrinterStatus.codeOf(zpSDK.zp_printer_status_get(TIME_OUT)));
		zpSDK.zp_page_free();
		zpSDK.zp_close();
	}
	
	
	
	
	
	/**
	 * 是否有已经配对的打印机
	 * @return true 有；false 没有 
	 */
	public boolean hasPairedPrinter(){
		return pb.findPairedPrinter(context) != null;
	}
	
	/**
	 * 打印机提示信息
	 * @param status
	 */
	private void notice(PrinterStatus status){
		Builder builder = MyDialogManager.builder(context);
		builder.setTitle("提示").
		setPositiveButton("确定", null);
		switch (status) {
		case OK:return;
		case PAPER_ERROR : builder.setMessage(PrinterStatus.PAPER_ERROR.getDesc()).show();break;
		case NO_PAPER : builder.setMessage(PrinterStatus.NO_PAPER.getDesc()).show();break;
		case HOT : builder.setMessage(PrinterStatus.HOT.getDesc()).show();break;
		case UNKNOWN : builder.setMessage("未知打印机状态，请确保打印机处于开机状态并且靠近平板").show();break;
		default:break;
		}
	}
	
	
	/**
	 * 打印品名，防止品名过长情况
	 * @param text
	 * @param y
	 */
	private void draw_text_pinming(String text,int y){
		if(paint.measureText(text) > 60){
			int length = paint.breakText(text, true, 60, null);
			zpSDK.zp_draw_text_ex(0, y, text.substring(0, length),null,2.4,0,false,false,false);
			draw_text_pinming(text.substring(length,text.length()),y+4);
			currentY = currentY+4;
		}else{
			zpSDK.zp_draw_text_ex(0, y, text,null,2.4,0,false,false,false);
			currentY = currentY+4;
		}
	}
	
}

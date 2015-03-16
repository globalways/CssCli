package com.globalways.csscli.tools;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

/**
 * Picasso加载图片的圆角化工具，不能单独使用
 * 
 * @author James
 *
 */
public class RoundedCornerTransformation implements Transformation {

	private float roundPx;

	public RoundedCornerTransformation(int roundPx) {
		this.roundPx = roundPx / 2;
	}

	@Override
	public String key() {
		return "roundedCornerTransformation()";
	}

	@Override
	public Bitmap transform(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		// 得到画布
		Canvas canvas = new Canvas(output);
		// 将画布的四角圆化
		final int color = Color.RED;
		final Paint paint = new Paint();
		// 得到与图像相同大小的区域 由构造的四个值决定区域的位置以及大小
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		// 值越大角度越明显
		// final float roundPx = 20;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		// drawRoundRect的第2,3个参数一样则画的是正圆的一角，如果数值不同则是椭圆的一角
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		bitmap.recycle();
		return output;
	}

}

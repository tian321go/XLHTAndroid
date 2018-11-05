package com.axeac.android.sdk.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import com.axeac.android.sdk.tools.Base64Coding;

/**
 * 图片工具类
 * @author axeac
 * @version 1.0.0
 * */
public class ImageUtils {

	/**
	 * Bitmap对象转byte数组
	 * @return
	 * byte数组
	 * */
	public static byte[] Bitmap2Bytes(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 字符串转为Bitmap对象
	 * @param ctx
	 * @param param
	 * 字符串
	 * */
	public static Bitmap createImage(Context ctx, String param) {
		Bitmap img = null;
		if (param == null || param.length() == 0)
			return img;
		img = BitmapFactory.decodeStream(new ByteArrayInputStream(Base64Coding.decode(param)));
		return img;
	}

	/**
	 * 根据id获取Bitmap对象
	 * @param res
	 * Resources对象
	 * @param resId
	 * 资源id
	 * */
	public static Bitmap getResIcon(Resources res, int resId) {
		Drawable icon=res.getDrawable(resId);
		if (icon instanceof BitmapDrawable) {
			BitmapDrawable bd = (BitmapDrawable)icon;
			return bd.getBitmap();
		} else {
			return null;
		}
	}


	public static Bitmap generatorContactCountIcon(Bitmap icon, int numCount, Resources res) {
		Bitmap contactIcon = Bitmap.createBitmap(icon.getWidth(), icon.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(contactIcon);
		Paint iconPaint = new Paint();
		iconPaint.setDither(true);
		iconPaint.setFilterBitmap(true);
		Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		Rect dst = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		canvas.drawBitmap(icon, src, dst, iconPaint);
		Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
		countPaint.setColor(Color.RED);
		countPaint.setTextSize(20f);
		countPaint.setTypeface(Typeface.DEFAULT_BOLD);
		if (numCount >= 10) {
			canvas.drawText(String.valueOf(numCount), icon.getWidth() - 25, 20, countPaint);
		} else {
			canvas.drawText(String.valueOf(numCount), icon.getWidth() - 15, 20, countPaint);
		}
		return contactIcon;
	}

	public static Bitmap writeNumOnCenter(Bitmap icon, int numCount) {
		Bitmap contactIcon = Bitmap.createBitmap(icon.getWidth(), icon.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(contactIcon);
		Paint iconPaint = new Paint();
		iconPaint.setDither(true);
		iconPaint.setFilterBitmap(true);
		Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		Rect dst = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		canvas.drawBitmap(icon, src, dst, iconPaint);
		Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
		countPaint.setColor(Color.rgb(196, 88, 25));
		countPaint.setTextSize(40f);
		countPaint.setTypeface(Typeface.DEFAULT_BOLD);
		canvas.drawText(String.valueOf(numCount), icon.getWidth() / 2 - 10, icon.getHeight() / 2 + 10, countPaint);
		return contactIcon;
	}

	/**
	 * 重置图片尺寸
	 * @param filePath
	 * 文件地址
	 * @param w
	 * 图片宽度
	 * @param h
	 * 图片高度
	 * @return
	 * Bitmap对象
	 * */
	public static Bitmap resizeImage(String filePath, int w, int h) {
		Bitmap bitmapOrg = BitmapFactory.decodeFile(filePath);
		Bitmap resizedBitmap = resizeImage(bitmapOrg, w, h);
		return resizedBitmap;
	}

	/**
	 * 重置图片尺寸
	 * @param d
	 * Drawable对象
	 * @param w
	 * 图片宽度
	 * @param h
	 * 图片高度
	 * @return
	 * Drawable对象
	 * */
	public static Drawable resizeImage(Drawable d, int w, int h) {
		Bitmap bitmapOrg = ((BitmapDrawable) d).getBitmap();
		Bitmap resizedBitmap = resizeImage(bitmapOrg, w, h);
		Drawable drawable = new BitmapDrawable(resizedBitmap);
		return drawable;
	}

	/**
	 * 重置图片尺寸
	 * @param bitmapOrg
	 * Bitmap对象
	 * @param w
	 * 图片宽度
	 * @param h
	 * 图片高度
	 * @return
	 * Drawable对象
	 * */
	public static Bitmap resizeImage(Bitmap bitmapOrg, int w, int h) {
		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
		return resizedBitmap;
	}

	/**
	 * 在Bitmap上绘制Bitmap
	 * @param bitmap
	 * Bitamap对象
	 * @param imgbmp
	 * Bitmap对象
	 * @return
	 * Bitmap对象
	 * */
	public static Bitmap drawImageAtBitmap(Bitmap bitmap, Bitmap imgbmp) {
		int x = bitmap.getWidth();
		int y = bitmap.getHeight();
		Bitmap newbit = Bitmap.createBitmap(x, y, Config.ARGB_8888);
		Canvas canvas = new Canvas(newbit);
		Paint paint = new Paint();
		canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.drawBitmap(resizeImage(imgbmp, 40, 40), x - 40, 0, paint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return newbit;
	}

	/**
	 * 在Bitmap上绘制文字
	 * @param bitmap
	 * Bitmap对象
	 * @param text
	 * 文字
	 * @return
	 * Bitmap对象
	 * */
	public static Bitmap drawTextAtBitmap(Bitmap bitmap, String text) {
		int x = bitmap.getWidth();
		int y = bitmap.getHeight();
		// Create a bitmap of the same size as the original
		// 创建一个和原图同样大小的位图
		Bitmap newbit = Bitmap.createBitmap(x, y, Config.ARGB_8888);
		Canvas canvas = new Canvas(newbit);
		Paint paint = new Paint();
		// In the original position 0, 0 insert the original image
		// 在原始位置0，0插入原图
		canvas.drawBitmap(bitmap, 0, 0, paint);
		paint.setColor(Color.parseColor("#dedbde"));
		paint.setTextSize(20);
		// Write the word at the specified location in the original image
		// 在原图指定位置写上字
		if (text.length() >= 2) {
			canvas.drawText(text, 9, 25, paint);
		} else {
			canvas.drawText(text, 15, 25, paint);
		}
		canvas.save(Canvas.ALL_SAVE_FLAG);
		// save
		// 存储
		canvas.restore();
		return newbit;
	}

	/**
	 * 在Bitmap上绘制数字
	 * @param bitmap
	 * Bitmap对象
	 * @param num
	 * 数字字符串
	 * @return
	 * Bitmap对象
	 * */
	public static Bitmap drawNumAtBitmap(Bitmap bitmap, int num) {
		int x = bitmap.getWidth();
		int y = bitmap.getHeight();
		Bitmap newbit = Bitmap.createBitmap(x, y + 20, Config.ARGB_8888);
		Canvas canvas = new Canvas(newbit);
		Paint paint = new Paint();
		canvas.drawBitmap(bitmap, 0, 0, paint);
		paint.setColor(Color.RED);
		paint.setTextSize(20);
		canvas.drawText(num + "", x/2 - 8, y + 12, paint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		bitmap.recycle();
		return newbit;
	}

	/**
	 * 根据设备屏幕获取图片尺寸
	 * @return
	 * 尺寸
	 * */
	public static int getMenuBGSize() {
		int bgSize = 80;
		if (StaticObject.deviceWidth == 240) {
			bgSize = 40;
		} else if (StaticObject.deviceWidth == 320) {
			bgSize = 50;
		} else if (StaticObject.deviceWidth == 480) {
			bgSize = 80;
		} else if (StaticObject.deviceWidth == 720) {
			bgSize = 120;
		} else if (StaticObject.deviceWidth == 1080) {
			bgSize = 210;
		}
		return bgSize;
	}

	/**
	 * 根据设备屏幕获取文字尺寸
	 * @return
	 * 尺寸
	 * */
	public static int getMenuBGSizeIncrement() {
		int size = 15;
		if (StaticObject.deviceWidth == 240) {
			size = 7;
		} else if (StaticObject.deviceWidth == 320) {
			size = 10;
		} else if (StaticObject.deviceWidth == 480) {
			size = 15;
		} else if (StaticObject.deviceWidth == 720) {
			size = 20;
		} else if (StaticObject.deviceWidth == 1080) {
			size = 25;
		}
		return size;
	}

	/**
	 * 根据设备屏幕获取文字尺寸
	 * @return
	 * 尺寸
	 * */
	private static int getMenuTextSize() {
		int tSize = 13;
		if (StaticObject.deviceWidth == 240) {
			tSize = 10;
		} else if (StaticObject.deviceWidth == 320) {
			tSize = 10;
		} else if (StaticObject.deviceWidth == 480) {
			tSize = 13;
		} else if (StaticObject.deviceWidth == 720) {
			tSize = 14;
		} else if (StaticObject.deviceWidth == 1080) {
			tSize = 14;
		}
		return tSize;
	}

	/**
	 * 拼接两张图片为一张
	 * @param c
	 * Bitmap对象
	 * @param s
	 * Bitmap对象
	 * @return
	 * Bitmap对象
	 * */
	public static Bitmap combineImages(Bitmap c, Bitmap s) {
		if (c == null)
			return s;
		if (s == null)
			return c;
		Bitmap cs = null;
		int width, height = 0;
		if(c.getWidth() > s.getWidth()) {
			width = c.getWidth() + s.getWidth();
			height = c.getHeight();
		} else {
			width = s.getWidth() + s.getWidth();
			height = c.getHeight();
		}
		cs = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas comboImage = new Canvas(cs);
		comboImage.drawBitmap(c, 0f, 0f, null);
		comboImage.drawBitmap(s, c.getWidth(), 0f, null);
		return cs;
	}

	/**
	 * 压缩图片
	 * @param srcPath
	 * 文件路径
	 * */
	public static void compressImageByPath(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// Start reading the picture, then set options.inJustDecodeBounds to true
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		// At this point, bm is empty
		//此时返回bm为空
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// Now mainstream mobile phone is more than 800 * 480 resolution, so high and wide we set to
		//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;
		float ww = 480f;
		// Zoom ratio. Because it is a fixed scale, calculate only one of the data in height or width
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		//be = 1 that does not scale
		//be=1表示不缩放
		int be = 1;
		// If the width of a large fixed size according to the size of the zoom
		//如果宽度大的话根据宽度固定大小缩放
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		}
		// If the height is high, according to the width of the fixed size zoom
		//如果高度高的话根据宽度固定大小缩放
		else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		// Set the zoom ratio
		//设置缩放比例
		newOpts.inSampleSize = be;
		// Re-read the picture, pay attention to this time has the options.inJustDecodeBounds set back to false
		// Compress the size of the scale and then the quality of compression
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		//压缩好比例大小后再进行质量压缩
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// Quality compression method, where 100 means no compression, the compressed data stored in the baos
		//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		// Loop to determine if the compressed picture is greater than 100kb, greater than continuing to compress
		//循环判断如果压缩后图片是否大于100kb,大于继续压缩
		while ( baos.toByteArray().length / 1024>100) {
			// Reset baos
			//重置baos即清空baos
			baos.reset();
			// Every time it is reduced by 10
			//每次都减少10
			options -= 10;
			// Here compressed options%, the compressed data stored in the baos
			//这里压缩options%，把压缩后的数据存放到baos中
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		try {
			FileOutputStream fos = new FileOutputStream(srcPath);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 压缩图片
	 * @param image
	 * Bitmap对象
	 * */
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// Quality compression method, where 100 means no compression, the compressed data stored in the baos
		//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		image.compress(Bitmap.CompressFormat.PNG, 100, baos);
		int options = 100;
		// Loop to determine if the compressed picture is greater than 100kb, greater than continuing to compress
		//循环判断如果压缩后图片是否大于100kb,大于继续压缩
		while ( options > 0 && baos.toByteArray().length / 1024>300) {
			// Reset baos
			//重置baos即清空baos
			baos.reset();
			// Every time it is reduced by 10
			//每次都减少10
			options -= 10;
			// Here compressed options%, the compressed data stored in the baos
			//这里压缩options%，把压缩后的数据存放到baos中
			image.compress(Bitmap.CompressFormat.PNG, options, baos);

		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}
}
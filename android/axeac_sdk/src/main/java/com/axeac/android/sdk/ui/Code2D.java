package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:Two-dimensional code display
 * 二维码生成组件
 * @author axeac
 * @version 1.0.0
 */
public class Code2D extends Component {

	/**
	 * 标题文本
	 * <br>默认值为空
	 * */
	private String title = "";
	/**
	 * 标题图标
	 * <br>默认值为空
	 * */
	private String icon = "";
	/**
	 * 二维码字符串
	 * <br>默认值为空
	 * */
	private String text = "";

	/**
	 * 二维码尺寸
	 * */
	private String size = "70%";

	private String textAll = "";

	/**
	 * 是否清除二维码
	 * <br>默认值为false
	 * */
	private boolean clear = false;
	/**
	 * 是否重新绘制二维码
	 * <br>默认值为false
	 * */
	private boolean refresh = false;

	private boolean display = true;

	private RelativeLayout valLayout;
	private ImageView code2dImg;
	private TextView code2dTxt;

	private String returnValue = "";

	public Code2D(Activity ctx) {
		super(ctx);
		this.returnable = false;
		valLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_code2d, null);
		code2dImg = (ImageView) valLayout.findViewById(R.id.code2d_img);
		code2dTxt = (TextView) valLayout.findViewById(R.id.code2d_txt);
	}

	/**
	 * 设置标题文本
	 * @param title
	 * 标题文本
	 * */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 设置标题图标
	 * @param icon
	 * 标题图标
	 * */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * 设置二维码字符串
	 * @param text
	 * 二维码字符串
	 * */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 对size赋值
	 * @param size
	 * 二维码尺寸
	 * */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * 连接字符串
	 * */
	public void addText(String s) {
		textAll += s + "\r\n";
	}

	/**
	 * 返回是否清楚二维码
	 * return
	 * true为清除，false为不清除
	 * */
	public boolean isClear() {
		return clear;
	}

	/**
	 * 设置是否清除二维码
	 * @param clear
	 * 可选值 true|false
	 * */
	public void setClear(boolean clear) {
		this.clear = clear;
	}

	/**
	 * 返回是否重绘二维码
	 * @return
	 * true代表重绘，false代表不重绘
	 * */
	public boolean isRefresh() {
		return refresh;
	}

	/**
	 * 设置是否重绘二维码
	 * @param refresh
	 * 可选值 true|false
	 * */
	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}

	public void setDisplay(String display){
		this.display = Boolean.parseBoolean(display);
	}

	/**
	 * 清除二维码
	 * */
	public void clear() {
		text = "";
		textAll = "";
		code2dImg.setVisibility(View.GONE);
		code2dTxt.setText(R.string.axeac_toast_exp_contentisnil);
	}

	/**
	 * 重新绘制二维码
	 * */
	public void refresh() {
		execute();
	}

	/**
	 * 执行方法
	 * */
	@Override
	public void execute() {
		if(!this.visiable) return;
		if (this.width == "-1" && this.height == -1) {
			valLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
		} else {
			if (this.width == "-1") {
				valLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, this.height, 1));
			} else if (this.height == -1) {
				if (this.width.endsWith("%")) {
					int viewWeight = 100 - (int) Float.parseFloat(this.width.substring(0, this.width.indexOf("%")));
					valLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, viewWeight));
				} else {
					valLayout.setLayoutParams(new LinearLayout.LayoutParams(Integer.parseInt(this.width), LinearLayout.LayoutParams.WRAP_CONTENT));
				}
			} else {
				if (this.width.endsWith("%")) {
					int viewWeight = 100 - (int) Float.parseFloat(this.width.substring(0, this.width.indexOf("%")));
					valLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, viewWeight));
				} else {
					valLayout.setLayoutParams(new LinearLayout.LayoutParams(Integer.parseInt(this.width), this.height));
				}
			}
		}
		String t = "";
		if (!text.equals("")) {
			t = text;
		} else {
			t = textAll;
		}
		if (t.equals("")) {
			code2dImg.setVisibility(View.GONE);
			code2dTxt.setText(R.string.axeac_toast_exp_contentisnil);
		} else {
			createBarcode(t);
		}
		int r = Integer.parseInt(bgColor.substring(0, 3));
		int g = Integer.parseInt(bgColor.substring(3, 6));
		int b = Integer.parseInt(bgColor.substring(6, 9));
		valLayout.setBackgroundColor(Color.rgb(r, g, b));
		valLayout.getBackground().setAlpha(180);
		if (this.bgImage != null && !"".equals(bgImage)) {
			BitmapDrawable draw;
			try {
				draw = new BitmapDrawable(this.ctx.getResources(),BitmapFactory.decodeStream(this.ctx.getResources().getAssets().open(bgImage + ".png")));
				valLayout.setBackgroundDrawable(draw);
				valLayout.getBackground().setAlpha(180);
			} catch (IOException e) {
			}
		}
		if (alpha!=100)
		valLayout.getBackground().setAlpha((int)(255 * ((float)this.alpha/100)));
	}

	/**
	 * 根据内容生成二维码
	 * @param text
	 * 生成二维码的内容
	 * */
	private void createBarcode(String text) {
		int width = StaticObject.deviceWidth;
		int height = StaticObject.deviceHeight;
		int smallerDimension = (width < height ? width : height);
		try {
			Bitmap bitmap = Create2DCode(text);
			if (bitmap == null) {
				code2dImg.setVisibility(View.GONE);
				code2dTxt.setText(R.string.axeac_barcode_encode_contents_failed);
				return;
			}
			returnValue = ctx.getString(R.string.axeac_code2d);
			code2dImg.setImageBitmap(bitmap);
			if(!display){
				code2dTxt.setVisibility(View.GONE);
			}
			code2dTxt.setText(text);
		} catch (WriterException e) {
			code2dImg.setVisibility(View.GONE);
			code2dTxt.setText(R.string.axeac_barcode_encode_contents_failed);
		}
	}

	/**
	 * 根据内容生成二维码，并返回二维码图片
	 * @param str
	 * 生成二维码的内容
	 * @return
	 * Bitmap对象
	 * */
	private Bitmap Create2DCode(String str) throws WriterException {
		// Generate a two-dimensional matrix, specify the size of the code. Do not generate a picture and then zoom, it will blur lead to recognition failure
		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
//		BitMatrix matrix = new MultiFormatWriter().encode(str,
//				BarcodeFormat.QR_CODE, 300, 300);
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		int screenwidth = dm.widthPixels;
		float bitmapSize;

		if(size.indexOf("%")!=-1) {
			int w = Integer.parseInt(size.replaceAll("%", ""));
			if(w>=100){
				w = 99;
			}else if(w<=0){
				w = 30;
			}
			float percent = Float.parseFloat("0."
					+ w);
			bitmapSize = screenwidth * percent;
		}else{
			bitmapSize = screenwidth * 0.3f;
		}
		BitMatrix matrix = null;
		try {
			matrix = new MultiFormatWriter().encode(new String(str.getBytes("UTF-8"),"ISO-8859-1"),
                    BarcodeFormat.QR_CODE, (int)bitmapSize, (int)bitmapSize);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		int width = matrix.getWidth();
		int height = matrix.getHeight();
		// Two-dimensional matrix into a one-dimensional array of pixels, that is, has been sideways row
		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				}

			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		// Generate bitmap through the array of pixels, with reference to the api
		// 通过像素数组生成bitmap,具体参考api
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}


	@Override
	public View getView() {
		return valLayout;
	}

	@Override
	public String getValue() {
		return returnValue;
	}

	@Override
	public void repaint() {

	}

	@Override
	public void starting() {

	}

	@Override
	public void end() {

	}
}
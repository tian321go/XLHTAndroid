package com.axeac.android.sdk.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.axeac.android.sdk.R;

/**
 * 显示图片界面
 * @author axeac
 * @version 1.0.0
 * */
public class ImageShowActivity extends BaseActivity {
	private ImageView signIv;
	private ImageView photoIv;
	private boolean isvis = false;
	private WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTransStatus(ImageShowActivity.this);
		this.setContentView(R.layout.axeac_mulit);
		initToolbar();
		signIv = (ImageView) this.findViewById(R.id.signimage);
		photoIv = (ImageView) this.findViewById(R.id.mulitimage);
		webView = (WebView)this.findViewById(R.id.webView) ;

		String str = this.getIntent().getStringExtra("path");
		String[] sp = str.split(";");
		String name = sp[0];
		final String path = sp[1];
		System.out.println(path);
		if (str.startsWith("Adjunct")) {
			webView.setVisibility(View.VISIBLE);
			getWindow().getDecorView().post(new Runnable() {

				@Override
				public void run() {
					new Handler().post(new Runnable() {
						@Override
						public void run() {
							toolbarTitle.setText(R.string.axeac_preview_title);
						}
					});
				}
			});
			webView.loadUrl(path);
			webView.setWebViewClient(new WebViewClient(){
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					//返回值是true的时候是控制网页在WebView中去打开，如果为false调用系统浏览器或第三方浏览器打开
					view.loadUrl(url);
					return true;
				}
				@Override public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

					handler.proceed();
				 }
				//WebViewClient帮助WebView去处理一些页面控制和请求通知
			});
			WebSettings settings = webView.getSettings();
			settings.setJavaScriptEnabled(true);
			settings.setJavaScriptCanOpenWindowsAutomatically(true);
			settings.setBuiltInZoomControls(true); //设置内置的缩放控件。
			settings.setLoadsImagesAutomatically(true);  //支持自动加载图片
			settings.setDisplayZoomControls(false);//隐藏缩放控件

			settings.setSupportZoom(true);
			settings.setAllowFileAccess(true);
			settings.setUseWideViewPort(true);
			settings.setLoadWithOverviewMode(true);
			settings.setDomStorageEnabled(true);
			//WebView加载页面优先使用缓存加载
			settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
			//页面加载
			final String pathc = path;
			webView.setWebChromeClient(new WebChromeClient() {

				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					if (pathc.indexOf("returnType=img")>0) {
						//newProgress   1-100之间的整数
						if (newProgress > 20) {
							removeProgressDialog();
						} else {
							showProgressDialog();
						}
					}
				}
			});

		} else {
			String picname = "";
			if (name.equals("PhotoSelector")) {
				photoIv.setVisibility(View.VISIBLE);
				photoIv.setImageBitmap(getBitmapByPath(path));
				picname = path.substring(path.lastIndexOf("/")+1,path.length());
				photoIv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						int imgheight1 = photoIv.getDrawable().getChangingConfigurations();
						int imgheight2 = photoIv.getMeasuredHeight();
						int screenheight = ImageShowActivity.this.getWindowManager().getDefaultDisplay().getHeight();
						if(imgheight1>screenheight){
							toolbar.setVisibility(View.GONE);
						}else{
							toolbar.setVisibility(View.VISIBLE);
						}
					}
				});
			} else {
				signIv.setVisibility(View.VISIBLE);
				signIv.setImageBitmap(getBitmapByPath(path));
				picname = path.substring(path.lastIndexOf("/")+1,path.length());
				signIv.setOnClickListener(listener);
			}

			final String picname_ = picname;
			getWindow().getDecorView().post(new Runnable() {

				@Override
				public void run() {
					new Handler().post(new Runnable() {
						@Override
						public void run() {
							toolbarTitle.setText(picname_);
						}
					});
				}
			});
		}

		backPage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageShowActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if(!isvis) {
				toolbar.setVisibility(View.GONE);
				isvis = true;
			}else{
				toolbar.setVisibility(View.VISIBLE);
				isvis = false;
			}
		}
	};
	/**
	 * 根据路径获取图片
	 * @param imageFile
	 * 图片路径
	 * @return
	 * Bitmap对象
	 * */
	private static Bitmap getBitmapByPath(String imageFile){
		BitmapFactory.Options opts =  new  BitmapFactory.Options();
		opts.inJustDecodeBounds =  true ;
		BitmapFactory.decodeFile(imageFile, opts);
		opts.inSampleSize = computeSampleSize(opts, - 1, 1024 * 600);
		opts.inJustDecodeBounds =  false ;
		return BitmapFactory.decodeFile(imageFile, opts);
	}

	/**
	 * 图片压缩
	 * @param options
	 * BitmapFactory.Options对象
	 * @param minSideLength
	 * 最小滑动长度
	 * @param maxNumOfPixels
	 * 最大尺寸
	 * */
	public static int computeSampleSize(BitmapFactory.Options options, int  minSideLength, int  maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);
		int roundedSize;
		if(initialSize <= 8) {
			roundedSize = 1;
			while(roundedSize < initialSize) {
				roundedSize <<=  1;
			}
		} else {
			roundedSize = (initialSize +  7 ) / 8 * 8;
		}
		return roundedSize;
	}

	/**
	 * 图片压缩
	 * */
	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

}
package com.axeac.android.sdk.ui.button;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * 系统级的按钮，固定事件
 * @author axeac
 * @version 1.0.0
 * */
public abstract class SystemButton extends Component {

	protected Button btn;
	protected View layout;
	/**
	 * 按钮文本 默认值为空
	 * */
	protected String text = "";

	/**
	 * 按钮图标
	 * */
	protected String icon;

	/**
	 * 不为空时弹出对话框信息
	 * */
	protected String confirm;

	public SystemButton(Activity ctx) {
		super(ctx);
		this.returnable = false;
		layout = LayoutInflater.from(ctx).inflate(R.layout.axeac_systembuttom, null);
		btn = (Button) layout.findViewById(R.id.systembutton_text);
		btn.setOnClickListener(listener);
	}

	/**
	 * 对text赋值（按钮文本）
	 * @param text
	 * text（按钮文本）
	 * */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 对icon赋值（按钮图标）
	 * @param icon
	 * icon（按钮图标）
	 * */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * 对confirm赋值（对话框消息）
	 * @param confirm
	 * confirm（对话框消息）
	 * */
	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (confirm != null && !confirm.equals("")) {
				CustomDialog.Builder dialog = new CustomDialog.Builder(ctx);
				dialog.setTitle(R.string.axeac_msg_prompt);
				dialog.setMessage(confirm);
				dialog.setPositiveButton(R.string.axeac_msg_confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						executeBtn();
						dialog.dismiss();
					}
				});
				dialog.setNegativeButton(R.string.axeac_msg_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.create().show();
			} else {
				executeBtn();
			}
		}
	};

	public abstract void executeBtn();

	@Override
	public void execute() {
		btn.setText(text);
	}

	@Override
	public String getValue() {
		return this.Id;
	}

	@Override
	public View getView() {

		return layout;
	}

	/**
	 * 设置文字和背景图片
	 * @param mLayout
	 * button所在布局
	 * @param mImgBtn
	 * 背景图片
	 * @param mTxtView
	 * 显示的文字
	 * */
	public void setImageAndText(RelativeLayout mLayout, ImageView mImgBtn, TextView mTxtView) {
		mLayout.setOnClickListener(listener);
		BitmapDrawable draw;
		if (icon != null && !icon.equals("")) {
			Log.e("gtu3",icon+"=="+text);
			Glide.with(ctx.getApplicationContext())
					.load(StaticObject.getImageUrl("res-img:"+icon))
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.into(mImgBtn);

		}
		if (text != null && !text.equals("")) {
			mTxtView.setText(text);
		}
	}
}
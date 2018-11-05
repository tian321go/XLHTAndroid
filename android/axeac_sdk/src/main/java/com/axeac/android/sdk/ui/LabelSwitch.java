package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.ui.base.LabelComponent;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:Switch
 * 开关
 * @author axeac
 * @version 1.0.0
 */
public class LabelSwitch extends LabelComponent {

	private LinearLayout valLayout;
	private ImageView imageView;

	/**
	 * 开关状态
	 * */
	private boolean state;

	public LabelSwitch(Activity ctx) {
		super(ctx);
		this.returnable = true;
		valLayout = new LinearLayout(ctx);
		valLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		valLayout.setOrientation(LinearLayout.VERTICAL);
		int width = 110, height = 60;
		if (StaticObject.deviceWidth == 240) {
			width = 50; height = 30;
		} else if (StaticObject.deviceWidth == 320) {
			width = 70; height = 40;
		} else if (StaticObject.deviceWidth == 480) {
			width = 110; height = 60;
		} else if (StaticObject.deviceWidth == 720) {
			width = 160; height = 90;
		} else if (StaticObject.deviceWidth == 1080) {
			width = 160; height = 90;
		}
		imageView = new ImageView(ctx);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,height);
		lp.setMargins(0,0,25,0);
		imageView.setLayoutParams(lp);
//		imageView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
		valLayout.addView(imageView);
		valLayout.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		this.view = valLayout;
	}

	/**
	 * 本类监听
	 * */
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (state) {
				imageView.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.axeac_switch_close));
				state = false;
			} else {
				imageView.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.axeac_switch_open));
				state = true;
			}
			imageView.invalidate();
		}
	};

	/**
	 * 设置开关状态
	 * @param state
	 * 可选值true/false
	 * */
	public void setState(String state) {
		this.state = Boolean.parseBoolean(state);
	}

	/**
	 * 执行方法
	 * */
	@Override
	public void execute() {
		if(!this.visiable) return;
		if (!readOnly) {
			imageView.setOnClickListener(listener);
		}
		if (state)
			imageView.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.axeac_switch_open));
		else
			imageView.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.axeac_switch_close));
	};
	
	@Override
	public View getView() {
		return super.getView();
	}
	
	@Override
	public String getValue() {
		return state + "";
	}

	@Override
	public void repaint() {
		
	}

	@Override
	public void starting() {
		this.buildable = false;
	}

	@Override
	public void end() {
		this.buildable = true;
	}

}
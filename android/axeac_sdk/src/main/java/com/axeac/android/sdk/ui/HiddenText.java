package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.view.View;

import com.axeac.android.sdk.ui.base.Component;
/**
 * 隐藏域
 * @author axeac
 * @version 1.0.0
 * */
public class HiddenText extends Component {

	/**
	 * 隐藏域的文本值
	 * */
	private String text;
	
	public HiddenText(Activity ctx) {
		super(ctx);
		this.returnable = true;
	}

	/**
	 * 设置隐藏域的文本值
	 * */
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void execute() {
		
	}
	
	@Override
	public View getView() {
		return null;
	}
	
	@Override
	public String getValue() {
		return text;
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
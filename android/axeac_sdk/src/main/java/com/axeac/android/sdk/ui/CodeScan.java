package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.activity.CaptureActivity;
import com.axeac.android.sdk.ui.base.LabelComponent;

/**
 * describe:Barcode scanning controls
 * 条码扫描控件
 * @author axeac
 * @version 1.0.0
 */
public class CodeScan extends LabelComponent {

	public static final int CODESCAN_WITH_DATA = 4002;

	private EditText textField;
	private ImageView scanBtn;

	/**
	 * 设置和返回二维码和一维码的扫描的文本结果
	 * */
	private String text = "";
	/**
	 * BarCode条码、2DCode二维码
	 * */
	private String option = "barcode";

	/**
	 * 负责将position值赋值给curPosition的中间值
	 * */
	private int pos = 0;

	/**
	 * 标记handler位置的position
	 * */
	public static int position = 0;

	/**
	 * 标记handler位置的position
	 * */
	public static int curPosition = 0;

	/**
	 * 存储handler的Map集合
	 * */
	public static Map<Integer, Handler> handlerMap = new HashMap<Integer, Handler>();

	public EditText getEditText(){
		return textField;
	}

	public CodeScan(Activity ctx) {
		super(ctx);
		this.returnable = true;
		RelativeLayout valLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_codescan, null);
		textField = (EditText) valLayout.findViewById(R.id.codescan_txt);
		scanBtn = (ImageView) valLayout.findViewById(R.id.codescan_btn);
		this.view = valLayout;
		handlerMap.put(++position, mHandler);
		pos = position;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CODESCAN_WITH_DATA:
				textField.setText(msg.obj.toString().split(" ")[0]);
				System.out.println(msg.obj.toString());
				textField.setText(msg.obj.toString());
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 设置二维码和一维码的扫描的文本结果
	 * @param text
	 * 二维码和一维码的扫描的文本结果
	 * */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * BarCode条码、2DCode二维码
	 * */
	public void setOption(String option) {
		this.option = option.toLowerCase().trim();
	}

	/**
	 * 扫描后跳转界面
	 * */
	private void onScan() {
		curPosition = pos;
		textField.setVisibility(View.VISIBLE);
		Intent intent = new Intent(ctx, CaptureActivity.class);
		ctx.startActivityForResult(intent, CODESCAN_WITH_DATA);
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onScan();
		}
	};

	/**
	 * 执行方法
	 * */
	@Override
	public void execute() {
		if (!readOnly) {
			scanBtn.setOnClickListener(listener);
		}
		if (text.equals("")) {
			//			textField.setVisibility(View.GONE);
			textField.setTextColor(ctx.getResources().getColor(R.color.texthint));
			textField.setHint(ctx.getResources().getString(R.string.axeac_labeltext_hint));
		} else {
			textField.setText(text);
		}
	}

	@Override
	public View getView() {
		return super.getView();
	}

	@Override
	public String getValue() {
		return textField.getText().toString();
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
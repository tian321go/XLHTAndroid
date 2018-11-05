package com.axeac.android.client.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import com.axeac.android.client.R;
import com.axeac.android.sdk.activity.BaseActivity;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * 流量统计界面
 * @author axeac
 * @version 2.3.0.0001
 *
 * */
public class FlowCountActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTransStatus(FlowCountActivity.this);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.setContentView(R.layout.axeac_common_layout_normal);
		setTitle(R.string.settings_flowcount);
		FrameLayout layout = (FrameLayout) this.findViewById(R.id.settings_layout_center);
		LayoutInflater mInflater = LayoutInflater.from(this);
		LinearLayout convertView = (LinearLayout) mInflater.inflate(R.layout.flowcount, null);
		layout.addView(convertView);
		RelativeLayout networkSave = (RelativeLayout) this.findViewById(R.id.menu_item_first);
		networkSave.setVisibility(View.VISIBLE);
		networkSave.setOnClickListener(mClearBtnClickListener());
		ImageView networkSaveBtn = (ImageView) this.findViewById(R.id.menu_item_first_btn);
		networkSaveBtn.setImageResource(R.drawable.btn_delete);
		TextView networkSaveText = (TextView) this.findViewById(R.id.menu_item_first_text);
		networkSaveText.setText(R.string.axeac_msg_clear);
		showNetworkFlow();
		backPage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backFuc();
			}
		});
	}

	private void showNetworkFlow() {
		TextView curRx = (TextView) this.findViewById(R.id.flowcount_curRx);
		curRx.setText(formatFlowSize(StaticObject.readCurRXTX()[0]));
		TextView curTx = (TextView) this.findViewById(R.id.flowcount_curTx);
		curTx.setText(formatFlowSize(StaticObject.readCurRXTX()[1]));
		TextView curCount = (TextView) this.findViewById(R.id.flowcount_curCount);
		curCount.setText(formatFlowSize(StaticObject.readCurRXTX()[0] + StaticObject.readCurRXTX()[1]));

		TextView allRx = (TextView) this.findViewById(R.id.flowcount_allRx);
		allRx.setText(formatFlowSize(StaticObject.readAllRXTX()[0]));
		TextView allTx = (TextView) this.findViewById(R.id.flowcount_allTx);
		allTx.setText(formatFlowSize(StaticObject.readAllRXTX()[1]));
		TextView allCount = (TextView) this.findViewById(R.id.flowcount_allCount);
		allCount.setText(formatFlowSize(StaticObject.readAllRXTX()[0] + StaticObject.readAllRXTX()[1]));
	}

	private View.OnClickListener mClearBtnClickListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StaticObject.clearAllRXTX();
				StaticObject.clearCurRXTX();
				showNetworkFlow();
			}
		};
	};

	private String formatFlowSize(long flowSize) {
		DecimalFormat format = new DecimalFormat("0.0");
		String size = "";
		if (flowSize < 1024) {
			size = format.format(flowSize) + "B";
		} else if (flowSize < 1048576) {
			size = format.format((double) flowSize / 1024) + "K";
		} else if (flowSize < 1073741824) {
			size = format.format((double) flowSize / 1048576) + "M";
		} else {
			size = format.format((double) flowSize / 1073741824) + "G";
		}
		return size;
	}

	private  void backFuc(){this.finish();}
}
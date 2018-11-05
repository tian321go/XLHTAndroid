package com.axeac.android.client.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axeac.android.client.R;
import com.axeac.android.sdk.activity.BaseActivity;
import com.axeac.android.sdk.utils.StaticObject;
/**
 * 系统设置界面
 * @author axeac
 * @version 2.3.0.0001
 * */
public class SystemSetupsActivity extends BaseActivity {

	private Context mContext;

	private Button thirdVerifyBtn;
	private Button useCertBtn;
	private Button checkNewVersionBtn;
	private Button backgroudMsgBtn;
	private TextView chooseMsgTime;
	private RelativeLayout msgRelativelayout;
	private View lineView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTransStatus(SystemSetupsActivity.this);
		this.setContentView(R.layout.axeac_common_layout_normal);
		mContext = this;
		this.findViewById(R.id.settings_layout_bottom).setVisibility(View.GONE);
		setTitle(R.string.settings_systemsetups);

		FrameLayout layout = (FrameLayout) this.findViewById(R.id.settings_layout_center);
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		LinearLayout convertView = (LinearLayout) mInflater.inflate(R.layout.systemsetups, null);
		layout.addView(convertView);

		thirdVerifyBtn = (Button) convertView.findViewById(R.id.systemsetups_thirdverify);
		thirdVerifyBtn.setOnClickListener(mBtnClickListener());
		boolean thirdVerifyFlag = StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_THIRDVERIFY, false);
		if (thirdVerifyFlag) {
			thirdVerifyBtn.setBackgroundResource(R.drawable.axeac_switch_open);
		} else {
			thirdVerifyBtn.setBackgroundResource(R.drawable.axeac_switch_close);
		}

		useCertBtn = (Button) convertView.findViewById(R.id.systemsetups_usecert);
		useCertBtn.setOnClickListener(mBtnClickListener());
		boolean useCertFlag = StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_USECERT, false);
		if (useCertFlag) {
			useCertBtn.setBackgroundResource(R.drawable.axeac_switch_open);
		} else {
			useCertBtn.setBackgroundResource(R.drawable.axeac_switch_close);
		}

		checkNewVersionBtn = (Button) convertView.findViewById(R.id.systemsetups_checknewversion);
		checkNewVersionBtn.setOnClickListener(mBtnClickListener());
		boolean checknewversion = StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_CHECKNEWVERSION, false);
		if (checknewversion) {
			checkNewVersionBtn.setBackgroundResource(R.drawable.axeac_switch_open);
		} else {
			checkNewVersionBtn.setBackgroundResource(R.drawable.axeac_switch_close);
		}

		backgroudMsgBtn = (Button) convertView.findViewById(R.id.systemsetups_backgroundmsg);
		msgRelativelayout = (RelativeLayout) convertView.findViewById(R.id.msg_relativelayout);
		lineView = (View) convertView.findViewById(R.id.line_msg);
		backgroudMsgBtn.setOnClickListener(mBtnClickListener());
		if (true) {
			backgroudMsgBtn.setBackgroundResource(R.drawable.axeac_switch_open);
			msgRelativelayout.setVisibility(View.GONE);
			lineView.setVisibility(View.GONE);
		} else {
			backgroudMsgBtn.setBackgroundResource(R.drawable.axeac_switch_close);
			msgRelativelayout.setVisibility(View.GONE);
			lineView.setVisibility(View.GONE);
		}
		chooseMsgTime = (TextView) convertView.findViewById(R.id.systemsetups_msgtimes);
		chooseMsgTime.setOnClickListener(mBtnClickListener());
		long times = StaticObject.read.getLong(StaticObject.SYSTEMSETUPS_MSGTIMES,10);
		chooseMsgTime.setText(String.valueOf(times)+"min");
		backPage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backFuc();
			}
		});
	}

	// describe:button listener，We judge the display of the picture and the storage of information according to the flag
	/**
	 * button点击事件，根据标志位判断图片的展示以及信息的存储
	 * */
	private View.OnClickListener mBtnClickListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.equals(thirdVerifyBtn)) {
					boolean thirdVerifyFlag = StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_THIRDVERIFY, false);
					if (thirdVerifyFlag) {
						thirdVerifyBtn.setBackgroundResource(R.drawable.axeac_switch_close);
						StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_THIRDVERIFY, false).commit();
					} else {
						thirdVerifyBtn.setBackgroundResource(R.drawable.axeac_switch_open);
						StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_THIRDVERIFY, true).commit();
					}
				}
				if (v.equals(useCertBtn)) {
					boolean useCertFlag = StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_USECERT, false);
					if (useCertFlag) {
						useCertBtn.setBackgroundResource(R.drawable.axeac_switch_close);
						StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_USECERT, false).commit();
					} else {
						useCertBtn.setBackgroundResource(R.drawable.axeac_switch_open);
						StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_USECERT, true).commit();
					}
				}
				if (v.equals(checkNewVersionBtn)) {
					boolean checknewversion = StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_CHECKNEWVERSION, false);
					if (checknewversion) {
						checkNewVersionBtn.setBackgroundResource(R.drawable.axeac_switch_close);
						StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_CHECKNEWVERSION, false).commit();
					} else {
						checkNewVersionBtn.setBackgroundResource(R.drawable.axeac_switch_open);
						StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_CHECKNEWVERSION, true).commit();
					}
				}
//				if (v.equals(backgroudMsgBtn)) {
//					boolean backgroudMsg = StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_BACKGROUDMSG, false);
//					if (backgroudMsg) {
//						msgRelativelayout.setVisibility(View.GONE);
//						lineView.setVisibility(View.GONE);
//						backgroudMsgBtn.setBackgroundResource(R.drawable.axeac_switch_close);
//						StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_BACKGROUDMSG, false).commit();
//					} else {
//						lineView.setVisibility(View.VISIBLE);
//						msgRelativelayout.setVisibility(View.VISIBLE);
//						backgroudMsgBtn.setBackgroundResource(R.drawable.axeac_switch_open);
//						StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_BACKGROUDMSG, true).commit();
//					}
//				}
				if (v.equals(chooseMsgTime)){
					View  view=(LinearLayout) getLayoutInflater().inflate(R.layout.dialog_numedit,null);
					final EditText editText = (EditText) view.findViewById(R.id.msg_times);
					editText.setText(chooseMsgTime.getText().toString().substring(0,chooseMsgTime.getText().length()==4?1:2));
					final AlertDialog.Builder builder =new AlertDialog.Builder(mContext);
					builder.setTitle(R.string.systemsetups_msgtimes);
					builder.setMessage(R.string.systemsetups_choosetimes);
					builder.setView(view);
					builder.setPositiveButton(R.string.systemsetups_y, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String msgtime = editText.getText().toString();
							if(msgtime!=null&&!"".equals(msgtime)){
								if(Long.parseLong(msgtime)>60){
									StaticObject.wirte.edit().putLong(StaticObject.SYSTEMSETUPS_MSGTIMES, Long.parseLong("60")).commit();
									chooseMsgTime.setText("60min");
								}else if(Long.parseLong(msgtime)<1){
									StaticObject.wirte.edit().putLong(StaticObject.SYSTEMSETUPS_MSGTIMES, Long.parseLong("1")).commit();
									chooseMsgTime.setText("1min");
								}else {
									StaticObject.wirte.edit().putLong(StaticObject.SYSTEMSETUPS_MSGTIMES, Long.parseLong(msgtime)).commit();
									chooseMsgTime.setText(msgtime + "min");
								}
								Toast.makeText(mContext,R.string.systemsetups_success,Toast.LENGTH_SHORT).show();
							}else {
								Toast.makeText(mContext, R.string.systemsetups_fail, Toast.LENGTH_SHORT).show();
							}
							dialog.dismiss();
						}
					});
					builder.setNegativeButton(R.string.systemsetups_n, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.create().show();
				}
			}
		};
	};

	private View.OnClickListener mBackBtnClickListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backFuc();
			}
		};
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backFuc();
		}
		return false;
	}

	private void backFuc() {
		mContext.startActivity(new Intent(mContext, SettingsActivity.class));
		this.finish();
	}
}
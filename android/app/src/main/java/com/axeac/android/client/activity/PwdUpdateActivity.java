package com.axeac.android.client.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axeac.android.client.R;
import com.axeac.android.sdk.activity.BaseActivity;
import com.axeac.android.sdk.jhsp.JHSPResponse;
import com.axeac.android.sdk.retrofit.OnRequestCallBack;
import com.axeac.android.sdk.retrofit.UIHelper;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:Set password interface
 * <br>设置密码界面
 * @author axeac
 * @version 2.3.0.0001
 * */
public class PwdUpdateActivity extends BaseActivity {

    private Context mContext;

    private ProgressDialog loadingDialog;

    private EditText userName;
    private EditText inputPwd;
    private EditText reputPwd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransStatus(PwdUpdateActivity.this);
        this.setContentView(R.layout.axeac_common_layout_normal);
        mContext = this;
        setTitle(R.string.settings_password);

        FrameLayout layout = (FrameLayout) this.findViewById(R.id.settings_layout_center);
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        LinearLayout convertView = (LinearLayout) mInflater.inflate(R.layout.pwdupdate, null);
        layout.addView(convertView);
        userName = (EditText) convertView.findViewById(R.id.username);
        inputPwd = (EditText) convertView.findViewById(R.id.pwdupdate_input);
        reputPwd = (EditText) convertView.findViewById(R.id.pwdupdate_reput);

        String userNameText = StaticObject.read.getString(StaticObject.USERNAME, "Demo");
        userName.setText(userNameText);

        RelativeLayout pwdSaved = (RelativeLayout) this.findViewById(R.id.menu_item_first);
        pwdSaved.setVisibility(View.VISIBLE);
        pwdSaved.setOnClickListener(mSavedBtnClickListener());
        ImageView pwdSavedBtn = (ImageView) this.findViewById(R.id.menu_item_first_btn);
        pwdSavedBtn.setImageResource(R.drawable.btn_saved);
        TextView pwdSavedText = (TextView) this.findViewById(R.id.menu_item_first_text);
        pwdSavedText.setText(R.string.axeac_msg_save);

        RelativeLayout networkBack = (RelativeLayout) this.findViewById(R.id.menu_item_second);
        networkBack.setVisibility(View.VISIBLE);
        networkBack.setOnClickListener(mBackBtnClickListener());
        ImageView networkBackBtn = (ImageView) this.findViewById(R.id.menu_item_second_btn);
        networkBackBtn.setImageResource(R.drawable.btn_back);
        TextView networkBackText = (TextView) this.findViewById(R.id.menu_item_second_text);
        networkBackText.setText(R.string.axeac_btn_return);

        backPage.setOnClickListener(mBackBtnClickListener());
    }

    /**
     * 更新密码
     * @param username
     * 用户名
     * @param password
     * 用户密码
     * @param parm
     * 请求字段和新密码
     * */
    private void update(final String username, final String password, final String parm) {
        UIHelper.sendRequestCom(this, username, password, parm, new OnRequestCallBack() {
            @Override
            public void onStart() {
                showProgressDialog();
            }

            @Override
            public void onCompleted() {
                removeProgressDialog();
            }

            @Override
            public void onSuccesed(JHSPResponse response) {
                if (response.getCode() == 0) {
                    if (!CommonUtil.isResponseNoToast(response.getMessage())) {
                        Toast.makeText(mContext, response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    StaticObject.loginFlag = false;
                    startActivity(new Intent(PwdUpdateActivity.this, LoginActivity.class));
                    finish();
                } else {
                    showToast(response.getMessage());
                }
            }

            @Override
            public void onfailed(Throwable e) {
                removeProgressDialog();
            }
        });
    }

    /**
     * 保存按钮点击事件
     * */
    private View.OnClickListener mSavedBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputPwd.getText().toString();
                String reput = reputPwd.getText().toString();
                if (input.equals("")) {
                    Toast.makeText(mContext, R.string.pwdupdate_error_1, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (reput.equals("")) {
                    Toast.makeText(mContext, R.string.pwdupdate_error_2, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!reput.equals(input)) {
                    Toast.makeText(mContext, R.string.pwdupdate_error_3, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtil.isPassWord(input)) {
                    Toast.makeText(mContext, R.string.login_pwderror, Toast.LENGTH_SHORT).show();
                    return;
                }
                String user = StaticObject.read.getString(StaticObject.USERNAME, "");
                String pwd = StaticObject.read.getString(StaticObject.PASSWORD, "");
                String parm = "MEIP_ACTION = khmap5.action.updatepassword\r\nMEIP_PASSWORD_NEW=" + input;
                update(user, pwd, parm);
            }
        };
    }

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
        this.finish();
    }
}
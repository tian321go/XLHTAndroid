package com.axeac.android.sdk.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.customview.PaintingView;
import com.axeac.android.sdk.ui.Sign;

/**
 * 签名界面Activity
 * @author axeac
 * @version 1.0.0
 * */
public class SignActivity extends Activity {
    /**
     * 时间字符串
     * */
    private String date;
    /**
     * 存储路径
     * */
    private String path;
    /**
     * 文件夹路径
     * */
    private String dir;
    /**
     * PaintingView对象
     * */
    private PaintingView paintingView;
    /**
     * 清除画板按钮
     * */
    private RelativeLayout mClear;
    /**
     * 保存按钮
     * */
    private RelativeLayout mSave;

    private RelativeLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.axeac_activity_sign);
        date = new SimpleDateFormat("yyyMMddHHmmssSSS").format(new Date());
        dir = Environment.getExternalStorageDirectory()+"/KuaiHu/Sign/";
        path = dir+date+".png";
        paintingView = (PaintingView)findViewById(R.id.activity_sign_view);
        mClear = (RelativeLayout) findViewById(R.id.activity_sign_clear);
        mSave = (RelativeLayout)findViewById(R.id.activity_sign_save);
        back = (RelativeLayout)findViewById(R.id.activity_sign_back);
        setResult(50);
        // set up listen to save
        //设置保存监听
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paintingView.getTouched()) {
                    try {
                        // Save the paintingView locally
                        // 将paintingView保存到本地
                        paintingView.save(dir,path);
                        Handler handler = Sign.handlerMap.get(Sign.curPosition);
                        backFuc();
                        if (handler != null) {
                            Message msg = new Message();
                            msg.what = Sign.PAINT_WITH_DATA;
                            msg.obj = path;
                            handler.sendMessage(msg);
                            backFuc();
                        }
                    } catch (IOException e) {
                        Toast.makeText(SignActivity.this, R.string.axeac_toast_exp_sign, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SignActivity.this, R.string.axeac_toast_exp_nonesign, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintingView.clear();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backFuc();
            }
        });

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void backFuc() {
        this.finish();
    }
}

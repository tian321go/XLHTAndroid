package com.axeac.android.sdk.ui.button;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:back to the Home's button
 * 继承SystemButton，事件为返回系统主页。
 * @author axeac
 * @version 1.0.0
 */
public class IndexButton extends SystemButton {

    public IndexButton(Activity ctx) {
        super(ctx);
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
    @Override
    public void setImageAndText(RelativeLayout mLayout, ImageView mImgBtn, TextView mTxtView) {

        mImgBtn.setImageResource(R.drawable.index1);
        mTxtView.setText(R.string.axeac_btn_home);
        super.setImageAndText(mLayout, mImgBtn, mTxtView);
    }

    /**
     * button执行操作
     * */
    @Override
    public void executeBtn() {
        Intent intent = new Intent();
        intent.setAction(StaticObject.INDEX_BUTTON);
        LocalBroadcastManager
                .getInstance(ctx).sendBroadcast(intent);
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
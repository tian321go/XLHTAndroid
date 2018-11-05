package com.axeac.android.sdk.retrofit;

import com.axeac.android.sdk.jhsp.JHSPResponse;
/**
 * 网络请求回调接口
 * @author axeac
 * @version 1.0.0
 * */
public interface OnRequestCallBack {
    void onStart();

    void onCompleted();

    void onSuccesed(JHSPResponse response);

    void onfailed(Throwable e);
}

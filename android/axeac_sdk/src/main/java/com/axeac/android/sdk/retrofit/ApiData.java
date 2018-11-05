package com.axeac.android.sdk.retrofit;


import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface ApiData {

//    @Headers({"Connection: close",
//            "Proxy-Connection: close",
//    })
    @POST("{serverName}")
    Observable<Response<ResponseBody>> request(@Path("serverName") String serverName, @Body RequestBody body);

//    @Headers("Connection: close")
    @POST("CommonServer")
    Observable<Response<ResponseBody>> requestCom(@Body RequestBody body);

}

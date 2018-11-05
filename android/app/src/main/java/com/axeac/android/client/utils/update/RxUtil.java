package com.axeac.android.client.utils.update;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

class RxUtil {
    private Subject<Object, Object> mBus;
    private static RxUtil sInstance;

    private RxUtil() {
        mBus = new SerializedSubject<>(PublishSubject.create());
    }

    /**
     * describe:Get the only instance of RxUtile
     * 描述：获得RxUtile唯一实例
     * */
    static RxUtil getInstance() {
        if (sInstance == null) {
            synchronized (RxUtil.class) {
                if (sInstance == null) {
                    sInstance = new RxUtil();
                }
            }
        }
        return sInstance;
    }

    private Subject<Object, Object> getBus() {
        return mBus;
    }

    static void send(Object obj) {
        if (getInstance().getBus().hasObservers()) {
            getInstance().getBus().onNext(obj);
        }
    }

    static Observable<Object> toObservable() {
        return getInstance().getBus();
    }

    static Observable<KHDownloadProgressEvent> getDownloadEventObservable() {
        return getInstance().toObservable().ofType(KHDownloadProgressEvent.class).observeOn(AndroidSchedulers.mainThread());
    }
}

package com.axeac.android.sdk.utils;

import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;

public class OrmHelper {

    static LiteOrm liteOrm;

    public static LiteOrm getLiteOrm(Context context) {
        if (liteOrm == null) {

            DataBaseConfig config = new DataBaseConfig(context);
            config.dbName = "khmap5.db";
            config.dbVersion = 2;

            // Independent operation
            // 独立操作
            liteOrm = LiteOrm.newSingleInstance(config);
        }
        liteOrm.setDebugged(true); // open the log
        // 打印log信息
        return liteOrm;
    }


}

package com.axeac.android.sdk.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SQLLiteDatabase {

    private static final String DB_NAME = "khmap5.db";
    private static final int DB_VERSION = 2;

    // Network settings information
    //网络设置信息
    public static final String TABLE_NAME_NETINFO = "netinfo";
    public static final String TABLE_NETINFO_SERVERDESC = "serverdesc";
    public static final String TABLE_NETINFO_SERVERURL = "serverurl";
    public static final String TABLE_NETINFO_SERVERNAME = "servername";
    public static final String TABLE_NETINFO_SERVERIP = "serverip";
    public static final String TABLE_NETINFO_HTTPPORT = "httpport";
    public static final String TABLE_NETINFO_ISHTTPS = "serverishttps";
    public static final String TABLE_NETINFO_VPNIP = "vpnip";
    public static final String TABLE_NETINFO_VPNPORT = "vpnport";

    // Image caching information
    //图片缓存信息
    public static final String TABLE_NAME_RESINFO = "resinfo";
    public static final String TABLE_RESINFO_RESID = "resid";
    public static final String TABLE_RESINFO_RESVER = "resver";
    public static final String TABLE_RESINFO_RESDATA = "resdata";

    private Context ctx;
    private SQLiteDatabase db;

    public SQLLiteDatabase(Context ctx) {
        this.ctx = ctx;
    }

    public void open() throws SQLiteException {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(ctx, DB_NAME, null, DB_VERSION);
        try {
            db = dbOpenHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            db = dbOpenHelper.getReadableDatabase();
        }
    }

    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    public boolean isClose() {
        if (db.isOpen()) {
            return false;
        }
        return true;
    }

    private static class DBOpenHelper extends SQLiteOpenHelper {

        public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            Map<String, String> netInfoTable = new HashMap<String, String>();
            netInfoTable.put(TABLE_NETINFO_SERVERDESC, SQLLiteDataType.TEXT);
            netInfoTable.put(TABLE_NETINFO_SERVERURL, SQLLiteDataType.TEXT);
            netInfoTable.put(TABLE_NETINFO_SERVERNAME, SQLLiteDataType.TEXT);
            netInfoTable.put(TABLE_NETINFO_SERVERIP, SQLLiteDataType.TEXT);
            netInfoTable.put(TABLE_NETINFO_HTTPPORT, SQLLiteDataType.TEXT);
            netInfoTable.put(TABLE_NETINFO_VPNIP, SQLLiteDataType.TEXT);
            netInfoTable.put(TABLE_NETINFO_VPNPORT, SQLLiteDataType.TEXT);
            netInfoTable.put(TABLE_NETINFO_ISHTTPS, SQLLiteDataType.TEXT);
            db.execSQL(createTableSql(TABLE_NAME_NETINFO, netInfoTable));

            Map<String, String> resInfoTable = new HashMap<String, String>();
            resInfoTable.put(TABLE_RESINFO_RESID, SQLLiteDataType.TEXT);
            resInfoTable.put(TABLE_RESINFO_RESVER, SQLLiteDataType.TEXT);
            resInfoTable.put(TABLE_RESINFO_RESDATA, SQLLiteDataType.BLOB);
            db.execSQL(createTableSql(TABLE_NAME_RESINFO, resInfoTable));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			db.execSQL("DROP TABLE IF EXISTS netinfo");
//			db.execSQL("DROP TABLE IF EXISTS resinfo");
//			onCreate(db);
            switch (newVersion) {
                case 2:
                    db.execSQL("alter table netinfo ADD COLUMN serverishttps TEXT");
                    break;
            }
        }
    }

    public void insertValueTo(String tableName, Map<String, String> vals) {
        db.execSQL(insertDataToTableSql(tableName, vals));
    }

    public void updateValueFrom(String tableName, Map<String, String> map, Map<String, String> where) {
        db.execSQL(updateDataFromTableSql(tableName, map, where));
    }

    public void deleteValueFrom(String tableName, Map<String, String> where) {
        db.execSQL(deleteDataFromSql(tableName, where));
    }

    public void deleteValueFrom(String tableName, String where) {
        db.execSQL(deleteDataFromSql(tableName, where));
    }

    public Cursor queryValueFrom(String tableName) {
        return queryValueFrom(false, tableName, null, null, null, null, null, "id desc", null);
    }

    public Cursor queryValueFrom(String tableName, String selection) {
        return queryValueFrom(false, tableName, null, selection, null, null, null, null, null);
    }

    public Cursor queryValueFrom(boolean distinct, String tableName,
                                 String[] columns, String selection, String[] selectionArgs,
                                 String groupBy, String having, String orderBy, String limit) {
        try {
            return db.query(distinct, tableName, columns, selection, selectionArgs,
                    groupBy, having, orderBy, limit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String createTableSql(String tableName, Map<String, String> map) {
        if (map == null || map.size() <= 0) {
            return "";
        }
        StringBuffer sql = new StringBuffer("create table ");
        sql.append(tableName).append(" (id ").append(SQLLiteDataType.INTEGER).append(" primary key NOT NULL,");
        Set<String> keys = map.keySet();
        for (String key : keys) {
            sql.append(key).append(" ").append(map.get(key)).append(",");
        }
        return sql.substring(0, sql.length() - 1) + ")";
    }

    private String insertDataToTableSql(String tableName, Map<String, String> map) {
        if (map == null || map.size() <= 0) {
            return "";
        }
        StringBuffer sql = new StringBuffer("insert into ");
        StringBuffer cols = new StringBuffer("(");
        StringBuffer values = new StringBuffer(" values (");
        Set<String> keys = map.keySet();
        for (String key : keys) {
            cols.append(key).append(",");
            values.append(map.get(key)).append(",");
        }
        return sql.append(tableName).append(cols.substring(0, cols.length() - 1) + ")").append(values.substring(0, values.length() - 1) + ")").toString();
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    private String updateDataFromTableSql(String tableName, Map<String, String> map, Map<String, String> where) {
        if (map == null || map.size() <= 0 || where == null || where.size() <= 0) {
            return "";
        }
        StringBuffer sql = new StringBuffer("update ");
        StringBuffer set = new StringBuffer(" set ");
        StringBuffer con = new StringBuffer(" where ");
        Set<String> sets = map.keySet();
        if (sets != null && sets.size() > 0) {
            for (String key : sets) {
                set.append(key).append(" = ").append(map.get(key)).append(",");
            }
        }
        Set<String> cons = where.keySet();
        if (cons != null && cons.size() > 0) {
            for (String key : cons) {
                con.append(key).append(" = ").append(where.get(key)).append(" and ");
            }
        }
        return sql.append(tableName).append(set.substring(0, set.length() - 1)).append(con.substring(0, con.length() - 5)).toString();
    }

    private String deleteDataFromSql(String tableName, Map<String, String> where) {
        if (where == null || where.size() <= 0) {
            return "";
        }
        StringBuffer sql = new StringBuffer("delete from ");
        sql.append(tableName).append(" where ");
        Set<String> keys = where.keySet();
        for (String key : keys) {
            sql.append(key).append(" = ").append(where.get(key)).append(" and ");
        }
        return sql.substring(0, sql.length() - 5);
    }

    private String deleteDataFromSql(String tableName, String where) {
        if (where == null || where.equals("")) {
            return "";
        }
        StringBuffer sql = new StringBuffer("delete from ");
        return sql.append(tableName).append(" where ").append(where).toString();
    }
}
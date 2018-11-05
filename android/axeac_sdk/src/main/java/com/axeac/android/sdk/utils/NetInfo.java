package com.axeac.android.sdk.utils;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 声明网络信息字段
 * @author axeac
 * @version 1.0.0
 * */
@Table("netinfo")
public class NetInfo {
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    /**
     * 网络描述
     * */
    public String serverdesc;
    /**
     * 网络地址
     * */
    public String serverurl;
    /**
     * 网络名称
     * */
    public String servername;
    /**
     * ip地址
     * */
    public String serverip;
    /**
     * 端口号
     * */
    public String httpport;
    /**
     * 是否为https
     * */
    public String serverishttps;
    /**
     * vpn ip地址
     * */
    public String vpnip;
    /**
     * vpn端口号
     * */
    public String vpnport;


}
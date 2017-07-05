package edu.illinois.entm.sawbodeployer.UserActivity;

import java.util.List;

/**
 * Created by Mahsa on 7/4/2017.
 */

public class UserActivities {
    int id;
    String appid;
    String usrid;
    String timestamp;
    String ip;
    String[] GPS;
    String dl_vidID;
    String blue_vidID;
    String wifi_vidID;
    String fb_vidID;
    String other_vidID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getUsrid() {
        return usrid;
    }

    public void setUsrid(String usrid) {
        this.usrid = usrid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String[] getGPS() {
        return GPS;
    }

    public void setGPS(String[] GPS) {
        this.GPS = GPS;
    }

    public String getDl_vidID() {
        return dl_vidID;
    }

    public void setDl_vidID(String dl_vidID) {
        this.dl_vidID = dl_vidID;
    }

    public String getBlue_vidID() {
        return blue_vidID;
    }

    public void setBlue_vidID(String blue_vidID) {
        this.blue_vidID = blue_vidID;
    }

    public String getWifi_vidID() {
        return wifi_vidID;
    }

    public void setWifi_vidID(String wifi_vidID) {
        this.wifi_vidID = wifi_vidID;
    }

    public String getFb_vidID() {
        return fb_vidID;
    }

    public void setFb_vidID(String fb_vidID) {
        this.fb_vidID = fb_vidID;
    }

    public String getOther_vidID() {
        return other_vidID;
    }

    public void setOther_vidID(String other_vidID) {
        this.other_vidID = other_vidID;
    }
}

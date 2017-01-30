package com.example.samsung.woonebo_android.model;

import java.util.Date;

/**
 * Created by SAMSUNG on 2016-11-24.
 */

public class BeaconData {
    private int major;
    private int minor;
    private int rssi;
    private Date lastScan;

    public BeaconData( int major, int minor, int rssi) {
        this.major = major;
        this.minor = minor;
        this.rssi = rssi;
        lastScan = new Date();
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public Date getLastScan() {
        return lastScan;
    }

    public void setLastScan(Date lastScan) {
        this.lastScan = lastScan;
    }
}

package com.example.samsung.woonebo_android.scan;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.samsung.woonebo_android.R;
import com.example.samsung.woonebo_android.activity.LoginActivity;
import com.example.samsung.woonebo_android.model.BeaconData;
import com.example.samsung.woonebo_android.model.BeaconDataList;

import java.util.ArrayList;

/**
 * Created by SAMSUNG on 2016-11-28.
 */
public class DeviceScan extends Application {
    private static final String LOG_TAG = "MainActivity";
    private BluetoothManager btManager;
    public static BluetoothAdapter btAdapter;
    private Handler scanHandler = new Handler();
    private int scan_interval_ms = 1000;
    private boolean isScanning = false;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static BeaconData beaconData;
    public static Location location;
    public static Location lastLocation;
    private static BeaconDataList beaconDataList;

    @Override
    public void onCreate() {
        super.onCreate();

        setBeaconConfig();  //비콘 데이터 가져오기
        setGPSConfig(); //GPS 데이터 가져오기
    }

    private void setBeaconConfig() {
        // init BLE
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();

        beaconDataList = new BeaconDataList();

        scanHandler.post(scanRunnable);
    }

    private void setGPSConfig() {
        //권한 설정
        checkPermission();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        GPSListener gpsListener = new GPSListener();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);    //1초, 0m, GPS를 통한 위치 요청
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, gpsListener);    //Network를 통한 위치 요청

        // 위치 확인이 안되는 경우에도 최근에 확인된 위치 정보 먼저 확인
        lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            if (isScanning) {
                if (btAdapter != null) {
                    btAdapter.stopLeScan(leScanCallback);
                }
            }
            else {
                if (btAdapter != null) {
                    btAdapter.startLeScan(leScanCallback);
                }
            }
            isScanning = !isScanning;

            scanHandler.postDelayed(this, scan_interval_ms);
        }
    };

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            int startByte = 2;
            boolean patternFound = false;
            while (startByte <= 5) {
                if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                        ((int) scanRecord[startByte + 3] & 0xff) == 0x15)
                { //Identifies correct data length
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound) {
                //Convert to hex String
                byte[] uuidBytes = new byte[16];
                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                String hexString = bytesToHex(uuidBytes);

                //UUID detection
                String uuid =  hexString.substring(0,8) + "-" +
                        hexString.substring(8,12) + "-" +
                        hexString.substring(12,16) + "-" +
                        hexString.substring(16,20) + "-" +
                        hexString.substring(20,32);

                // major
                final int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

                // minor
                final int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

                if (uuid.equals(getApplicationContext().getString(R.string.uuid).toString())) {
                    beaconData = new BeaconData(major, minor, rssi);
                    beaconDataList.setBeacon(beaconData);
                }

                Log.i(LOG_TAG,"UUID: " +uuid + "\\nmajor: " +major +"\\nminor" +minor);
            }
        }
    };

    public static ArrayList<BeaconData> getBLE() {
        ArrayList<BeaconData> beaconDatas = null;
        if(beaconDataList != null)
            beaconDatas = beaconDataList.nearBeacon();

        if (beaconDatas != null)
            return beaconDatas; //현재 계속 최신걸로 유지
        else
            return null;
    }

    public static int getBLECount() {
        if(beaconDataList != null)
            return beaconDataList.nearBeacon().size();
        return 0;
    }

    private class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(Location newLocation) {
            location = newLocation;

            String msg = "Location!!!!!!! Latitude : "+ newLocation.getLatitude() + "\nLongitude:"+ newLocation.getLongitude();
            Log.i("GPSListener", msg);
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override
        public void onProviderEnabled(String s) {}
        @Override
        public void onProviderDisabled(String s) {}
    }
    /**
     * bytesToHex method
     */
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }
    private void checkPermission() {
        //권한x
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getBaseContext(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }
}

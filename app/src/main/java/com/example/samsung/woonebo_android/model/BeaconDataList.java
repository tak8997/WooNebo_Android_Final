package com.example.samsung.woonebo_android.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by SAMSUNG on 2016-11-28.
 */

public class BeaconDataList {
    private ArrayList<BeaconData> beaconDatas;

    public BeaconDataList() {
        beaconDatas = new ArrayList<>();
    }

    public void setBeacon(BeaconData beaconData) {
        int index = matchBeacon(beaconData);
        if(index != -1) {
            beaconDatas.set(index, beaconData);
        } else {
            beaconDatas.add(beaconData);
        }
    }

    private int matchBeacon(BeaconData beaconData) {
        for (int i=0; i<beaconDatas.size(); i++) {
            if ((beaconDatas.get(i).getMajor() == beaconData.getMajor()) && (beaconDatas.get(i).getMinor() == beaconData.getMinor())) {
                return i;
            }
        }
        return -1;  //beaconDatas에 같은게 없으면 add()
    }

    public ArrayList<BeaconData> nearBeacon() {
        //beaconDatas의 비콘들 중에서 Rssi 기준 오름차순 정렬
        RssiComparator rssiComparator = new RssiComparator();
        Collections.sort(beaconDatas, rssiComparator);

        return beaconDatas;
    }

    private class RssiComparator implements Comparator<BeaconData> {
        @Override
        public int compare(BeaconData firstBeacon, BeaconData nextBeacon) {
            int firstRssi = firstBeacon.getRssi();
            int nextRssi = nextBeacon.getRssi();

            if(firstRssi > nextRssi)
                return 1;
            else if(firstRssi < nextRssi)
                return -1;
            else
                return 0;
        }
    }

//    public BeaconData nearBeacon() {
//        int rssi = -100;
//        int index = -1;
//
//        for (int i=0; i<beaconDatas.size(); i++) {
//            int nextRssi = beaconDatas.get(i).getRssi();
//
//            if (nextRssi > rssi) {
////                if ((new Date().getSeconds() - beaconDatas.get(i).getLastScan().getSeconds()) < 3) {
//                    rssi = nextRssi;
//                    index = i;
////                }
//            }
//
//        }
//
//        if (index == -1)
//            return null;
//        else
//            return beaconDatas.get(index);
//    }
}

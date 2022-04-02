package com.example.kickport;

import android.location.Location;

public class SpeedTracker {
    public double mySpeed;

    public double getMySpeed(Location location) {
//        3.6 을 곱해서 km/h로 변환
        mySpeed = location.getSpeed() * 3.6;
        return mySpeed;
    }
}

package com.ice.skininnerdemo;

import android.graphics.Bitmap;

/**
 * 交通类型实体类</br>
 * 包含 交通工具名 和 交通图标Bitmap对象
 * Created by ice on 14-10-11.
 */
public class TrafficType {

    private String trafficName;

    private Bitmap trafficBitmap;

    public String getTrafficName() {
        return trafficName;
    }

    public void setTrafficName(String trafficName) {
        this.trafficName = trafficName;
    }

    public Bitmap getTrafficBitmap() {
        return trafficBitmap;
    }

    public void setTrafficBitmap(Bitmap trafficBitmap) {
        this.trafficBitmap = trafficBitmap;
    }
}

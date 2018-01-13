package com.peng.tinyweather.gson;

/**
 * Created by peng on 2018/1/13.
 */

public class AQI {
    public  AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}

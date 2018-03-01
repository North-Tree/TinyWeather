package com.peng.tinyweather.data;

/**
 * Created by peng on 2018/2/4.
 */

import com.peng.tinyweather.gson.AQI;
import com.peng.tinyweather.gson.Weather;

/**
 * 数据源接口
 */
public interface DataSource {
    // 天气加载完成回调
    interface LoadWeatherDataCallback {
        void onWeatherDataLoaded(Weather weather);
        void onWeatherDataNotAvailable();
    }

    // 空气质量信息加载完成回调
    interface LoadAQIDataCallback {
        void onAQIDataLoaded(AQI aqi);
        void onAQIDataNotAvailable();
    }

    void loadWeatherData(String city, LoadWeatherDataCallback callback);

    void loadAQIData(String city, LoadAQIDataCallback callback);

    void saveWeatherData(String city, String weatherString);

    void saveAQIData(String city, String aqiString);
}

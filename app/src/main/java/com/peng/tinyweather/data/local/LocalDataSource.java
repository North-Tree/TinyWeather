package com.peng.tinyweather.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.peng.tinyweather.data.DataSource;
import com.peng.tinyweather.gson.AQI;
import com.peng.tinyweather.gson.Weather;
import com.peng.tinyweather.util.Utility;

/**
 * Created by peng on 2018/2/4.
 */

public class LocalDataSource implements DataSource {
    private Context mCtx;

    public LocalDataSource (Context context) {
        mCtx = context;
    }

    @Override
    public void loadWeatherData(String city, LoadWeatherDataCallback callback) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            callback.onWeatherDataLoaded(Utility.handleHeAPIResponse(weatherString, Weather.class));
        } else {
            callback.onWeatherDataNotAvailable();
        }
    }

    @Override
    public void loadAQIData(String city, LoadAQIDataCallback callback) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
        String aqiString = prefs.getString("aqi", null);
        if (aqiString != null) {
            callback.onAQIDataLoaded(Utility.handleHeAPIResponse(aqiString, AQI.class));
        } else {
            callback.onAQIDataNotAvailable();
        }
    }

    @Override
    public void saveWeatherData(String city, String weatherString) {
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(mCtx).edit();
        editor.putString("weather", weatherString);
        editor.apply();
    }

    @Override
    public void saveAQIData(String city, String aqiString) {
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(mCtx).edit();
        editor.putString("aqi", aqiString);
        editor.apply();
    }
}

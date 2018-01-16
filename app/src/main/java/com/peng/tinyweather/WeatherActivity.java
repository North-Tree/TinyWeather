package com.peng.tinyweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.peng.tinyweather.gson.AQI;
import com.peng.tinyweather.gson.Forecast;
import com.peng.tinyweather.gson.Suggestion;
import com.peng.tinyweather.gson.Weather;
import com.peng.tinyweather.util.HttpUtil;
import com.peng.tinyweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherScrollView;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private LinearLayout suggestionLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private ImageView bingImg;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    private String mCurrentCityName;

    public DrawerLayout mDrawerLayout;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        weatherScrollView = findViewById(R.id.sv_weather);
        titleCity = findViewById(R.id.tv_title_city);
        titleUpdateTime = findViewById(R.id.tv_title_update_time);
        degreeText = findViewById(R.id.tv_degree);
        weatherInfoText = findViewById(R.id.tv_weather_info);
        forecastLayout = findViewById(R.id.layout_forecast);
        suggestionLayout = findViewById(R.id.layout_suggestion);
        aqiText = findViewById(R.id.tv_aqi);
        pm25Text = findViewById(R.id.tv_pm25);
        bingImg = findViewById(R.id.img_bing);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.btn_nav);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        String aqiString = prefs.getString("aqi", null);
        if (weatherString != null && aqiString != null) {
            // 有缓存时直接解析天气数据和AQI数据
            Weather weather = Utility.handleHeAPIResponse(weatherString, Weather.class);
            mCurrentCityName = weather.basic.cityName;
            AQI aqi = Utility.handleHeAPIResponse(aqiString, AQI.class);
            showWeatherInfo(weather);
            showAQIInfo(aqi);
        } else {
            // 无缓存时去服务器查询天气
            mCurrentCityName = getIntent().getStringExtra("county_name");
            weatherScrollView.setVisibility(View.INVISIBLE);
            requestWeather(mCurrentCityName);
            requestAQI(mCurrentCityName);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mCurrentCityName);
                requestAQI(mCurrentCityName);
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Glide加载背景图
        String bingPicUrl = prefs.getString("bing_pic", null);
        if (bingPicUrl == null) {
            Glide.with(this).load(bingPicUrl).into(bingImg);
        } else {
            loadBingPicPathFromGuolinAPI();
        }
    }

    public void requestWeather(final String countyName) {
        String weatherUrl = "https://free-api.heweather.com/s6/weather?" +
                "key=34fcb36bcc8a42d2b0fe9b549cce8f8c&location=" + countyName;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,
                                "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleHeAPIResponse(responseText, Weather.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor =
                                    PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                            mCurrentCityName = countyName;
                        } else {
                            Toast.makeText(WeatherActivity.this,
                                    "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPicPathFromGuolinAPI();
    }

    public void requestAQI(final String countyName) {
        String aqiUrl = "https://free-api.heweather.com/s6/air/now?" +
                "key=34fcb36bcc8a42d2b0fe9b549cce8f8c&location=" + countyName;
        HttpUtil.sendOkHttpRequest(aqiUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,
                                "获取空气质量信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final AQI aqi = Utility.handleHeAPIResponse(responseText, AQI.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (aqi != null && "ok".equals(aqi.status)) {
                            SharedPreferences.Editor editor =
                                    PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("aqi", responseText);
                            editor.apply();
                            showAQIInfo(aqi);
                        } else {
                            Toast.makeText(WeatherActivity.this,
                                    "获取空气质量信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.update.localUpdateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.weatherInfo;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            // to learn
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.tv_date);
            TextView infoText = view.findViewById(R.id.tv_info);
            TextView maxText = view.findViewById(R.id.tv_max);
            TextView minText = view.findViewById(R.id.tv_min);
            dateText.setText(forecast.date);
            infoText.setText(forecast.weatherInfoDay);
            maxText.setText(forecast.maxTemper);
            minText.setText(forecast.minTemper);
            forecastLayout.addView(view);
        }

        for (Suggestion suggestion : weather.suggestionList) {
            // to learn
            View view = LayoutInflater.from(this).inflate(R.layout.suggestion_item, suggestionLayout, false);
            TextView indexText = view.findViewById(R.id.tv_index);
            TextView suggestionText = view.findViewById(R.id.tv_suggestion);
            indexText.setText("程度 : " + suggestion.level);
            suggestionText.setText("建议 : " + suggestion.suggestionInfo);
            suggestionLayout.addView(view);
        }
        weatherScrollView.setVisibility(View.VISIBLE);
    }

    private void showAQIInfo(AQI aqi) {
        aqiText.setText(aqi.aqiNowInfo.aqi);
        pm25Text.setText(aqi.aqiNowInfo.pm25);
    }

    /**
     * 从郭霖提供的API获取bing的每日一图
     */
    private void loadBingPicPathFromGuolinAPI() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            Glide.with(WeatherActivity.this).load(bingPic).into(bingImg);
                    }
                });
            }
        });
    }
}

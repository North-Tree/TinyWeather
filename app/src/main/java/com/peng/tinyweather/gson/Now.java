package com.peng.tinyweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peng on 2018/1/13.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond_txt")
    public String weatherInfo;
}

package com.peng.tinyweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peng on 2018/1/13.
 */

public class Basic {
    @SerializedName("location")
    public String cityName;

    @SerializedName("cid")
    public String cityId;
}

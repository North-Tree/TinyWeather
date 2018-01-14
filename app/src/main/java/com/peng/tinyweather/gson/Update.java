package com.peng.tinyweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peng on 2018/1/14.
 */

public class Update {
    @SerializedName("loc")
    public String localUpdateTime;

    @SerializedName("utc")
    public String utcUpdateTime;
}

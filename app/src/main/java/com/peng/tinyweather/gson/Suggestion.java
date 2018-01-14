package com.peng.tinyweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by peng on 2018/1/13.
 */

public class Suggestion {
    @SerializedName("brf")
    public String level;

    @SerializedName("txt")
    public String suggestionInfo;

    public String type;
}

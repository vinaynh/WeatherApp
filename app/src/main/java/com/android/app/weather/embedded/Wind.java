package com.android.app.weather.embedded;

import androidx.room.ColumnInfo;

public class Wind {

    @ColumnInfo(name = "wind_speed_value")
    public String mSpeedValue;

    @ColumnInfo(name = "wind_speed_unit")
    public String mSpeedUnit;

    @ColumnInfo(name = "wind_speed_name")
    public String mSpeedName;

    @ColumnInfo(name = "wind_gust_value")
    public String mGustValue;

    @ColumnInfo(name = "wind_gust_unit")
    public String mGustUnit;

    @ColumnInfo(name = "wind_direction_value")
    public String mDirectionValue;

    @ColumnInfo(name = "wind_direction_code")
    public String mDirectionCode;

    @ColumnInfo(name = "wind_direction_name")
    public String mDirectionName;

    public Wind(String mSpeedValue, String mSpeedUnit, String mSpeedName, String mValue, String mGustUnit, String mDirectionValue, String mDirectionCode, String mDirectionName) {
        this.mSpeedValue = mSpeedValue;
        this.mSpeedUnit = mSpeedUnit;
        this.mSpeedName = mSpeedName;
        this.mGustValue = mValue;
        this.mGustUnit = mGustUnit;
        this.mDirectionValue = mDirectionValue;
        this.mDirectionCode = mDirectionCode;
        this.mDirectionName = mDirectionName;
    }

    public Wind() {

    }
}

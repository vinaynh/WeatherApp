package com.android.app.weather.embedded;

import androidx.room.ColumnInfo;

// LastUpdate class will be @embedded in WeatherUser
public class LastUpdate {

    @ColumnInfo(name = "lastUpdate_value")
    public String mLastUpdateValue;

    public LastUpdate(String lastUpdateValue) {
        this.mLastUpdateValue = lastUpdateValue;
    }
}

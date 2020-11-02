package com.android.app.weather.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.android.app.weather.embedded.Weather;
import com.android.app.weather.entities.WeatherUser;

@Dao
public interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WeatherUser weather);

    @Query("SELECT * FROM Weather WHERE :latitude LIKE (lat || '%')  AND :longtitude LIKE (lon || '%')  LIMIT 1")
    WeatherUser getWeatherInfo(String latitude, String longtitude);


}

package com.android.app.weather.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.android.app.weather.dao.WeatherDao;
import com.android.app.weather.entities.WeatherUser;


@Database(entities = {WeatherUser.class}, version = 1)
public abstract class WeatherDb extends RoomDatabase {

    private static final String DB_NAME = "weather";

    public abstract WeatherDao weatherDao();

    public static WeatherDb sWeatherDb;

    public static WeatherDb getInstance(Context context) {
        if (sWeatherDb == null) {
            sWeatherDb = Room.databaseBuilder(context,
                    WeatherDb.class, DB_NAME).build();
        }
        return sWeatherDb;
    }

    public WeatherDao getDao() {
        return weatherDao();
    }
}
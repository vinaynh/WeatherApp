package com.android.app.weather.embedded;

import androidx.room.ColumnInfo;

// City class will be @embedded in WeatherUser
public class City {

    @ColumnInfo(name = "city_cityid")
    public String mCityId;

    @ColumnInfo(name = "city_name")
    public String mCityName;

    @ColumnInfo(name = "city_lon")
    public String mLongitude;

    @ColumnInfo(name = "city_lat")
    public String mLatitude;

    @ColumnInfo(name = "city_country")
    public String mCountry;

    @ColumnInfo(name = "city_timezone")
    public String mTimeZone;

    @ColumnInfo(name = "city_rise")
    public String mSunRise;

    @ColumnInfo(name = "city_set")
    public String mSunSet;

    public City(String cityId, String cityName, String longitude, String latitude, String country, String timeZone, String sunRise, String sunSet) {
        mCityId = cityId;
        mCityName = cityName;
        mLongitude = longitude;
        mLatitude = latitude;
        mCountry = country;
        mTimeZone = timeZone;
        mSunRise = sunRise;
        mSunSet = sunSet;
    }

}

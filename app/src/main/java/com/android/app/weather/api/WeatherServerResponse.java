package com.android.app.weather.api;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

//Example Response start
/*<current>
<city id="0" name="Mountain View">
<coord lon="-122.09" lat="37.39" />
<country>US</country>
<timezone>-28800</timezone>
<sun rise="2020-01-07T15:22:59" set="2020-01-08T01:05:37" />
</city>
<temperature value="278.07" min="273.15" max="282.59" unit="kelvin" />
<feels_like value="275.88" unit="kelvin" />
<humidity value="86" unit="%" />
<pressure value="1026" unit="hPa" />
<wind>
<speed value="0.93" unit="m/s" name="Calm" />
<gusts />
<direction value="23" code="NNE" name="North-northeast" />
</wind>
<clouds value="1" name="clear sky" />
<visibility value="16093" />
<precipitation mode="no" />
<weather number="800" value="clear sky" icon="01n" />
<lastupdate value="2020-01-07T11:33:40" />
</current>
//Example Response end
*/

@Root(name = "current" ,strict = false)
public class WeatherServerResponse {

    @Element(name = "city")
    public CityResponse mCity;

    public static class CityResponse {

        @Attribute(name = "id")
        public String mId;

        @Attribute(name = "name")
        public String mName;

        @Element(name = "coord", required = false)
        public CordinateResponse mCord;

        public static class CordinateResponse {

            @Attribute(name = "lon")
            public String mLongitude;

            @Attribute(name = "lat")
            public String mLatitude;
        }

        @Element(name = "country")
        public String mCountry;

        @Element(name = "timezone", required = false)
        public String mTimeZone;

        @Element(name = "sun", required = false)
        public SunResponse mSun;

        public static class SunResponse {

            @Attribute(name = "rise")
            public String mSunRise;

            @Attribute(name = "set")
            public String mSunSet;
        }

        public String getId() {
            return mId;
        }
    }

    public CityResponse getCity() {
        return mCity;
    }

    @Element(name = "temperature")
    public TemperatureResponse mTemperature;

    public static class TemperatureResponse {

        @Attribute(name = "value")
        public String mCurrentTemp;

        @Attribute(name = "min")
        public String mMaxTemp;

        @Attribute(name = "max")
        public String mMinTemp;

        @Attribute(name = "unit")
        public String mUnit;
    }

    @Element(name = "feels_like")
    public FeelsLikeResponse mFeelsLike;

    public static class FeelsLikeResponse {

        @Attribute(name = "value")
        public String mValue;

        @Attribute(name = "unit")
        public String mUnit;

    }

    @Element(name = "humidity", required = false)
    public HumidityResponse mHumidity;

    public static class HumidityResponse {

        @Attribute(name = "value")
        public String mValue;

        @Attribute(name = "unit")
        public String mUnit;

    }

    @Element(name = "pressure", required = false)
    public PressureResponse mPressure;

    public static class PressureResponse {

        @Attribute(name = "value")
        public String mValue;

        @Attribute(name = "unit")
        public String mUnit;

    }

    @Element(name = "wind", required = false)
    public WindResponse mWind;

    public static class WindResponse {
        @Element(name = "speed")
        public Speed mSpeed;

        public static class Speed {

            @Attribute(name = "value")
            public String mValue;

            @Attribute(name = "unit")
            public String mUnit;

            @Attribute(name = "name")
            public String mName;

        }

        @Element(name = "gust", required = false)
        public Gust mGust;

        public static class Gust {

            @Attribute(name = "value", required = false)
            public String mValue;

            @Attribute(name = "unit", required = false)
            public String mUnit;

        }

        @Element(name = "direction", required = false)
        public Direction mDirection;

        public static class Direction {

            @Attribute(name = "value")
            public String mValue;

            @Attribute(name = "code")
            public String mCode;

            @Attribute(name = "name")
            public String mName;

        }
    }

    @Element(name = "clouds", required = false)
    public CloudResponse mCloud;


    public static class CloudResponse {

        @Attribute(name = "value")
        public String mValue;

        @Attribute(name = "name")
        public String mName;

    }

    @Element(name = "visibility", required = false)
    public visibilityResponse mVisibility;


    public static class visibilityResponse {

        @Attribute(name = "value")
        public String mValue;


    }

    @Element(name = "precipitation", required = false)
    public precipitationResponse mPrecipitation;


    public static class precipitationResponse {

        @Attribute(name = "mode")
        public String mMode;


    }

    @Element(name = "weather", required = false)
    public WeatherResponse mWeather;

    public static class WeatherResponse {

        @Attribute(name = "number")
        public String mNumber;

        @Attribute(name = "value")
        public String mValue;

        @Attribute(name = "icon")
        public String mIconId;

    }

    @Element(name = "lastupdate", required = false)
    public LastUpdateResponse mLastUpdate;

    public static class LastUpdateResponse {

        @Attribute(name = "value")
        public String mValue;
    }

}

package com.algee.airportweather.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Stores data from the GeoNames ICAO API.
 */
public class WeatherReport implements Parcelable
{
    private Date    mTime; // UTC Time
    private String  mClouds,
                    mStationName,
                    mStationCode, // ICAO
                    mCountryCode,
                    mTemperature, // celsius // float string
                    mWindSpeed, // knots
                    mWeatherCondition,
                    mDewPoint,
                    mCloudsCode;

    private double  mWindDirection,
                    mSeaLevelPressure;

    private int     mElevation, // meters
                    mHumidity;

    private GeoCoordinates mGeoCoordinates;
    private Observation mObservation;
    private ErrorStatus mErrorStatus;


    public WeatherReport()
    {
    }

    public String getCloudsCode()
    {
        return mCloudsCode;
    }

    public void setCloudsCode(String cloudsCode)
    {
        mCloudsCode = cloudsCode;
    }

    public Date getTime()
    {
        return mTime;
    }

    public void setTime(Date time)
    {
        mTime = time;
    }

    public String getClouds()
    {
        return mClouds;
    }

    public void setClouds(String clouds)
    {
        mClouds = clouds;
    }

    public String getStationName()
    {
        return mStationName;
    }

    public void setStationName(String stationName)
    {
        mStationName = stationName;
    }

    public String getStationCode()
    {
        return mStationCode;
    }

    public void setStationCode(String stationCode)
    {
        mStationCode = stationCode;
    }

    public String getCountryCode()
    {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode)
    {
        mCountryCode = countryCode;
    }

    /**
     * A string that can be easily converted to a float value.  Represented in Celcius.
     * @return
     */
    public String getTemperature()
    {
        return mTemperature;
    }

    public void setTemperature(String temperature)
    {
        mTemperature = temperature;
    }

    /**
     * The wind speed in knots. :/
     * @return
     */
    public String getWindSpeed()
    {
        return mWindSpeed;
    }

    public void setWindSpeed(String windSpeed)
    {
        mWindSpeed = windSpeed;
    }

    public String getWeatherCondition()
    {
        return mWeatherCondition;
    }

    public void setWeatherCondition(String weatherCondition)
    {
        mWeatherCondition = weatherCondition;
    }

    /**
     * Direction in degrees, most-likely a positive value (0-359)
     */
    public double getWindDirection()
    {
        return mWindDirection;
    }

    public void setWindDirection(double windDirection)
    {
        mWindDirection = windDirection;
    }

    public double getSeaLevelPressure()
    {
        return mSeaLevelPressure;
    }

    public void setSeaLevelPressure(double seaLevelPressure)
    {
        mSeaLevelPressure = seaLevelPressure;
    }

    public String getDewPoint()
    {
        return mDewPoint;
    }

    public void setDewPoint(String dewPoint)
    {
        mDewPoint = dewPoint;
    }

    /**
     * Elevation in meters.
     */
    public int getElevation()
    {
        return mElevation;
    }

    public void setElevation(int elevation)
    {
        mElevation = elevation;
    }

    public int getHumidity()
    {
        return mHumidity;
    }

    public void setHumidity(int humidity)
    {
        mHumidity = humidity;
    }

    public GeoCoordinates getGeoCoordinates()
    {
        return mGeoCoordinates;
    }

    public void setGeoCoordinates(GeoCoordinates geoCoordinates)
    {
        mGeoCoordinates = geoCoordinates;
    }

    public Observation getObservation()
    {
        return mObservation;
    }

    public void setObservation(Observation observation)
    {
        mObservation = observation;
    }

    public ErrorStatus getErrorStatus()
    {
        return mErrorStatus;
    }

    public void setErrorStatus(ErrorStatus errorStatus)
    {
        mErrorStatus = errorStatus;
    }

    public boolean hasErrorStatus()
    {
        return mErrorStatus != null;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeSerializable(mTime);
        dest.writeString(mClouds);
        dest.writeString(mStationName);
        dest.writeString(mCountryCode);
        dest.writeString(mTemperature);
        dest.writeString(mWindSpeed);
        dest.writeString(mWeatherCondition);
        dest.writeString(mDewPoint);
        dest.writeString(mCloudsCode);
        dest.writeDouble(mWindDirection);
        dest.writeDouble(mSeaLevelPressure);
        dest.writeInt(mElevation);
        dest.writeInt(mHumidity);
        dest.writeParcelable(mGeoCoordinates, flags);
        dest.writeParcelable(mObservation, flags);
        dest.writeParcelable(mErrorStatus, flags);
    }

    private WeatherReport(Parcel source)
    {
        mTime = (Date) source.readSerializable();
        mClouds = source.readString();
        mStationName = source.readString();
        mCountryCode = source.readString();
        mTemperature = source.readString();
        mWindSpeed = source.readString();
        mWeatherCondition = source.readString();
        mDewPoint = source.readString();
        mCloudsCode = source.readString();
        mWindDirection = source.readDouble();
        mSeaLevelPressure = source.readDouble();
        mElevation = source.readInt();
        mHumidity = source.readInt();
        mGeoCoordinates = source.readParcelable(GeoCoordinates.class.getClassLoader());
        mObservation = source.readParcelable(Observation.class.getClassLoader());
        mErrorStatus = source.readParcelable(ErrorStatus.class.getClassLoader());
    }

    public static final Creator<WeatherReport> CREATOR = new Creator<WeatherReport>()
    {
        @Override
        public WeatherReport createFromParcel(Parcel source)
        {
            return new WeatherReport(source);
        }

        @Override
        public WeatherReport[] newArray(int size)
        {
            return new WeatherReport[size];
        }
    };


}

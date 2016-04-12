package com.algee.airportweather.api;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * A simple class that stores latitude and longitude values as a 'double' data type.
 */
public class GeoCoordinates implements Parcelable
{
    private double mLatitude,
            mLongitude;

    public GeoCoordinates(double latitude, double longitude)
    {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public double getLatitude()
    {
        return mLatitude;
    }

    public void setLatitude(double latitude)
    {
        mLatitude = latitude;
    }

    public double getLongitude()
    {
        return mLongitude;
    }

    public void setLongitude(double longitude)
    {
        mLongitude = longitude;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
    }

    private GeoCoordinates(Parcel source)
    {
        mLatitude = source.readDouble();
        mLongitude = source.readDouble();
    }

    public static final Creator<GeoCoordinates> CREATOR = new Creator<GeoCoordinates>()
    {
        @Override
        public GeoCoordinates createFromParcel(Parcel source)
        {
            return new GeoCoordinates(source);
        }

        @Override
        public GeoCoordinates[] newArray(int size)
        {
            return new GeoCoordinates[size];
        }
    };
}

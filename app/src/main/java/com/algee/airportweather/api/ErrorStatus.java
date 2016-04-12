package com.algee.airportweather.api;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stores a message and an integer value for an error status that could be sent from the geonames
 * api.
 */
public class ErrorStatus implements Parcelable
{
    private String mMessage;
    private int mValue;

    public ErrorStatus(String message, int value)
    {
        mMessage = message;
        mValue = value;
    }

    public String getMessage()
    {
        return mMessage;
    }

    public void setMessage(String message)
    {
        mMessage = message;
    }

    public int getValue()
    {
        return mValue;
    }

    public void setValue(int value)
    {
        mValue = value;
    }

    @Override
    public String toString()
    {
        return mMessage + "(" + mValue + ")";
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    private ErrorStatus(Parcel source)
    {
        mMessage = source.readString();
        mValue = source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mMessage);
        dest.writeInt(mValue);
    }

    public static final Creator<ErrorStatus> CREATOR = new Creator<ErrorStatus>()
    {
        @Override
        public ErrorStatus createFromParcel(Parcel source)
        {
            return new ErrorStatus(source);
        }

        @Override
        public ErrorStatus[] newArray(int size)
        {
            return new ErrorStatus[size];
        }
    };

}

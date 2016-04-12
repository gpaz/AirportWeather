package com.algee.airportweather.icao;

/**
 * Class object that contains airport details base on the data given by each line in the
 * 'airports.csv' file in the res/raw diredtory.
 */
public class Airport
{
    public static final char    DST_EUROPE              = 'E',
                                DST_AMERICA_AND_CANADA  = 'A',
                                DST_SOUTH_AMERICA       = 'S',
                                DST_AUSTRALIA           = 'O',
                                DST_NEW_ZEALAND         = 'Z',
                                DST_NONE                = 'N',
                                DST_UNKNOWN             = 'U';

    private int mId;
    private String mName;
    private String mCity;
    private String mCountry;
    private String mIATACode;
    private String mICAOCode;
    private double mLatitude;
    private double mLongitude;
    private int mAltitude;
    private double mTimezoneOffset; // from UTC
    private char mDST;
    private String mTimezone; // tx (Olson) format, eg. "America/Lost_Angeles"

    public Airport()
    {
    }

    public int getId()
    {
        return mId;
    }

    public void setId(int id)
    {
        mId = id;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public String getCity()
    {
        return mCity;
    }

    public void setCity(String city)
    {
        mCity = city;
    }

    public String getCountry()
    {
        return mCountry;
    }

    public void setCountry(String country)
    {
        mCountry = country;
    }

    public String getIATACode()
    {
        return mIATACode;
    }

    public void setIATACode(String IATACode)
    {
        mIATACode = IATACode;
    }

    public String getICAOCode()
    {
        return mICAOCode;
    }

    public void setICAOCode(String ICAOCode)
    {
        mICAOCode = ICAOCode;
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

    public int getAltitude()
    {
        return mAltitude;
    }

    public void setAltitude(int altitude)
    {
        mAltitude = altitude;
    }

    public double getTimezoneOffset()
    {
        return mTimezoneOffset;
    }

    public void setTimezoneOffset(double timezoneOffset)
    {
        mTimezoneOffset = timezoneOffset;
    }

    public char getDST()
    {
        return mDST;
    }

    public void setDST(char DST)
    {
        mDST = DST;
    }

    public String getTimezone()
    {
        return mTimezone;
    }

    public void setTimezone(String timezone)
    {
        mTimezone = timezone;
    }
}

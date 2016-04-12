package com.algee.airportweather.icao;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Encapsulating SharedPreferences, persists data for the app.
 */
public class ICAOPreferences
{
    private static final String
            KEY_ID          = "id",
            KEY_NAME        = "name",
            KEY_CITY        = "city",
            KEY_COUNTRY     = "country",
            KEY_IATA        = "iata",
            KEY_ICAO        = "icao",
            KEY_LAT         = "lat",
            KEY_LNG         = "lng",
            KEY_ALT         = "alt",
            KEY_TZOFFSET    = "tzoffset",
            KEY_DST         = "dst",
            KEY_TZ          = "tz";


    private final Context mContext;
    private final SharedPreferences mPreferences;



    public ICAOPreferences(Context c)
    {
        mContext = c;
        mPreferences = mContext.getSharedPreferences("icao", Context.MODE_PRIVATE);
    }

    private static final String EMPTY_STRING = "";
    private static final String ZERO_DBL = "0.0";

    public Airport getMostRecentSelectedAirport()
    {
        Airport airport = null;
        if(mPreferences.contains(KEY_ID))
        {
            airport = new Airport();
            airport.setId(mPreferences.getInt(KEY_ID, -1));
            airport.setName(mPreferences.getString(KEY_NAME, EMPTY_STRING));
            airport.setCity(mPreferences.getString(KEY_CITY, EMPTY_STRING));
            airport.setCountry(mPreferences.getString(KEY_COUNTRY, EMPTY_STRING));
            airport.setIATACode(mPreferences.getString(KEY_IATA, EMPTY_STRING));
            airport.setICAOCode(mPreferences.getString(KEY_ICAO, EMPTY_STRING));
            airport.setLatitude(Double.parseDouble(mPreferences.getString(KEY_LAT, ZERO_DBL)));
            airport.setLongitude(Double.parseDouble(mPreferences.getString(KEY_LNG, ZERO_DBL)));
            airport.setAltitude(mPreferences.getInt(KEY_ALT, 0));
            airport.setTimezoneOffset(Double.parseDouble(mPreferences.getString(KEY_TZOFFSET, ZERO_DBL)));
            airport.setDST(mPreferences.getString(KEY_DST, "" + Airport.DST_UNKNOWN).charAt(0));
            airport.setTimezone(mPreferences.getString(KEY_TZ, EMPTY_STRING));
        }
        return airport;
    }

    public void setMostRecentSelectedAirport(Airport airport)
    {
        final SharedPreferences.Editor editor = mPreferences.edit();
        if(airport == null)
            editor.remove(KEY_ID);
        else
        {
            editor.putInt(KEY_ID, airport.getId());
            editor.putString(KEY_NAME, airport.getName());
            editor.putString(KEY_CITY, airport.getCity());
            editor.putString(KEY_COUNTRY, airport.getCountry());
            editor.putString(KEY_IATA, airport.getIATACode());
            editor.putString(KEY_ICAO, airport.getICAOCode());
            editor.putString(KEY_LAT, Double.toString(airport.getLatitude()));
            editor.putString(KEY_LNG, Double.toString(airport.getLongitude()));
            editor.putInt(KEY_ALT, airport.getAltitude());
            editor.putString(KEY_TZOFFSET, Double.toString(airport.getTimezoneOffset()));
            editor.putString(KEY_DST, "" + airport.getDST());
            editor.putString(KEY_TZ, airport.getTimezone());
        }
        editor.apply();
    }
}
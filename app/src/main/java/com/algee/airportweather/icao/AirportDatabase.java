package com.algee.airportweather.icao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.algee.airportweather.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * An SQLite database interface that creates a static database from the 'airports.dat' CSV file
 * found on https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat
 * at the time this was made.
 */
public class AirportDatabase extends SQLiteOpenHelper
{
    private static final String DBNAME  = "icaodb";
    private static final int VERSION    = 1;

    private static final String
            TABLE_NAME = "airports",
            COL_ID = "_id",
            COL_AIRPORT_NAME = "a",
            COL_CITY = "b",
            COL_COUNTRY = "c",
            COL_IATA_CODE = "d",
            COL_ICAO_CODE = "e",
            COL_LATITUDE = "f",
            COL_LONGITUDE = "g",
            COL_ALTITUDE = "h",
            COL_TIMEZONE_OFFSET = "i",
            COL_DST = "j",
            COL_TIMEZONE = "k";

    private static final int
            IDX_ID = 0,
            IDX_AIRPORT_NAME = 1,
            IDX_CITY = 2,
            IDX_COUNTRY = 3,
            IDX_IATA_CODE = 4,
            IDX_ICAO_CODE = 5,
            IDX_LATITUDE = 6,
            IDX_LONGITUDE = 7,
            IDX_ALTITUDE = 8,
            IDX_TIMEZONE_OFFSET = 9,
            IDX_DST = 10,
            IDX_TIMEZONE = 11;

    private final Context mContext;

    public AirportDatabase(Context context)
    {
        super(context, DBNAME, null, VERSION);
        mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String createTableSqlFormat = "create table " +
                "%s (" +
                "%s integer primary key, " +    // airport id
                "%s text, " +                   // name
                "%s text, " +                   // city
                "%s text, " +                   // country
                "%s character(3), " +           // IATA code
                "%s character(4), " +           // ICAO code
                "%s double, " +                 // latitude
                "%s double, " +                 // longitude
                "%s integer, " +                // altitude
                "%s float, " +                  // timezone fractional hours difference from UTC
                "%s character(1), " +           // Dayligh Savings Time code (one character)
                "%s text)";                     // timezone description

        db.execSQL(String.format(createTableSqlFormat,
                TABLE_NAME,
                COL_ID,
                COL_AIRPORT_NAME,
                COL_CITY,
                COL_COUNTRY,
                COL_IATA_CODE,
                COL_ICAO_CODE,
                COL_LATITUDE,
                COL_LONGITUDE,
                COL_ALTITUDE,
                COL_TIMEZONE_OFFSET,
                COL_DST,
                COL_TIMEZONE));

        loadInitalDataSetFromAssets(db);
    }

    // Documentation at http://openflights.org/data.html
    private void populateAirport(String[] csvParts, Airport airport)
    {
        final String name, city, country, iata, icao, dst, timezone;

        name = csvParts[IDX_AIRPORT_NAME];
        city = csvParts[IDX_CITY];
        country = csvParts[IDX_COUNTRY];
        iata = csvParts[IDX_IATA_CODE];
        icao = csvParts[IDX_ICAO_CODE];
        dst = csvParts[IDX_DST];
        timezone = csvParts[IDX_TIMEZONE];

        airport.setId(Integer.valueOf(csvParts[0]));
        airport.setName(name.substring(1, name.length() - 1));
        airport.setCity(city.substring(1, city.length() - 1));
        airport.setCountry(country.substring(1, country.length() - 1));
        airport.setIATACode(iata.substring(1, iata.length() - 1));
        airport.setICAOCode(icao.substring(1, icao.length() - 1));
        airport.setLatitude(Double.parseDouble(csvParts[6]));
        airport.setLongitude(Double.parseDouble(csvParts[7]));
        airport.setAltitude(Integer.parseInt(csvParts[8]));
        airport.setTimezoneOffset(Double.parseDouble(csvParts[9]));
        airport.setDST(dst.charAt(1));
        airport.setTimezone(timezone.substring(1, timezone.length() - 1));
    }

    private void addAirport(SQLiteDatabase db, Airport airport)
    {
        ContentValues cv = new ContentValues(12);
        cv.put(COL_ID, airport.getId());
        cv.put(COL_AIRPORT_NAME, airport.getName());
        cv.put(COL_CITY, airport.getCity());
        cv.put(COL_COUNTRY, airport.getCountry());
        cv.put(COL_IATA_CODE, airport.getIATACode());
        cv.put(COL_ICAO_CODE, airport.getICAOCode());
        cv.put(COL_LATITUDE, airport.getLatitude());
        cv.put(COL_LONGITUDE, airport.getLongitude());
        cv.put(COL_ALTITUDE, airport.getAltitude());
        cv.put(COL_TIMEZONE_OFFSET, airport.getTimezoneOffset());
        cv.put(COL_DST, (int) airport.getDST());
        cv.put(COL_TIMEZONE, airport.getTimezone());
        insertContentValues(db, cv);
    }

    private void insertContentValues(SQLiteDatabase db, ContentValues cv)
    {
        db.beginTransaction();
        try
        {
            db.insert(TABLE_NAME, null, cv);
            db.setTransactionSuccessful();
        } finally
        {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    private static final String CSV_REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    private void loadInitalDataSetFromAssets(SQLiteDatabase db)
    {
        final Airport airport = new Airport();
        InputStream inputStream = null;
        try
        {
            inputStream = mContext.getResources().openRawResource(R.raw.airports);
            Scanner sc = new Scanner(inputStream, "UTF-8");
            while(sc.hasNextLine())
            {
                final String line = sc.nextLine();
                final String[] csvParts = line.split(CSV_REGEX);
                populateAirport(csvParts, airport);
                addAirport(db, airport);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if(inputStream != null)
                try
                {
                    inputStream.close();
                } catch (IOException e)
                {
                    // do nothing
                }
        }
    }

    private static final String[] ONE_ARG = new String[1];

    /**
     * Searches through the Airports database given a city's name.
     * @param city
     * @return
     */
    public Airport[] getAirportsByCity(String city)
    {
        SQLiteDatabase db = getReadableDatabase();
        if(city != null)
            city = city.trim();
        final String whereClause, orderByClause;
        final String[] whereClauseArgs;
        final Cursor cursor;
        if(city.isEmpty())
        {
            whereClause = String.format("%1$s not null and %1$s !=\"\"", COL_ICAO_CODE);
            orderByClause = String.format("%s asc", COL_CITY);
            whereClauseArgs = null;
        } else
        {
            whereClause = String.format("%s like ? and %2$s not null and %2$s != \"\"", COL_CITY, COL_ICAO_CODE);
            orderByClause = String.format("CASE WHEN %s LIKE '%s%%' THEN 0 ELSE 1 END, %1$s ASC", COL_CITY, city);
            ONE_ARG[0] = "%" + city + "%";
            whereClauseArgs = ONE_ARG;
        }
        cursor = db.query(TABLE_NAME, null, whereClause, whereClauseArgs, null, null, orderByClause);
        final int count = cursor.getCount();
        Airport[] airports = new Airport[cursor.getCount()];
        cursor.moveToFirst();
        for(int i = 0; i < count; i++)
        {
            airports[i] = createFromPositionedCursor(cursor);
            cursor.moveToNext();
        }
        return airports;
    }
/*

    public Airport[] getAirportsByIATACode(String iata)
    {
        SQLiteDatabase db = getReadableDatabase();
        final String whereClause = String.format("? like %%%s%%", iata);
        final String orderByClause = String.format("%s asc", COL_IATA_CODE);
        Cursor cursor = db.query(TABLE_NAME, null, whereClause, new String[]{COL_IATA_CODE}, null, null, orderByClause);
        final int count = cursor.getCount();
        Airport[] airports = new Airport[cursor.getCount()];
        cursor.moveToFirst();
        for(int i = 0; i < count; i++)
            airports[i] = createFromPositionedCursor(cursor);
        return airports;
    }
*/

    private Airport createFromPositionedCursor(Cursor cursor)
    {
        final Airport airport = new Airport();
        airport.setId(cursor.getInt(IDX_ID));
        airport.setName(cursor.getString(IDX_AIRPORT_NAME));
        airport.setCity(cursor.getString(IDX_CITY));
        airport.setCountry(cursor.getString(IDX_COUNTRY));
        airport.setIATACode(cursor.getString(IDX_IATA_CODE));
        airport.setICAOCode(cursor.getString(IDX_ICAO_CODE));
        airport.setLatitude(cursor.getDouble(IDX_LATITUDE));
        airport.setLongitude(cursor.getDouble(IDX_LONGITUDE));
        airport.setAltitude(cursor.getInt(IDX_ALTITUDE));
        airport.setTimezoneOffset(cursor.getDouble(IDX_TIMEZONE_OFFSET));
        airport.setDST((char) cursor.getInt(IDX_DST));
        airport.setTimezone(cursor.getString(IDX_TIMEZONE));
        return airport;
    }

}

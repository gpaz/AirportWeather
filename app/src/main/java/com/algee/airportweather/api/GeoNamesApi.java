package com.algee.airportweather.api;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

/**
 * A class that is meant to be used to access the 'GeoNames' api ({@link http://www.geonames.org})
 * that accesses weather reports via ICAO (International Civil Aviation Organization) codes.  The
 * report may or may not exist based on the airport's ability/want to report to the ICAO.
 */
public class GeoNamesApi
{
    // http://api.geonames.org/weatherIcaoJSON?ICAO=KSFO&username=sdkteam

    /**
     * Url format that requires two arguments:
     * 1 - icao string,
     * 2 - the 'username' associated with the GeoNames API.
     */
    private static final String HOME_URL_JSON_FORMAT =
            "http://api.geonames.org/weatherIcaoJSON?ICAO=%s&username=%s";

    private static final int READ_TIMEOUT_MILLIS = 5000,
                             CONNECT_TIMEOUT_MILLIS = 5000;

    private static final String SDKTEAM = "sdkteam";
    private static final String GPAZ = "gpaz";

    /**
     * The public username that can be used to access the GeoNames API endpoints.
     */
    private static String USER = SDKTEAM;
//    private static final String USER = GPAZ;

    public static void setUsername(String user)
    {
        USER = user != null ? user : "";
    }

    public static String getUser()
    {
        return USER;
    }

    /**
     * A string that allows access to the GeoNames API
     *
     * @return
     * The username that is publicly available for the purpose of issuing requests to the
     * GeoNames API.
     */
    public static String getUserName()
    {
        return USER;
    }

    /**
     * Performs an HTTP GET request to the GeoNames REST API for a weather report.
     * @param icao
     * The ICAO (International Civil Airpot Organization) string that identifies with a
     * weather-reporting system.
     * @param username
     * The user's name associated with the GeoNames API.
     * @return
     * A non-null {@code WeatherReport} object representing either the successful reply from the GeoNames
     * API giving an up-to-date report of the weather at that location or an unsuccessful reply
     * giving an error status within the {@code WeatherReport} object
     */
    @NonNull public static WeatherReport getWeatherReport(String icao, String username)
    {
        WeatherReport report;
        try
        {
            URL geonamesUrl = new URL(String.format(HOME_URL_JSON_FORMAT, icao, username));
            HttpURLConnection connection = (HttpURLConnection) geonamesUrl.openConnection();
            prepareGetRequest(connection);
            final String jsonResponse = executeRequest(connection);
            report = WeatherReportFactory.parseWeatherReportJson(jsonResponse);
        } catch (IOException e)
        {
            report = new WeatherReport();
            ErrorStatus errorStatus = new ErrorStatus(e.getMessage(), -1);
            report.setErrorStatus(errorStatus);
        } catch (Exception e)
        {
            report = new WeatherReport();
            ErrorStatus errorStatus = new ErrorStatus(e.getMessage(), -2);
            report.setErrorStatus(errorStatus);
        }

        return report;
    }

    /**
     * Prepares the HttpUrlConnection for the normal GeoNames ICAO API request.
     * @param connection
     * The created connection to the GeoNames ICAO API.
     * @throws ProtocolException
     */
    private static void prepareGetRequest(HttpURLConnection connection) throws ProtocolException
    {
        connection.setReadTimeout(READ_TIMEOUT_MILLIS);
        connection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.setRequestMethod("GET");
    }

    /**
     * Performs the http request and returns the JSON String response (dictated by the api).
     * @param connection
     * @return
     * @throws Exception
     */
    private static String executeRequest(HttpURLConnection connection)
    throws Exception
    {
        //Log.v("HTTP-OUT", connection.getURL().toString() + " --> " + jsonStr);
        InputStream inStream = null;
        try
        {
            // establish connection
            connection.connect();
            // check response code
            final int response = connection.getResponseCode();
            inStream = response == 200
                       ? connection.getInputStream()
                       : connection.getErrorStream();

            // read from the response body
            final String inJsonStr = readFullString(inStream);
            //Log.v("HTTP-IN", connection.getURL().toString() + " --> " + inJsonStr);
            return inJsonStr;
        } catch (Exception e)
        {
            // TODO: LOG ERROR
            throw e;
        } finally
        {
            // close input stream
            if(inStream != null)
                inStream.close();
        }
    }

    /**
     * Creates a String from an {@code InputStream}'s input until the endof the stream.
     * @param in
     * @return
     * @throws IOException
     */
    static String readFullString(InputStream in) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(in);
        while(scanner.hasNext())
            sb.append(scanner.nextLine());
        return sb.toString();
    }
}
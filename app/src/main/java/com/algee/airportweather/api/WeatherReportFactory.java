package com.algee.airportweather.api;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Static methods used to help parse strings into a WeatherReport object.  JSON implementation is
 * the only method as of now.
 */
public class WeatherReportFactory
{
    private static final String EMPTY_STRING = "";

    private static final String
            PARAM_WEATHER_OBSERVATION = "weatherObservation",
            PARAM_CLOUDS = "clouds",
            PARAM_WEATHER_CONDITION = "weatherCondition",
            PARAM_OBSERVATION = "observation",
            PARAM_WIND_DIRECTION = "windDirection",
            PARAM_ICAO = "ICAO",
            PARAM_SEA_LEVEL_PRESSURE = "seaLevelPressure",
            PARAM_ELEVATION = "elevation",
            PARAM_COUNTRY_CODE = "countryCode",
            PARAM_LATITUDE = "lat",
            PARAM_LONGITUDE = "lng",
            PARAM_TEMPERATURE = "temperature",
            PARAM_DEW_POINT = "dewPoint",
            PARAM_WIND_SPEED = "windSpeed",
            PARAM_HUMIDITY = "humidity",
            PARAM_STATION_NAME = "stationName",
            PARAM_OBSERVATION_TIME = "datetime",
            PARAM_CLOUDS_CODE = "cloudsCode",

            PARAM_ERROR_STATUS = "status",
            PARAM_ERROR_MESSAGE = "message",
            PARAM_ERROR_VALUE = "value";

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

    /**
     * Parses a string in JSON format to a {@code WeatherReport} object.
     * @param json
     * @return
     * A {@code WeatherReport} object parsed from the input String.  The returned object will
     * contain either valid data or a non-null {@code ErrorStatus} attribute found by the method
     * {@link WeatherReport{@link getErrorStatus}}.
     * @throws JSONException
     */
    @NonNull public static WeatherReport parseWeatherReportJson(String json) throws JSONException
    {
        JSONObject jsonObj = new JSONObject(json);
        return jsonObj.has(PARAM_ERROR_STATUS)
               ? parseError(jsonObj.getJSONObject(PARAM_ERROR_STATUS))
               : parseSuccess(jsonObj.getJSONObject(PARAM_WEATHER_OBSERVATION));
    }

    @NonNull private static WeatherReport parseSuccess(JSONObject json) throws JSONException
    {
        final WeatherReport report = new WeatherReport();
        // parse latitude and longitude
        final double lat = json.optDouble(PARAM_LATITUDE, 0);
        final double lng = json.optDouble(PARAM_LONGITUDE, 0);
        report.setGeoCoordinates(new GeoCoordinates(lat, lng));
        // parse codified observation
        final String observation = json.optString(PARAM_OBSERVATION, EMPTY_STRING);
        report.setObservation(new Observation(observation));
        // parse date time : format = "2016-04-07 03:56:00" // UTC
        final String dateTimeStr = json.optString(PARAM_OBSERVATION_TIME, null);
        Date date = null;
        if(dateTimeStr != null)
        {
            try
            {
                date = SDF.parse(dateTimeStr);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        report.setTime(date);
        // now parse the rest
        report.setClouds(json.optString(PARAM_CLOUDS, EMPTY_STRING));
        report.setWeatherCondition("Wather Condition: " + json.optString(PARAM_WEATHER_CONDITION, EMPTY_STRING));
        report.setWindDirection(json.optInt(PARAM_WIND_DIRECTION, Integer.MIN_VALUE));
        report.setStationCode(json.optString(PARAM_ICAO, EMPTY_STRING));
        report.setStationName(json.optString(PARAM_STATION_NAME, EMPTY_STRING));
        report.setSeaLevelPressure(json.optDouble(PARAM_SEA_LEVEL_PRESSURE, Double.NEGATIVE_INFINITY));
        report.setElevation(json.optInt(PARAM_ELEVATION, Integer.MIN_VALUE));
        report.setDewPoint(json.optString(PARAM_DEW_POINT, EMPTY_STRING));
        report.setWindSpeed(json.optString(PARAM_WIND_SPEED, EMPTY_STRING));
        report.setHumidity(json.optInt(PARAM_HUMIDITY, Integer.MIN_VALUE));
        report.setCloudsCode(json.optString(PARAM_CLOUDS_CODE, EMPTY_STRING));
        report.setTemperature(json.optString(PARAM_TEMPERATURE, EMPTY_STRING));
        report.setCountryCode(json.optString(PARAM_COUNTRY_CODE, EMPTY_STRING));
        return report;
    }

    @NonNull private static WeatherReport parseError(JSONObject json)
    {
        final WeatherReport report = new WeatherReport();
        final String message = json.optString(PARAM_ERROR_MESSAGE, EMPTY_STRING);
        final int value = json.optInt(PARAM_ERROR_VALUE, -1);
        report.setErrorStatus(new ErrorStatus(message, value));
        return report;
    }
}

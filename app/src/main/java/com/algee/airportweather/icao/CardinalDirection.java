package com.algee.airportweather.icao;


import java.util.Map;
import java.util.TreeMap;

/**
 * Class that converts a positive integer (in degrees) into our known map or compass direction.
 */
public class CardinalDirection
{
    private static final TreeMap<Double, String> MAP = new TreeMap<Double, String>();
    static
    {
        /**
         * http://climate.umn.edu/snow_fence/components/winddirectionanddegreeswithouttable3.htm
         */
        final String NORTH = "N";
        MAP.put(0.0, NORTH);
        MAP.put(11.25, "NNE");
        MAP.put(33.75, "NE");
        MAP.put(56.25, "ENE");
        MAP.put(78.75, "E");
        MAP.put(101.25, "ESE");
        MAP.put(123.75, "SE");
        MAP.put(146.25, "SSE");
        MAP.put(168.75, "S");
        MAP.put(191.25, "SSW");
        MAP.put(213.75, "SW");
        MAP.put(236.25, "WSW");
        MAP.put(258.75, "W");
        MAP.put(281.25, "WNW");
        MAP.put(303.75, "NW");
        MAP.put(326.25, "NNW");
        MAP.put(348.75, NORTH);
    }

    public static String getCardinalDirection(double degrees)
    {
        // assumes a positive value
        double corrected = degrees % 360.0;
        Map.Entry<Double, String> entry = MAP.floorEntry(degrees);
        if(entry == null)
            return "N/A";
        else
            return entry.getValue();
    }
}

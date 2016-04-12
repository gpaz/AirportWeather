package com.algee.airportweather;

import java.util.List;


public class Util
{

    public static String join(List list, String separator)
    {
        if(list == null)
            return "null";
        if(separator == null)
            separator = "";
        final StringBuilder sb = new StringBuilder();
        for(Object o : list)
        {
            sb.append(o.toString());
            sb.append(separator);
        }
        return sb.toString();
    }
}

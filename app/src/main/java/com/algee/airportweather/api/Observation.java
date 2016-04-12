package com.algee.airportweather.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.algee.airportweather.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The original string assocated with the 'observation' key separated by a space.
 */
public class Observation implements Parcelable
{
    private List<String> mObservationParts;

    public Observation(String s)
    {
        mObservationParts = s != null
                            ? partition(s)
                            : Collections.<String>emptyList();
    }

    private List<String> partition(String s)
    {
        String[] parts = s.split(" ");
        return Arrays.asList(parts);
    }

    @Override
    public String toString()
    {
        return Util.join(mObservationParts, " ");
    }

    public int getPartCount()
    {
        return mObservationParts.size();
    }

    public String getPart(int index)
    {
        return mObservationParts.get(index);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mObservationParts.size());
        dest.writeStringList(mObservationParts);
    }

    private Observation(Parcel source)
    {
        int size = source.readInt();
        mObservationParts = new ArrayList<String>(size);
        source.readStringList(mObservationParts);
    }


    public static final Creator<Observation> CREATOR = new Creator<Observation>()
    {
        @Override
        public Observation createFromParcel(Parcel source)
        {
            return new Observation(source);
        }

        @Override
        public Observation[] newArray(int size)
        {
            return new Observation[size];
        }
    };
}

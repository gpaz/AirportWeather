package com.algee.airportweather.weatherui;



import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.algee.airportweather.R;
import com.algee.airportweather.hacks.AsyncLoader;
import com.algee.airportweather.icao.Airport;
import com.algee.airportweather.icao.AirportDatabase;

import java.util.Arrays;
import java.util.List;


public class StationListFragment extends Fragment
{
    private static final int AIRPORT_DATABASE_LOADER_ID = 1;
    private static final String KEY_QUERY = "query";
    private final AirportLoaderCallbacks mAirportLoaderCallbacks = new AirportLoaderCallbacks();
    private String mSearchParameter;
    private StationListFragmentCallback mCallback;

    // UI
    private ListView mListView;
    private ProgressBar mProgressBar;


    public static StationListFragment newInstance(String initialQuery)
    {
        StationListFragment slf = new StationListFragment();
        Bundle args = new Bundle(1);
        args.putString(KEY_QUERY, initialQuery != null ? initialQuery : "");
        slf.setArguments(args);
        return slf;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mCallback = (StationListFragmentCallback) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final Bundle b = savedInstanceState != null ? savedInstanceState : getArguments();
        if(b != null)
            mSearchParameter = b.getString(KEY_QUERY, "");
        if(mSearchParameter == null)
            mSearchParameter = "";
        // ensure mSearchParameter is not null
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_station_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.airport_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Airport airport = (Airport) parent.getItemAtPosition(position);
                mCallback.onStationSelected(airport);
            }
        });
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        mProgressBar.setVisibility(View.VISIBLE);
        LoaderManager lm = getLoaderManager();
        lm.initLoader(AIRPORT_DATABASE_LOADER_ID, null, mAirportLoaderCallbacks);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_QUERY, mSearchParameter);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public void setSearchParameter(String searchString)
    {
        mSearchParameter = searchString;
        getLoaderManager().restartLoader(AIRPORT_DATABASE_LOADER_ID, null, mAirportLoaderCallbacks);
    }

    private class AirportLoaderCallbacks implements LoaderManager.LoaderCallbacks
    {
        @Override
        public Loader onCreateLoader(int id, Bundle args)
        {
            AirportLoader loader = new AirportLoader(getActivity());
            loader.setFilter(mSearchParameter);
            return loader;
        }

        @Override
        public void onLoadFinished(Loader loader, Object data)
        {
            mProgressBar.setVisibility(View.GONE);
            List<Airport> airports = (List<Airport>) data;
            // set adapter data
            if(getActivity() != null)
            {
                ArrayAdapter<Airport> adapter = new AirportListAdapter(getActivity(), airports);
                mListView.setAdapter(adapter);
            }
        }

        @Override
        public void onLoaderReset(Loader loader)
        {

        }
    }

    private static class AirportLoader extends AsyncLoader<List<Airport>>
    {
        private String mFilter;

        public AirportLoader(Context context)
        {
            super(context);
        }

        public String getFilter()
        {
            return mFilter;
        }

        public void setFilter(String filter)
        {
            mFilter = filter;
        }

        @Override
        public List<Airport> loadInBackground()
        {
            AirportDatabase icaoDb = new AirportDatabase(getContext());
            final List<Airport> airports;
            if(mFilter == null || mFilter.isEmpty())
                airports = Arrays.asList(icaoDb.getAirportsByCity(""));
            else
                airports = Arrays.asList(icaoDb.getAirportsByCity(mFilter));
            return airports;
        }
    }

    private static class AirportListItemViewHolder
    {
        private final TextView mAirportCityTv;
        private final TextView mAirportNameTv;

        public AirportListItemViewHolder(TextView airportCityTv, TextView airportNameTv)
        {
            mAirportCityTv = airportCityTv;
            mAirportNameTv = airportNameTv;
        }

        public CharSequence getAirportName()
        {
            return mAirportNameTv.getText();
        }

        public void setAirportName(CharSequence text)
        {
            mAirportNameTv.setText(text);
        }

        public CharSequence getAirportCity()
        {
            return mAirportCityTv.getText();
        }

        public void setAirportCity(CharSequence text)
        {
            mAirportCityTv.setText(text);
        }
    }

    private static class AirportListAdapter extends ArrayAdapter<Airport>
    {
        private static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_2;
        private final LayoutInflater mLayoutInflater;

        public AirportListAdapter(Context context, List<Airport> objects)
        {
            super(context, LAYOUT_RES_ID, objects);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
            {
                convertView = mLayoutInflater.inflate(LAYOUT_RES_ID, parent, false);
                final TextView cityTv = (TextView) convertView.findViewById(android.R.id.text1);
                final TextView nameTv = (TextView) convertView.findViewById(android.R.id.text2);
                AirportListItemViewHolder holder = new AirportListItemViewHolder(cityTv, nameTv);
                convertView.setTag(holder);
            }

            final AirportListItemViewHolder holder = (AirportListItemViewHolder) convertView.getTag();
            Airport airport = getItem(position);
            holder.setAirportCity(airport.getCity() + ", " + airport.getCountry());
            holder.setAirportName(airport.getName() + " (" + airport.getICAOCode() + ")");
            return convertView;
        }
    }

    public interface StationListFragmentCallback
    {
        void onStationSelected(Airport airport);
    }
}

package com.algee.airportweather.weatherui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algee.airportweather.R;
import com.algee.airportweather.api.GeoNamesApi;
import com.algee.airportweather.api.WeatherReport;
import com.algee.airportweather.hacks.AsyncLoader;
import com.algee.airportweather.icao.CardinalDirection;

import java.util.Date;


public class WeatherReportFragment
        extends Fragment
    implements SwipeRefreshLayout.OnRefreshListener
{
    private static final String TAG = WeatherReport.class.getSimpleName();

    private static final String KEY_ICAO = "icao",
                                KEY_WEATHER_REPORT = "weather-report";

    private String mICAO;
    private WeatherReport mWeatherReport;
    private WeatherReportLoaderCallbacks mWeatherReportLoaderCallbacks;

    private View mSelectedStationLayout, mUnselectedStationLayout;

    private TextView    mAirportNameTv,
                        mAirportIcaoTv,
                        mLatitudeTv,
                        mLongitudeTv,
                        mCloudsTv,
                        mElevationTv,
                        mWeatherConditionTv,
                        mDewpointTv,
                        mHumidityTv,
                        mTemperatureTv,
                        mSeaLevelPressureTv,
                        mWindSpeedTv,
                        mWindDirectionTv,
                        mErrorMessage,
                        mErrorIcao;

    private ProgressBar mProgressBar;

    private SwipeRefreshLayout mSwipeRefresh;

    public static WeatherReportFragment newInstance(String icao)
    {
        WeatherReportFragment wrf = new WeatherReportFragment();
        Bundle args = new Bundle(1);
        args.putString(KEY_ICAO, icao != null ? icao : "");
        wrf.setArguments(args);
        return wrf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle b = savedInstanceState != null ? savedInstanceState : getArguments();
        if(b != null)
        {
            mICAO = b.getString(KEY_ICAO);
            mWeatherReport = b.getParcelable(KEY_WEATHER_REPORT);
        }
        if(mICAO == null)
            mICAO = "";
        mWeatherReportLoaderCallbacks = new WeatherReportLoaderCallbacks();
        // ensure that mICAO is never null.
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_ICAO, mICAO);
        outState.putParcelable(KEY_WEATHER_REPORT, mWeatherReport);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_weather_report, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mSelectedStationLayout = view.findViewById(R.id.selected_station_layout);
        mUnselectedStationLayout = view.findViewById(R.id.unselected_station_layout);

        mAirportNameTv = (TextView) view.findViewById(R.id.airport_name);
        mAirportIcaoTv = (TextView) view.findViewById(R.id.airport_icao);
        mLatitudeTv = (TextView) view.findViewById(R.id.weather_latitude);
        mLongitudeTv = (TextView) view.findViewById(R.id.weather_longitude);
        mCloudsTv = (TextView) view.findViewById(R.id.weather_clouds);
        mElevationTv = (TextView) view.findViewById(R.id.weather_elevation);
        mWeatherConditionTv = (TextView) view.findViewById(R.id.weather_condition);
        mDewpointTv = (TextView) view.findViewById(R.id.weather_dewpoint);
        mHumidityTv = (TextView) view.findViewById(R.id.weather_humidity);
        mTemperatureTv = (TextView) view.findViewById(R.id.weather_temperature);
        mSeaLevelPressureTv = (TextView) view.findViewById(R.id.weather_sea_level_pressure);
        mWindSpeedTv = (TextView) view.findViewById(R.id.weather_wind_speed);
        mWindDirectionTv = (TextView) view.findViewById(R.id.weather_wind_direction);
        mErrorMessage = (TextView) view.findViewById(R.id.error_message);
        mErrorIcao = (TextView) view.findViewById(R.id.airport_icao_failure);

        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefresh.setOnRefreshListener(this);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);


        if(mWeatherReport != null)
            updateUi();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mProgressBar.setVisibility(View.VISIBLE);
        LoaderManager lm = getLoaderManager();
        lm.initLoader(0, null, mWeatherReportLoaderCallbacks);
    }


    // assumes a non-null value, for now
    private void updateToNewWeatherReport(WeatherReport newWeatherReport)
    {
        mWeatherReport = newWeatherReport;
        updateUi();
    }

    private void updateUi()
    {
        final String EMPTY = "";
        if(mWeatherReport.hasErrorStatus())
        {
            mSelectedStationLayout.setVisibility(View.GONE);
            mUnselectedStationLayout.setVisibility(View.VISIBLE);
            mAirportNameTv.setText(EMPTY);
            mAirportIcaoTv.setText(EMPTY);
            mLatitudeTv.setText(EMPTY);
            mLongitudeTv.setText(EMPTY);
            mCloudsTv.setText(EMPTY);
            mElevationTv.setText(EMPTY);
            mWeatherConditionTv.setText(EMPTY);
            mDewpointTv.setText(EMPTY);
            mHumidityTv.setText(EMPTY);
            mTemperatureTv.setText(EMPTY);
            mSeaLevelPressureTv.setText(EMPTY);
            mWindSpeedTv.setText(EMPTY);
            mWindDirectionTv.setText(EMPTY);
            mErrorMessage.setText(mWeatherReport.getErrorStatus().getMessage());
            mErrorIcao.setText(mICAO);
        } else
        {
            mSelectedStationLayout.setVisibility(View.VISIBLE);
            mUnselectedStationLayout.setVisibility(View.GONE);
            mAirportNameTv.setText(mWeatherReport.getStationName());
            mAirportIcaoTv.setText("("+mICAO+")");
            mLatitudeTv.setText("Lat: " + mWeatherReport.getGeoCoordinates().getLatitude());
            mLongitudeTv.setText("Lng: " + mWeatherReport.getGeoCoordinates().getLongitude());
            mCloudsTv.setText(mWeatherReport.getClouds());
            mElevationTv.setText("Elevation: " + mWeatherReport.getElevation() + " meters");
            mWeatherConditionTv.setText(mWeatherReport.getWeatherCondition());
            mDewpointTv.setText("Dewpoint: " + mWeatherReport.getDewPoint());
            mHumidityTv.setText("Humidity: " + mWeatherReport.getHumidity());
            mTemperatureTv.setText(mWeatherReport.getTemperature() + " C"); // be careful not to sent an integer
            mSeaLevelPressureTv.setText(EMPTY + mWeatherReport.getSeaLevelPressure());
            mWindSpeedTv.setText("Wind Speed: " + mWeatherReport.getWindSpeed() + " knots");
            mWindDirectionTv.setText("Wind Direction: " + CardinalDirection.getCardinalDirection(mWeatherReport.getWindDirection()) + " (" + mWeatherReport.getWindDirection() + ")");
        }
    }

    @Override
    public void onRefresh()
    {
        getLoaderManager().restartLoader(0, null, mWeatherReportLoaderCallbacks);
    }


    private class WeatherReportLoaderCallbacks implements LoaderManager.LoaderCallbacks<WeatherReport>
    {
        @Override
        public Loader<WeatherReport> onCreateLoader(int id, Bundle args)
        {
            WeatherReportAsyncLoader loader = new WeatherReportAsyncLoader(getContext());
            loader.setIcao(mICAO);
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<WeatherReport> loader, WeatherReport data)
        {
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefresh.setRefreshing(false);
            if(data == null)
                throw new NullPointerException("data should not be null.");
            final Activity activity = getActivity();
            if(activity != null)
            {
                if(data.hasErrorStatus())
                    handleErrorWeatherReport(data);
                else
                    handleNewValidWeatherReport(data);
            }
        }

        private void handleNewValidWeatherReport(WeatherReport newWeatherReport)
        {
            if(mWeatherReport == null
                    || mWeatherReport.hasErrorStatus()
                    || !(areEqual(mWeatherReport.getStationCode(), newWeatherReport.getStationCode())
                        || areEqual(mWeatherReport.getTime(), newWeatherReport.getTime())))
            {
                updateToNewWeatherReport(newWeatherReport);
            } // else do nothing
        }

        private void handleErrorWeatherReport(WeatherReport newWeatherReport)
        {
            if(mWeatherReport == null
                    || mWeatherReport.hasErrorStatus())
            {
                updateToNewWeatherReport(newWeatherReport);
            } else
            {
                // mWeatherReport is a valid WeatherReport, thus we will keep the original error
                // report as the current weather report and just notify through a simple to the
                // user that the latest report returned as an error, likely due to connection issues.
                Toast.makeText(getContext(), "Request Failed (code="
                        + newWeatherReport.getErrorStatus().getValue() + ")", Toast.LENGTH_SHORT)
                        .show();
                Log.e(TAG, mWeatherReport.getErrorStatus().toString());
            }
        }

        private boolean areEqual(Date d1, Date d2)
        {
            return d1 == null ? d2 == null : d1.equals(d2);
        }

        private boolean areEqual(String s1, String s2)
        {
            return s1 == null ? s2 == null : s1.equals(s2);
        }

        @Override
        public void onLoaderReset(Loader<WeatherReport> loader)
        {

        }
    }

    private static class WeatherReportAsyncLoader extends AsyncLoader<WeatherReport>
    {
        private String mIcao;

        public WeatherReportAsyncLoader(Context context)
        {
            super(context);
        }

        public String getIcao()
        {
            return mIcao;
        }

        public void setIcao(String icao)
        {
            mIcao = icao;
        }

        @Override
        public WeatherReport loadInBackground()
        {
            return GeoNamesApi.getWeatherReport(mIcao, GeoNamesApi.getUserName());
        }
    }
}

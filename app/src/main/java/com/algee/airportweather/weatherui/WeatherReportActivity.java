package com.algee.airportweather.weatherui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.algee.airportweather.R;
import com.algee.airportweather.icao.Airport;
import com.algee.airportweather.icao.ICAOPreferences;

public class WeatherReportActivity extends AppCompatActivity
    implements SearchView.OnQueryTextListener,
        StationListFragment.StationListFragmentCallback,
        SearchView.OnCloseListener,
        View.OnClickListener
{
    private static final String FRAG_AIRPORT_LIST = "airport-list-fragment";
    private static final String FRAG_WEATHER_REPORT = "weather-report-fragment";
    private ICAOPreferences mICAOPreferences;
    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mICAOPreferences = new ICAOPreferences(getApplicationContext());
        setContentView(R.layout.activity_weather_report);
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.search);
        SearchView searchView =
                (SearchView) mSearchMenuItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setOnSearchClickListener(this);
        searchView.setOnCloseListener(this);
        mSearchView = searchView;
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        FragmentManager fm = getSupportFragmentManager();
        final Airport selectedAirport = mICAOPreferences.getMostRecentSelectedAirport();
        if(selectedAirport == null)
        {
            WeatherReportFragment ssf = (WeatherReportFragment) fm.findFragmentByTag(FRAG_WEATHER_REPORT);
            StationListFragment slf = (StationListFragment) fm.findFragmentByTag(FRAG_AIRPORT_LIST);
            if(slf == null)
            {
                showAirportList("", ssf != null);
            }
        } else if(fm.findFragmentByTag(FRAG_WEATHER_REPORT) == null)
                showWeatherReportFragment(selectedAirport);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent)
    {
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            String query = intent.getStringExtra(SearchManager.QUERY);
            performSearch(query);
        }
    }

    private void performSearch(String query)
    {
        StationListFragment fragment = (StationListFragment) getSupportFragmentManager().findFragmentByTag(FRAG_AIRPORT_LIST);
        if(fragment != null && !fragment.isRemoving())
            fragment.setSearchParameter(query);
        else
        {
            final boolean addToBackStack = getSupportFragmentManager().findFragmentByTag(FRAG_WEATHER_REPORT) != null;
            showAirportList(query, addToBackStack);
        }
    }

    private void updateAirportSelection(Airport airport)
    {
        mICAOPreferences.setMostRecentSelectedAirport(airport);
    }

    private void showAirportList(String query, boolean addToBackStack)
    {
        final StationListFragment slf = StationListFragment.newInstance(query);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().replace(R.id.fragment_space, slf, FRAG_AIRPORT_LIST);
        if(addToBackStack && fm.findFragmentByTag(FRAG_WEATHER_REPORT) != null)
            ft.addToBackStack(null);
        ft.commit();
    }

    private void showWeatherReportFragment(Airport selectedAirport)
    {
        FragmentManager fm = getSupportFragmentManager();
        WeatherReportFragment wrf = (WeatherReportFragment) fm.findFragmentByTag(FRAG_WEATHER_REPORT);
        if(wrf != null && fm.getBackStackEntryCount() > 0)
            fm.popBackStack();
        wrf = WeatherReportFragment.newInstance(selectedAirport.getICAOCode());
        fm.beginTransaction().replace(R.id.fragment_space, wrf, FRAG_WEATHER_REPORT).commit();
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        performSearch(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        performSearch(newText);
        return true;
    }

    @Override
    public void onStationSelected(Airport airport)
    {
        updateAirportSelection(airport);
        showWeatherReportFragment(airport);
        mSearchView.setOnQueryTextListener(null); // to stop notification of any text change
        mSearchMenuItem.collapseActionView(); // collapse the searchview and the keyboard if up
    }

    @Override
    public void onClick(View v)
    {
        final FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(FRAG_AIRPORT_LIST) == null)
            showAirportList("", getSupportFragmentManager().findFragmentByTag(FRAG_WEATHER_REPORT) != null);
        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onClose()
    {
        FragmentManager fm = getSupportFragmentManager();
        if(mICAOPreferences.getMostRecentSelectedAirport() == null )
        {
            // do nothing
        } else if (fm.getBackStackEntryCount() > 0 && fm.findFragmentByTag(FRAG_AIRPORT_LIST) != null)
        {
            fm.popBackStack();
        }
        return true;
    }
}

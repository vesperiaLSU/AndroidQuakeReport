package android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    /**
     * Query URL
     */
    private String url;

    /**
     * Construsts a new instance of {@link EarthquakeLoader}
     * @param context of the activity
     * @param url to load data from
     */
    EarthquakeLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    /**
     * call forceLoad() when onStartLoading() in order to trigger loadInBackground()
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is run on a background thread just like doInBackGround for AsyncTask
     * @return a list of {@link Earthquake}
     */
    @Override
    public List<Earthquake> loadInBackground() {
        if (this.url == null) {
            return null;
        }

        // Perform the network requests, parse the response, and then extract a list of earthquakes
        return QueryUtils.fetchEarthquakeData(url);
    }
}

package android.quakereport;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

final class QueryUtils {

    /**
     * The private constructor for {@link QueryUtils} so that no
     * new instance can be created for this class
     */
    private QueryUtils() {
    }

    static List<Earthquake> extractEarthquakes(String jsonResponse) {
        List<Earthquake> earthquakes = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray earthquakeArray = jsonObject.optJSONArray("features");

            for (int i = 0; i < earthquakeArray.length(); i++) {
                JSONObject currentEarthquake = earthquakeArray.optJSONObject(i);
                JSONObject properties = currentEarthquake.optJSONObject("properties");
                double magnitude = properties.optDouble("mag");
                String location = properties.optString("place");
                long time = properties.optLong("time");
                String url = properties.optString("url");

                Earthquake earthquake = new Earthquake(magnitude, location, time, url);
                earthquakes.add(earthquake);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return earthquakes;
    }



}

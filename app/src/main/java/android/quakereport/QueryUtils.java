package android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

final class QueryUtils {

    /**
     * Sample JSON response for a USGS query
     */
    private static final String LOG_TAG = EarthquakeActivity.class.getSimpleName();

    /**
     * The private constructor for {@link QueryUtils} so that no
     * new instance can be created for this class
     */
    private QueryUtils() {
    }

    static List<Earthquake> fetchEarthquakeData(String urlRequest) {
        // Create URL object from the string provided
        URL url = createUrl(urlRequest);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException exception) {
            Log.e(LOG_TAG, "Error with making Http request", exception);
        }

        return QueryUtils.extractEarthquakes(jsonResponse);
    }

    private static List<Earthquake> extractEarthquakes(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

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

    /**
     * Make Http request with the URL provided
     *
     * @param url the URL object to make connection to
     * @return a string representing the json response
     * @throws IOException
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    /**
     * Read from the input stream and turns into a json response
     *
     * @param inputStream the input stream received from url connection
     * @return a string representing the json response
     * @throws IOException
     */
    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null && line.length() > 0) {
                sb.append(line);
                line = bufferedReader.readLine();
            }
        }

        return sb.toString();
    }

    /**
     * Create a URL object from string
     *
     * @param urlStr a string representing url
     * @return a URL object
     */
    private static URL createUrl(String urlStr) {
        URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }

        return url;
    }
}

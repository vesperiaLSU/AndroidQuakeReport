package android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.TimeZoneFormat;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    /**
     * Constructor for {@link EarthquakeAdapter} object
     * @param context the EarthquakeActivity context
     * @param earthquakes a list of earthquakes
     */
    EarthquakeAdapter(Context context, List<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    /**
     * The part of the location string from the USGS service that we use to determine
     * whether or not there is a location offset present ("5km N of Cairo, Egypt").
     */
    private static final String LOCATION_SEPARATOR = " of ";


    /**
     * Returns a list item view that displays information about the earthquake at the given position
     * in the list of earthquakes.
     */
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Check if there is an existing list item view (called recycled view) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_list_item, parent, false);
        }

        // Find the earthquake at the given position in the list of earthquakes
        Earthquake currentEarthquake = getItem(position);

        if (currentEarthquake != null) {
            // display the magnitude of the current earthquake
            TextView magnitudeView = (TextView) listItemView.findViewById(R.id.magnitude);
            String formattedMagnitude = formatMagnitude(currentEarthquake.getMagnitude());
            magnitudeView.setText(formattedMagnitude);

            // Set the proper background color on the magnitude circle
            GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeView.getBackground();
            int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());
            magnitudeCircle.setColor(magnitudeColor);

            // Display the location of the current earthquake
            String location = currentEarthquake.getLocation();
            String primaryLocation;
            String locationOffset;

            // Check whether the location contains the " of " text
            if (location.contains(LOCATION_SEPARATOR)) {
                // find the index of the " of " text and get substring based on its index
                int indexOfSeparator = location.indexOf(LOCATION_SEPARATOR);
                locationOffset = location.substring(0, indexOfSeparator + LOCATION_SEPARATOR.length());
                primaryLocation = location.substring(indexOfSeparator);
            } else {
                // otherwise, just assign "near the" to the offset
                locationOffset = getContext().getString(R.string.near_the);
                primaryLocation = location;
            }

            // set text for the primary location
            TextView primaryLocationView = (TextView) listItemView.findViewById(R.id.primary_location);
            primaryLocationView.setText(primaryLocation);

            // set the text for the location offset
            TextView locationOffsetView = (TextView) listItemView.findViewById(R.id.location_offset);
            locationOffsetView.setText(locationOffset);

            Date dateObject = new Date(currentEarthquake.getTimeInMilliseconds());

            // set the text for the date
            TextView dateView = (TextView) listItemView.findViewById(R.id.date);
            String formattedDate = formatDate(dateObject);
            dateView.setText(formattedDate);

            // set the text for the time
            TextView timeView = (TextView) listItemView.findViewById(R.id.time);
            String formattedTime = formatTime(dateObject);
            timeView.setText(formattedTime);
        }

        return listItemView;
    }

    /**
     * Format the date object into string of time
     * @param dateObject the date object
     * @return a string representation of the time (i.e. "4:30 PM)
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
        return timeFormat.format(dateObject);
    }

    /**
     * Format the date object into string of date
     * @param dateObject the date object
     * @return a string representation of the date (i.e. "Mar 3, 1998")
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy", Locale.US);
        return dateFormat.format(dateObject);
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }

        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

    /**
     * format the magnitude into 0.0 format
     * @param magnitude the original magnitude
     * @return formatted magnitude
     */
    private String formatMagnitude(double magnitude) {
        DecimalFormat formatter = new DecimalFormat("0.0");
        return formatter.format(magnitude);
    }
}

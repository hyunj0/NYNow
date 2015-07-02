package nyteam.c4q.nyc.nynow;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DirectionsAsyncTask extends AsyncTask<String, Void, ArrayList<LatLng>> {

    ArrayList<String> polylines = new ArrayList<>();
    ArrayList<LatLng> directions = new ArrayList<>();

    @Override
    protected ArrayList<LatLng> doInBackground(String... strings) {
        try {
            JSONObject jsonObject = new JSONObject(strings[0]);
            JSONArray routes = jsonObject.getJSONArray("routes");
            for (int i = 0; i < routes.length(); i++) {
                JSONArray legs = ((JSONObject) routes.get(i)).getJSONArray("legs");
                for (int j = 0; j < legs.length(); j++) {
                    JSONArray steps = ((JSONObject) legs.get(j)).getJSONArray("steps");
                    for (int k = 0; k < steps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) steps.get(k)).get("polyline")).get("points");
                        polylines.add(polyline);
                        directions = decodePoly(polylines);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return directions;
    }

    //credits to jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
    private ArrayList<LatLng> decodePoly(ArrayList<String> polylines) {

        ArrayList<LatLng> poly = new ArrayList<LatLng>();

        for (int start = 0; start < polylines.size(); start++) {
            String encoded = polylines.get(start);

            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
        }
        return poly;
    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> latLngs) {
        super.onPostExecute(latLngs);
    }
}
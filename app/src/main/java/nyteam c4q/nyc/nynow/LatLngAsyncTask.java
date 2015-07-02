package nyteam.c4q.nyc.nynow;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LatLngAsyncTask extends AsyncTask<String, Void, LatLng> {

    double latitude, longitude;

    @Override
    protected LatLng doInBackground(String... strings) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(strings[0]);
            longitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");
            latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new LatLng(latitude, longitude);
    }

    @Override
    protected void onPostExecute(LatLng latLng) {
        super.onPostExecute(latLng);
    }
}

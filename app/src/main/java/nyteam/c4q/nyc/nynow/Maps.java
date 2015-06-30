package nyteam.c4q.nyc.nynow;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Maps extends ActionBarActivity implements OnMapReadyCallback {
    private MapFragment mMapFragment;
    private EditText from, to;
    private Button search_button;
    private double lng, lat;
    private String reformat;
    ArrayList<LatLng> directions = new ArrayList<LatLng>();
    private JSONArray routes, legs, steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        initializeViews();
    }

    public void initializeViews() {
        from = (EditText) findViewById(R.id.from);
        to = (EditText) findViewById(R.id.to);
        search_button = (Button) findViewById(R.id.search_button);
    }

    public void getLatLongFromSearch(String search) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String uri = "https://maps.googleapis.com/maps/api/geocode/json?address=" + search + "&sensor=true";
        HttpGet httpGet = new HttpGet(uri);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getDirections(String from, String to) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String uri = "https://maps.googleapis.com/maps/api/directions/json?origin=" + from + "&destination=" + to;
        HttpGet httpGet = new HttpGet(uri);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject;
        ArrayList<String> polylines = new ArrayList<>();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            routes = jsonObject.getJSONArray("routes");
            for (int i = 0; i < routes.length(); i++) {
                legs = ((JSONObject) routes.get(i)).getJSONArray("legs");
                for (int j = 0; j < legs.length(); j++) {
                    steps = ((JSONObject) legs.get(j)).getJSONArray("steps");
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
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);

        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
            }
        });

//        search_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                reformat = from.getText().toString().replace(" ", "+");
//                getLatLongFromSearch(reformat);
//                googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
//            }
//        });


        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String formatFrom = from.getText().toString().replace(" ", "+");
                String formatTo = to.getText().toString().replace(" ", "+");
                getDirections(formatFrom, formatTo);

                PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.BLUE);
                for (int poly = 0; poly < directions.size(); poly++) {
                    if (poly == 0 || poly == directions.size() - 1) {
                        googleMap.addMarker(new MarkerOptions().position(directions.get(poly)));
                    }
                    rectLine.add(directions.get(poly));
                }

                Polyline polyLine = googleMap.addPolyline(rectLine);

            }
        });
    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

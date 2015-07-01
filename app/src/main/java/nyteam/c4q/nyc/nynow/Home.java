package nyteam.c4q.nyc.nynow;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Home extends ActionBarActivity implements OnMapReadyCallback, LocationListener {

    private ScrollView scroll_view;
    private ImageView greeting;
    private TextView title;
    private EditText todo;
    private Button add;
    private ListView todo_list;
    private MapFragment map;

    private String name, location;

    private ArrayList<String> todos;
    private ArrayAdapter<String> todo_adapter;

    private LocationManager mLocationManager;
    private Criteria mCriteria;
    private String mProvider;
    private Location mLocation;
    private double latitude, longitude;
    private LatLng latLng;

    private OkHttpClient client = new OkHttpClient();

    private ArrayList<LatLng> directions = new ArrayList<LatLng>();
    private ArrayList<MarkerOptions> markers = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initializeViews();
        setGreeting();

        name = getIntent().getExtras().get("Name").toString();
        location = getIntent().getExtras().get("Location").toString();

        todo.setHint("Hi " + name + ", add a todo here!");
        todo.setHintTextColor(Color.WHITE);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todos.add(todo.getText().toString());
                todo_adapter.notifyDataSetChanged();
                todo.setText("");
            }
        });

        todos = new ArrayList<String>();
        todo_adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_checked, todos);
        todo_list.setAdapter(todo_adapter);
        todo_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        todo_list.performClick();

        //credits to kyogs (http://stackoverflow.com/a/15062429)
        //nested scrolling: scroll child listview nested inside parent scrollview
        scroll_view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                findViewById(R.id.todo_list).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        todo_list.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        getLocation();
    }

    public void initializeViews() {
        scroll_view = (ScrollView) findViewById(R.id.scroll_view);
        greeting = (ImageView) findViewById(R.id.greeting);
        title = (TextView) findViewById(R.id.title);
        todo = (EditText) findViewById(R.id.todo);
        add = (Button) findViewById(R.id.add);
        todo_list = (ListView) findViewById(R.id.todo_list);
        map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
    }

    public void setGreeting() {
        Calendar now = GregorianCalendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            greeting.setImageDrawable(getResources().getDrawable(R.drawable.morning));
        } else if (hour < 18) {
            greeting.setImageDrawable(getResources().getDrawable(R.drawable.afternoon));
        } else if (hour < 20) {
            greeting.setImageDrawable(getResources().getDrawable(R.drawable.evening));
        } else {
            greeting.setImageDrawable(getResources().getDrawable(R.drawable.night));
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                LatLng destination = getLatLng(location);
                getDirections(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), destination);

                PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.BLUE);
                for (int poly = 0; poly < directions.size(); poly++) {
                    if (poly == 0 || poly == directions.size() - 1) {
                        MarkerOptions markerOptions = new MarkerOptions().position(directions.get(poly));
                        markers.add(markerOptions);
                        googleMap.addMarker(markerOptions);
                    }
                    rectLine.add(directions.get(poly));
                }

                Polyline polyLine = googleMap.addPolyline(rectLine);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (MarkerOptions marker : markers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                int padding = 100;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                googleMap.moveCamera(cu);
            }
        });
    }

    public void getLocation() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mCriteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(mCriteria, true);
        mLocation = mLocationManager.getLastKnownLocation(mProvider);
        if (mLocation != null) {
            onLocationChanged(mLocation);
        }
        mLocationManager.requestLocationUpdates(mProvider, 20000, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        latLng = new LatLng(latitude, longitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public LatLng getLatLng(String location) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        double lng = 0.0;
        double lat = 0.0;
        location.replace(" ", "+");
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + location + "&sensor=true";
        try {
            String data = run(url);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(data.toString());

                lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new LatLng(lat, lng);
    }

    public void getDirections(LatLng from, LatLng to) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + from.latitude + "," + from.longitude + "&destination=" + to.latitude + "," + to.longitude;
        try {
            String data = run(url);
            JSONObject jsonObject;
            ArrayList<String> polylines = new ArrayList<>();
            try {
                jsonObject = new JSONObject(data.toString());

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
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}

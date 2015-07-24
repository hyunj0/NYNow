package nyteam.c4q.nyc.nynow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Home extends ActionBarActivity implements OnMapReadyCallback, LocationListener {

    private ScrollView scroll_view;
    private ImageView greeting;
    private TextView title;
    private EditText todo;
    private Button add;
    private ListView todo_list;
    private MapFragment map;

    private LocationManager mLocationManager;
    private Criteria mCriteria;
    private String mProvider;
    private Location mLocation;

    private JSONAsyncTask latLng_jsonAsyncTask, directions_jsonAsyncTask;
    private LatLngAsyncTask latLngAsyncTask;
    private DirectionsAsyncTask directionsAsyncTask;

    private String name, location;
    private ArrayList<String> todos;
    private ArrayAdapter<String> todo_adapter;
    private double latitude, longitude;
    private ArrayList<LatLng> directions = new ArrayList<LatLng>();
    private ArrayList<MarkerOptions> markers = new ArrayList<>();

    public static final String TAG = MainActivity.class.getSimpleName();
    private Forecast mForecast;
    @InjectView(R.id.time) TextView mTimeLabel;
    @InjectView(R.id.temperature) TextView mTemperatureLabel;
    @InjectView(R.id.humidity) TextView mHumidityValue;
    @InjectView(R.id.precip) TextView mPrecipValue;
    @InjectView(R.id.icon_image) ImageView mIconImageView;
    @InjectView(R.id.refresh) ImageView mRefreshView;
    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initializeViews();
        initializeAsyncTasks();
        setGreeting();

        name = getIntent().getExtras().get("Name").toString();
        location = getIntent().getExtras().get("Location").toString();

        todo.setHint("Hi " + name + ", add a todo here!");
        todo.setHintTextColor(Color.WHITE);

        mRefreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWeather(mLocation);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (todo.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No todo added", Toast.LENGTH_LONG).show();
                } else {
                    todos.add(todo.getText().toString());
                    todo_adapter.notifyDataSetChanged();
                    todo.setText("");
                }
            }
        });

        todos = new ArrayList<String>();
        todo_adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, todos);
        todo_list.setAdapter(todo_adapter);
        todo_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                todos.remove(i);
                todo_adapter.notifyDataSetChanged();
                return false;
            }
        });

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

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mCriteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(mCriteria, true);
        mLocation = mLocationManager.getLastKnownLocation(mProvider);
        if (mLocation != null) {
            onLocationChanged(mLocation);
        }
        mLocationManager.requestLocationUpdates(mProvider, 20000, 0, this);

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

    public void initializeAsyncTasks() {
        latLng_jsonAsyncTask = new JSONAsyncTask();
        directions_jsonAsyncTask = new JSONAsyncTask();
        latLngAsyncTask = new LatLngAsyncTask();
        directionsAsyncTask = new DirectionsAsyncTask();
    }

    public void setGreeting() {
        Calendar now = GregorianCalendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) {
            greeting.setImageDrawable(getResources().getDrawable(R.drawable.morning));
        } else if (hour >= 12 && hour < 18) {
            greeting.setImageDrawable(getResources().getDrawable(R.drawable.afternoon));
        } else if (hour >= 18 && hour < 20) {
            greeting.setImageDrawable(getResources().getDrawable(R.drawable.evening));
        } else {
            greeting.setImageDrawable(getResources().getDrawable(R.drawable.night));
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                location.replace(" ", "+");
                String latLng_url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + location + "&sensor=true";
                latLng_jsonAsyncTask.execute(latLng_url);

                try {
                    latLngAsyncTask.execute(latLng_jsonAsyncTask.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


                LatLng destination = null;
                try {
                    destination = latLngAsyncTask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


                String directions_url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + mLocation.getLatitude() + "," + mLocation.getLongitude() + "&destination=" + destination.latitude + "," + destination.longitude;

                directions_jsonAsyncTask.execute(directions_url);
                String data = null;
                try {
                    data = directions_jsonAsyncTask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                directionsAsyncTask.execute(data);
                try {
                    directions = directionsAsyncTask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


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

//   <------------------------------------Weather---------------------------------------->

    private void getWeather( Location location ) {
        // API for live weather
        String apiKey = "16b8dffff056e3ba845b90150ade2a98";
        String weatherUrl = "https://api.forecast.io/forecast/" + apiKey + "/" + location.getLatitude() + "," + location.getLongitude();

        if (NetworkAvailability()) {

            OkHttpClient Client = new OkHttpClient();
            Request request = new Request.Builder().url(weatherUrl).build();

            Call call = Client.newCall(request);
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() { // A new runnable to run on UI thread
                        @Override
                        public void run() {
                            toggleForRefresh(); // This need to run on the UI thread
                        }
                    });
                    alertUser();
                }

                @Override
                public void onResponse(Response response) throws IOException {

                    runOnUiThread(new Runnable() {  // A new runnable to run on UI thread
                        @Override
                        public void run() {
                            toggleForRefresh(); // This need to run on the UI thread
                        }
                    });

                    try {
                        String jsonData = response.body().string(); //Stores the json data into String
                        Log.v(TAG, jsonData); // take the response body and turn it into a string representation of the body, not toString().
                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updatedScreen();
                                }
                            });
                        } else {
                            alertUser();
                        }                                      //Catch all exceptions here.
                    } catch (IOException e) {
                        Log.e(TAG, "Exception Found", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception Found", e);
                    }
                }
            });
        } else {

            Toast.makeText(this, getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
        }
    }

    private void toggleForRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {

            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshView.setVisibility(View.VISIBLE);
        }
    }

    // method to get the data parse data by linking the widget to current and each info.
    private void updatedScreen() {

        Current current = mForecast.getCurrent();

        Drawable drawableIcon = getResources().getDrawable(current.getIconId());

        mTemperatureLabel.setText(current.getTemperature() + "");
        mTimeLabel.setText("Last updated at " + current.getFormattedTime());
        mHumidityValue.setText(current.getHumidity() + "");
        mPrecipValue.setText(current.getPrecipitation() + " %");
        mIconImageView.setImageDrawable(drawableIcon);
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();
        forecast.setCurrent(getCurrentDetails(jsonData));
        return forecast;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException { // throws for one exception.
        // declaring a new JSON object
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        Log.i(TAG, "From JSON: " + timezone);

        // get info from JSON Object "currently"
        JSONObject currentInfo = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setHumidity(currentInfo.getDouble("humidity"));
        current.setTime(currentInfo.getLong("time"));
        current.setIcon(currentInfo.getString("icon"));
        current.setPrecipitation(currentInfo.getDouble("precipProbability"));
        current.setTemperature(currentInfo.getDouble("temperature"));
        current.setSummary(currentInfo.getString("summary"));
        current.setTimeZone(timezone);
        Log.d(TAG, current.getFormattedTime()); // convert time to simpleDateFormat

        return current;
    }


    // Method to determine if there is a connection.
    private boolean NetworkAvailability() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = manager.getActiveNetworkInfo(); // determine it is both present and active. IFY need network permission.

        Boolean isAvailable = false;

        // !=null checks for present and isConnected checks for availability.
        if (networkinfo != null && networkinfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    // Method to shows user AlertDialogFragment message when there is an error.
    private void alertUser() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getSupportFragmentManager(), "error_message");
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
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
}

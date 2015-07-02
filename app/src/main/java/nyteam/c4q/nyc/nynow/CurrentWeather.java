package nyteam.c4q.nyc.nynow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dexafree.materialList.cards.BasicButtonsCard;
import com.dexafree.materialList.view.MaterialListView;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by c4q-vanice on 7/2/15.
 */
public class CurrentWeather extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST"; // All caps is a standard indicate of a static variable.
    public static final String HOUR_FORECAST = "HOUR_FORECAST";

    private Forecast mForecast;
    private MaterialListView mListView;

    @InjectView(R.id.time) TextView mTimeLabel;
    @InjectView(R.id.temperature) TextView mTemperatureLabel;
    @InjectView(R.id.icon_image) ImageView mIconImageView;
    @InjectView(R.id.refresh) ImageView mRefreshView;
    @InjectView(R.id.progressBar)ProgressBar mProgressBar;
    @InjectView(R.id.humidity) TextView mHumidityValue;
    @InjectView(R.id.precip) TextView mPrecipValue;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);
        ButterKnife.inject(this);
        initializeViews();

// Library for declaring variables.

//       mProgressBar.setVisibility(View.INVISIBLE);
        final double latitude = 40.748817;
        final double longitude = -74.0059;


        mRefreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWeather(latitude, longitude);
            }
        });

        mTemperatureLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BasicButtonsCard weatherCard = new BasicButtonsCard(getApplicationContext());
                weatherCard.setTitle("Weather");
                weatherCard.setDescription("New York");
                mListView.add(weatherCard);
            }
        });

        getWeather(latitude, longitude);


        Log.d(TAG, "Main UI thread is running"); // use only for debugging.

    }

    public void initializeViews() {
        ButterKnife.inject(this);
        mListView = (MaterialListView) findViewById(R.id.material_listview);
    }

    private void getWeather(double latitude, double longitude ) {
        // API for live weather
        String apiKey = "16b8dffff056e3ba845b90150ade2a98";
        String weatherUrl = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + "," + longitude;

        if (NetworkAvailability()) {

            toggleForRefresh();

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
        }
        else{

            Toast.makeText(this, getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
        }
    }

    // TODO: toggleForRefresh() needs to run on the UI thread because it calls get Current

    private void toggleForRefresh() {
        if( mProgressBar.getVisibility() == View.INVISIBLE){

            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshView.setVisibility(View.INVISIBLE);}
        else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshView.setVisibility(View.VISIBLE);}
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
        forecast.setDaily(getDailyDetails(jsonData));
        return forecast;
    }

    private Daily[] getDailyDetails(String jsonData) throws JSONException {
        // declaring a new JSON object
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject dailies = forecast.getJSONObject("daily");
        JSONArray data = dailies.getJSONArray("data");

        Daily [] days = new Daily[data.length()];

        for (int i = 0; i < data.length(); i++){
            JSONObject jsonDaily = data.getJSONObject(i);
            Daily day = new Daily();

            day.setSummary(jsonDaily.getString("summary"));
            day.setIcon(jsonDaily.getString("icon"));
            day.setTemperatureMax(jsonDaily.getDouble("temperatureMax"));
            day.setTime(jsonDaily.getLong("time"));
            day.setTimeZone(timezone);
            // This is referencing to the days [] made from line 182.
            days[i] = day;
        }
        return days;
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
        if (networkinfo != null && networkinfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    // Method to shows user AlertDialogFragment message when there is an error.
    private void alertUser() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getSupportFragmentManager(), "error_message");
    }
}

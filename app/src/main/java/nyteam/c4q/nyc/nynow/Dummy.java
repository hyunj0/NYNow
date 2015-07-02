package nyteam.c4q.nyc.nynow;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class Dummy extends ActionBarActivity implements OnMapReadyCallback {

    private MapFragment map;
    RelativeLayout dummy;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    Notification notification;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);

        dummy = (RelativeLayout) findViewById(R.id.dummy);

        map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        if (isNetworkAvailable()) {
            map.getMapAsync(this);
        } else {


            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setContentTitle("Network Restricted");
            builder.setContentText("Enable network capability");
            builder.setSmallIcon(R.drawable.ic_stat_maps_directions_transit);
            notification = builder.build();
            notificationManager.notify(1, notification);
            MapView mapView = new MapView(this);
            dummy.addView(mapView);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onPause() {
        super.onPause();
        saveSharedPreferences();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveSharedPreferences();
    }

    public void saveSharedPreferences () {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name", "name");
        editor.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dummy, menu);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.getMap().getCameraPosition();
    }
}

package nyteam.c4q.nyc.nynow;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dexafree.materialList.cards.BasicListCard;
import com.dexafree.materialList.cards.BigImageCard;
import com.dexafree.materialList.cards.WelcomeCard;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.MaterialListView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Home extends ActionBarActivity {

    private MaterialListView mListView;
    String name, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        name = getIntent().getExtras().get("Name").toString();
        location = getIntent().getExtras().get("Location").toString();

        mListView = (MaterialListView) findViewById(R.id.material_listview);

        Calendar now = GregorianCalendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        String timeOfDay;
        if (hour < 12) {
            timeOfDay = "Good Morning";
        } else if (hour < 18) {
            timeOfDay = "Good Afternoon";
        } else if (hour < 20) {
            timeOfDay = "Good evening";
        } else {
            timeOfDay = "Good Night";
        }

        WelcomeCard welcome = new WelcomeCard(getApplicationContext());
        welcome.setBackgroundColor(Color.BLUE);
        welcome.setTitle(timeOfDay);
        welcome.setSubtitle(name);
        welcome.setDescription("Current Location: " + location);

        mListView.add(welcome);
    }
}

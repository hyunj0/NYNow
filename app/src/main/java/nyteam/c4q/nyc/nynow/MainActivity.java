package nyteam.c4q.nyc.nynow;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.FrameLayout;

import nyteam.c4q.nyc.nynow.Fragments.Feed;
import nyteam.c4q.nyc.nynow.Fragments.Welcome;

public class MainActivity extends ActionBarActivity {

    private FrameLayout feed_container, content_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Tablet Mode
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.feed_container, new Feed());
        ft.add(R.id.content_container, new Welcome());
        ft.commit();
    }
}

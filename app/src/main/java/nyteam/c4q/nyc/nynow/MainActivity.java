package nyteam.c4q.nyc.nynow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {

    private EditText name, location;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (EditText) findViewById(R.id.name);
        location = (EditText) findViewById(R.id.location);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent show = new Intent(getApplicationContext(), Home.class);
                show.putExtra("Name", name.getText().toString());
                show.putExtra("Location", location.getText().toString());
                startActivity(show);
            }
        });
    }
}

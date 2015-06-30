package nyteam.c4q.nyc.nynow;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dexafree.materialList.cards.BasicListCard;
import com.dexafree.materialList.view.MaterialListView;


public class ToDo extends ActionBarActivity {

    private EditText todo;
    private Button enter;
    private MaterialListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        initializeViews();

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BasicListCard todoCard = new BasicListCard(getApplicationContext());
                todoCard.setTitle(todo.getText().toString());
                todoCard.setDescription("Swipe to remove");
                todoCard.setDismissible(true);
                mListView.add(todoCard);
            }
        });
    }

    public void initializeViews() {
        todo = (EditText) findViewById(R.id.todo);
        enter = (Button) findViewById(R.id.enter);
        mListView = (MaterialListView) findViewById(R.id.material_listview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo, menu);
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

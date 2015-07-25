package nyteam.c4q.nyc.nynow.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import nyteam.c4q.nyc.nynow.R;

public class Welcome extends Fragment {

    private EditText name, location;
    private Button submit;

    public Welcome() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.welcome, container, false);
        name = (EditText) v.findViewById(R.id.name);
        location = (EditText) v.findViewById(R.id.location);
        submit = (Button) v.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return v;
    }
}

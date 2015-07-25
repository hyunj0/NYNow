package nyteam.c4q.nyc.nynow.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import nyteam.c4q.nyc.nynow.Card.Card;
import nyteam.c4q.nyc.nynow.Card.GreetingCard;
import nyteam.c4q.nyc.nynow.Card.MapCard;
import nyteam.c4q.nyc.nynow.Card.MusicCard;
import nyteam.c4q.nyc.nynow.Card.ToDoCard;
import nyteam.c4q.nyc.nynow.Card.WeatherCard;
import nyteam.c4q.nyc.nynow.CardAdapter.CardAdapter;
import nyteam.c4q.nyc.nynow.R;

public class Feed extends Fragment {

    private RecyclerView feed_list;

    private List<Card> cards;

    public Feed() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.feed, container, false);
        feed_list = (RecyclerView) v.findViewById(R.id.feed_list);
        cards = new ArrayList<Card>();
        cards.add(new GreetingCard());
        cards.add(new ToDoCard());
        cards.add(new MapCard());
        cards.add(new WeatherCard());
        cards.add(new MusicCard());
        feed_list.setAdapter(new CardAdapter(cards));
        return v;
    }
}

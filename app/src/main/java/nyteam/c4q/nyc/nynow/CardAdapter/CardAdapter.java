package nyteam.c4q.nyc.nynow.CardAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import nyteam.c4q.nyc.nynow.Card.Card;
import nyteam.c4q.nyc.nynow.Card.GreetingCard;
import nyteam.c4q.nyc.nynow.Card.MapCard;
import nyteam.c4q.nyc.nynow.Card.MusicCard;
import nyteam.c4q.nyc.nynow.Card.ToDoCard;
import nyteam.c4q.nyc.nynow.Card.WeatherCard;
import nyteam.c4q.nyc.nynow.CardViewHolder.GreetingCardViewHolder;
import nyteam.c4q.nyc.nynow.CardViewHolder.MapCardViewHolder;
import nyteam.c4q.nyc.nynow.CardViewHolder.MusicCardViewHolder;
import nyteam.c4q.nyc.nynow.CardViewHolder.ToDoCardViewHolder;
import nyteam.c4q.nyc.nynow.CardViewHolder.WeatherCardViewHolder;
import nyteam.c4q.nyc.nynow.R;

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Card> cards;

    public static final int VIEW_TYPE_GREETING = 0;
    public static final int VIEW_TYPE_TODO = 1;
    public static final int VIEW_TYPE_MAP = 2;
    public static final int VIEW_TYPE_WEATHER = 3;
    public static final int VIEW_TYPE_MUSIC = 4;

    public CardAdapter(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public int getItemViewType(int position) {
        if (cards.get(position) instanceof GreetingCard)
            return VIEW_TYPE_GREETING;
        else if (cards.get(position) instanceof ToDoCard)
            return VIEW_TYPE_TODO;
        else if (cards.get(position) instanceof MapCard)
            return VIEW_TYPE_MAP;
        else if (cards.get(position) instanceof WeatherCard)
            return VIEW_TYPE_WEATHER;
        else if (cards.get(position) instanceof MusicCard)
            return VIEW_TYPE_MUSIC;
        else
            return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View v;
        RecyclerView.ViewHolder vh = null;
        switch(viewType) {
            case VIEW_TYPE_GREETING:
                v = layoutInflater.inflate(R.layout.card_greeting, viewGroup, false);
                vh = new GreetingCardViewHolder(v);
                break;
            case VIEW_TYPE_TODO:
                v = layoutInflater.inflate(R.layout.card_todo, viewGroup, false);
                vh = new ToDoCardViewHolder(v);
                break;
            case VIEW_TYPE_MAP:
                v = layoutInflater.inflate(R.layout.card_map, viewGroup, false);
                vh = new MapCardViewHolder(v);
                break;
            case VIEW_TYPE_WEATHER:
                v = layoutInflater.inflate(R.layout.card_weather, viewGroup, false);
                vh = new WeatherCardViewHolder(v);
                break;
            case VIEW_TYPE_MUSIC:
                v = layoutInflater.inflate(R.layout.card_music, viewGroup, false);
                vh = new MusicCardViewHolder(v);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = viewHolder.getItemViewType();
        switch (viewType) {
            case VIEW_TYPE_GREETING:
                Card card = cards.get(position);
                GreetingCardViewHolder greetingCardViewHolder = (GreetingCardViewHolder) viewHolder;
                greetingCardViewHolder.greeting_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                break;
            case VIEW_TYPE_TODO:
                ToDoCard toDoCard = (ToDoCard) cards.get(position);
                ToDoCardViewHolder toDoCardViewHolder = (ToDoCardViewHolder) viewHolder;
                toDoCardViewHolder.todo_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                break;
            case VIEW_TYPE_MAP:
                MapCard mapCard = (MapCard) cards.get(position);
                MapCardViewHolder mapCardViewHolder = (MapCardViewHolder) viewHolder;
                mapCardViewHolder.map_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                break;
            case VIEW_TYPE_WEATHER:
                WeatherCard weatherCard = (WeatherCard) cards.get(position);
                WeatherCardViewHolder weatherCardViewHolder = (WeatherCardViewHolder) viewHolder;
                weatherCardViewHolder.weather_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                break;
            case VIEW_TYPE_MUSIC:
                MusicCard musicCard = (MusicCard) cards.get(position);
                MusicCardViewHolder musicCardViewHolder = (MusicCardViewHolder) viewHolder;
                musicCardViewHolder.music_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}


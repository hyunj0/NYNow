package nyteam.c4q.nyc.nynow.CardViewHolder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import nyteam.c4q.nyc.nynow.R;

public class WeatherCardViewHolder extends RecyclerView.ViewHolder {
    public CardView weather_card;

    public WeatherCardViewHolder(View itemView) {
        super(itemView);
        weather_card = (CardView) itemView.findViewById(R.id.weather_card);
    }
}

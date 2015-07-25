package nyteam.c4q.nyc.nynow.CardViewHolder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import nyteam.c4q.nyc.nynow.R;

public class GreetingCardViewHolder extends RecyclerView.ViewHolder {
    public CardView greeting_card;

    public GreetingCardViewHolder(View itemView) {
        super(itemView);
        greeting_card = (CardView) itemView.findViewById(R.id.greeting_card);
    }
}

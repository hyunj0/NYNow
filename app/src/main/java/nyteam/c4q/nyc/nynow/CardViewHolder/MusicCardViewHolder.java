package nyteam.c4q.nyc.nynow.CardViewHolder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import nyteam.c4q.nyc.nynow.R;

public class MusicCardViewHolder extends RecyclerView.ViewHolder {
    public CardView music_card;

    public MusicCardViewHolder(View itemView) {
        super(itemView);
        music_card = (CardView) itemView.findViewById(R.id.music_card);
    }
}

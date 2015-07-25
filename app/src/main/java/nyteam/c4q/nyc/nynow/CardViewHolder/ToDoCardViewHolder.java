package nyteam.c4q.nyc.nynow.CardViewHolder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import nyteam.c4q.nyc.nynow.R;

public class ToDoCardViewHolder extends RecyclerView.ViewHolder {
    public CardView todo_card;

    public ToDoCardViewHolder(View itemView) {
        super(itemView);
        todo_card = (CardView) itemView.findViewById(R.id.todo_card);
    }
}

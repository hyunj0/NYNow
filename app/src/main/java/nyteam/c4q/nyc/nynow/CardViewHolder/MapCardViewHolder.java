package nyteam.c4q.nyc.nynow.CardViewHolder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import nyteam.c4q.nyc.nynow.R;

public class MapCardViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

    public CardView map_card;

    public MapCardViewHolder(View itemView) {
        super(itemView);
        map_card = (CardView) itemView.findViewById(R.id.map_card);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
    }
}

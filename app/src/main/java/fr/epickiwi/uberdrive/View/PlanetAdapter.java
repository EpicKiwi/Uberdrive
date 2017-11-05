package fr.epickiwi.uberdrive.View;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fr.epickiwi.uberdrive.PlanetActivity;
import fr.epickiwi.uberdrive.R;
import fr.epickiwi.uberdrive.model.Planet;

public class PlanetAdapter extends RecyclerView.Adapter<PlanetAdapter.PlanetViewHolder> {

    private ArrayList<Planet> planetList = new ArrayList<>();

    @Override
    public PlanetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.planet_view,parent,false);
        return new PlanetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanetViewHolder holder, int position) {
        Planet planet = this.planetList.get(position);
        holder.display(planet);
    }

    @Override
    public int getItemCount() {
        return this.planetList.size();
    }

    public class PlanetViewHolder extends RecyclerView.ViewHolder {
        private final TextView distanceText;
        private final TextView nameText;
        private final TextView climateText;
        private LinearLayout planetContainer;
        private Planet currPlanet;

        public PlanetViewHolder(final View itemView) {
            super(itemView);

            this.nameText = itemView.findViewById(R.id.planetNameText);
            this.distanceText = itemView.findViewById(R.id.planetDistanceText);
            this.climateText = itemView.findViewById(R.id.planetClimate);
            this.planetContainer = itemView.findViewById(R.id.planetContainer);
            this.planetContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, PlanetActivity.class);
                    intent.putExtra("name",currPlanet.getName());
                    context.startActivity(intent);
                }
            });
        }

        public void display(Planet planet){
            currPlanet = planet;
            this.nameText.setText(planet.getName());
            Resources res = this.itemView.getResources();
            this.distanceText.setText(res.getString(R.string.distanceUnit,planet.getDistance()));
            this.climateText.setText(planet.getClimate().equals("unknown") ? "" : planet.getClimate());
            this.planetContainer = itemView.findViewById(R.id.planetContainer);
        }
    }

    public ArrayList<Planet> getPlanetList() {
        return planetList;
    }
}

package fr.epickiwi.uberdrive.View;

import android.app.Activity;
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

import fr.epickiwi.uberdrive.R;
import fr.epickiwi.uberdrive.SimulationActivity;
import fr.epickiwi.uberdrive.model.Spaceship;

public class SpaceshipAdapter extends RecyclerView.Adapter<SpaceshipAdapter.PlanetViewHolder> {

    private final String heritedPlanetName;
    private ArrayList<Spaceship> spaceshipList = new ArrayList<>();

    public SpaceshipAdapter(String planetName) {
        this.spaceshipList = new ArrayList<>();
        this.heritedPlanetName = planetName;
    }

    @Override
    public PlanetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.spaceship_view,parent,false);
        return new PlanetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanetViewHolder holder, int position) {
        Spaceship ship = this.spaceshipList.get(position);
        holder.display(ship);
    }

    @Override
    public int getItemCount() {
        return this.spaceshipList.size();
    }

    public class PlanetViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameText;
        private final TextView speedText;
        private final TextView priceText;
        private final LinearLayout container;

        private Spaceship curreSpaceship;

        public PlanetViewHolder(final View itemView) {
            super(itemView);

            this.nameText = itemView.findViewById(R.id.shipNameText);
            this.speedText = itemView.findViewById(R.id.shipSpeedText);
            this.priceText = itemView.findViewById(R.id.shipPriceText);
            this.container = itemView.findViewById(R.id.spaceshipContainer);
            this.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, SimulationActivity.class);
                    intent.putExtra("planetName",heritedPlanetName);
                    intent.putExtra("spaceshipName",curreSpaceship.getName());
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            });
        }

        public void display(Spaceship ship){
            this.curreSpaceship = ship;
            Resources res = this.itemView.getResources();
            this.nameText.setText(ship.getName());
            this.speedText.setText(res.getString(R.string.shipSpeedUnit,ship.getSpeed()));
            this.priceText.setText(res.getString(R.string.shipPriceUnit,String.valueOf(ship.getPrice())));

        }
    }

    public ArrayList<Spaceship> getSpaceshipList() {
        return spaceshipList;
    }
}

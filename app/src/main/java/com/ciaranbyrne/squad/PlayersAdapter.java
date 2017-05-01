package com.ciaranbyrne.squad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ciaranbyrne on 20/04/2017.
 */

public class PlayersAdapter extends ArrayAdapter<Player> {
    public PlayersAdapter(Context context, int resource, List<Player> objects) {
        super(context, resource, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Player player = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_entry, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.playerNameTextView);
        TextView tvPhone = (TextView) convertView.findViewById(R.id.playerPhoneTextView);
        // Populate the data into the template view using the data object
        tvName.setText(player.name);
        tvPhone.setText(player.phoneNum);
        // Return the completed view to render on screen
        return convertView;
    }
}


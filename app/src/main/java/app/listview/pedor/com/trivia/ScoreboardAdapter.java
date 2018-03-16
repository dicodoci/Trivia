package app.listview.pedor.com.trivia;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

// Adapter for the scoreboard list
public class ScoreboardAdapter extends ArrayAdapter<DataSnapshot> {
    private ArrayList<DataSnapshot> scoreArray;
    private String userName;

    // Constructor
    public ScoreboardAdapter(@NonNull Context context, int resource, @NonNull ArrayList<DataSnapshot> objects, String name) {
        super(context, resource, objects);
        scoreArray = objects;
        userName = name;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.scoreboard_item, parent, false);
        }

        // Get all info views
        TextView nameView = convertView.findViewById(R.id.name);
        TextView scoreView = convertView.findViewById(R.id.score);

        // Set all views with correct information
        String key = scoreArray.get(position).getKey();
        // If userName equals database key give that entry a nice color
        if( key.equals(userName)) {
            nameView.setTextColor(Color.parseColor("#DC9696"));
            scoreView.setTextColor(Color.parseColor("#DC9696"));
        } else {
            nameView.setTextColor(Color.parseColor("#606060"));
            scoreView.setTextColor(Color.parseColor("#606060"));
        }
        nameView.setText(key);
        scoreView.setText(scoreArray.get(position).getValue().toString());
        return convertView;
    }
}

package app.listview.pedor.com.trivia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// Activity to show scoreboard
public class ScoreboardActivity extends AppCompatActivity {

    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        // Get name of user that just put a score on the scoreboard if it exists
        Intent intent = getIntent();
        final String name = (String) intent.getSerializableExtra("name");

        // Set button listeners
        Button newGameButton = findViewById(R.id.scoreboardNewGame);
        newGameButton.setOnClickListener(new ScoreboardActivity.NewGameOnClickListener());
        Button homeButton = findViewById(R.id.home);
        homeButton.setOnClickListener(new ScoreboardActivity.HomeOnClickListener());

        // Get Firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("database");

        // Collect data from Firebase database a single time
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Put data in arraylist
                ArrayList<DataSnapshot> scoreList = new ArrayList<>();
                for (DataSnapshot scores : snapshot.getChildren()) {
                    for (DataSnapshot user : scores.getChildren()) {
                        scoreList.add(user);
                    }
                }

                // Sort filled array list on score amount
                Collections.sort(scoreList, new Comparator<DataSnapshot>() {
                    @Override
                    public int compare(DataSnapshot t1, DataSnapshot t2) {
                        return Integer.parseInt(t2.getValue().toString()) -
                                Integer.parseInt(t1.getValue().toString());
                    }
                });

                // Set custom adapter on listView
                ListView scoresView = findViewById(R.id.scoreboardList);
                ScoreboardAdapter adapter = new ScoreboardAdapter(ScoreboardActivity.this,
                        R.layout.scoreboard_item, scoreList, name);
                scoresView.setAdapter(adapter);
            }

            // On error getting data show error and return to previous view
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ScoreboardActivity.this, "Database Error", Toast.LENGTH_LONG).show();
                Log.w("scoreboardActivity", "loadPost:onCancelled", databaseError.toException());
                finish();
            }
        });
    }

    // Listener for New Game button
    private class NewGameOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ScoreboardActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Listener for Home button
    private class HomeOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ScoreboardActivity.this, OpeningActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

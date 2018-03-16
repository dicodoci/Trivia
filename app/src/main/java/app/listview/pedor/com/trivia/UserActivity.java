package app.listview.pedor.com.trivia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Activity that shows user score and gives option to add score to the scoreboard
public class UserActivity extends AppCompatActivity {

    int score;
    DatabaseReference myRef;
    EditText userNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Get FireBase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("database");

        // Get score from the mainActivity
        Intent intent = getIntent();
        score = (int) intent.getSerializableExtra("score");
        TextView pointsView = findViewById(R.id.points);
        pointsView.setText(Integer.toString(score));

        // Add listeners
        Button addButton = findViewById(R.id.addScoreBoard);
        Button scoreboardButton = findViewById(R.id.scoreboard);
        Button newGameButton = findViewById(R.id.newGame);
        Button homeButton = findViewById(R.id.home);
        userNameInput = findViewById(R.id.username);
        addButton.setOnClickListener(new AddScoreOnClickListener());
        scoreboardButton.setOnClickListener(new ScoreboardOnClickListener());
        newGameButton.setOnClickListener(new NewGameOnClickListener());
        homeButton.setOnClickListener(new HomeOnClickListener());
        userNameInput.setOnEditorActionListener(new onEditorActionListener());
    }

    // Add score to the FireBase under the username filled in
    private void addScore() {
        String user = userNameInput.getText().toString();
        // Create Toast and do nothing if no name is filled in
        if (user.equals("")) {
            Toast.makeText(this, "Fill in a name", Toast.LENGTH_LONG).show();
        } else {
            myRef.child("users").child(user).setValue(score);
            toScoreboard();
        }
    }

    // Go to the scoreboard activity
    private void toScoreboard() {
        Intent intent = new Intent(UserActivity.this, ScoreboardActivity.class);
        intent.putExtra("name", userNameInput.getText().toString());
        startActivity(intent);
    }

    // Listener for "Add Score to Scoreboard" button
    private class AddScoreOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            addScore();
        }
    }

    // Listener for "New Game" button
    private class NewGameOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(UserActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Listener for "Home" button
    private class HomeOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(UserActivity.this, OpeningActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Listener for 'Scoreboard' Button
    private class ScoreboardOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            toScoreboard();
        }
    }

    // Listener for "done" button in the keyboard
    // Does the same as the "Add Score to Scoreboard" button
    private class onEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addScore();
            }
            return false;
        }
    }
}

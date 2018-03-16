package app.listview.pedor.com.trivia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

// Activity for the home/opening screen
public class OpeningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        // Set button listeners
        Button newGameButton = findViewById(R.id.startGame);
        Button scoreboardButton = findViewById(R.id.startScoreboard);
        newGameButton.setOnClickListener(new OpeningActivity.NewGameOnClickListener());
        scoreboardButton.setOnClickListener(new OpeningActivity.ScoreboardOnClickListener());
    }

    // Listener for New Game button
    private class NewGameOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(OpeningActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    // Listener for Scoreboard button
    private class ScoreboardOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(OpeningActivity.this, ScoreboardActivity.class);
            startActivity(intent);
        }
    }
}

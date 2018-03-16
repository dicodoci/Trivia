package app.listview.pedor.com.trivia;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// Activity that takes care of the question asking and keeping track of stats

public class MainActivity extends AppCompatActivity implements TriviaHelper.Callback{

    Question question;
    Button answerButton;
    EditText answerEditText;
    double errorMargin = 0.5;
    int score;
    int lives;
    int questionNumber;
    boolean correct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set listeners
        Button homeButton = findViewById(R.id.home);
        answerButton = findViewById(R.id.nextQuestion);
        answerEditText = findViewById(R.id.answerInput);
        homeButton.setOnClickListener(new HomeOnClickListener());
        answerEditText.setOnEditorActionListener(new onEditorActionListener());
        answerButton.setOnClickListener(new CheckOnClickListener());

        if (savedInstanceState != null) {

            // collect previous state and set views according to that state
            question = (Question) savedInstanceState.getSerializable("question");
            score = savedInstanceState.getInt("score");
            lives = savedInstanceState.getInt("lives");
            questionNumber = savedInstanceState.getInt("questionNumber");
            correct = savedInstanceState.getBoolean("correct");
            String answeredState = savedInstanceState.getString("answeredState");
            answerButton.setTag(answeredState);
            gotQuestion(question);
            if(answeredState.equals("next")) {
                showAnswer(correct);
                if (lives < 0) {
                    answerButton.setText("Scores");
                } else {
                    answerButton.setText("Next Question");
                }
            }
        } else {
            // starting values
            new TriviaHelper(getApplicationContext()).getNextQuestion(this);
            answerButton.setTag("answer");
            lives = 5;
            score = 0;
            correct = false;
            questionNumber = 1;
        }
    }

    // function is called when TriviaHelper was able to generate a question
    @Override
    public void gotQuestion(Question requestedQuestion) {
        question = requestedQuestion;
        // No questions that were tagged as invalid
        if (question.getInvalidCount() > 0) {
            new TriviaHelper(getApplicationContext()).getNextQuestion(this);
            return;
        }

        // No questions that have no difficulty measure
        if (question.getDifficulty() == 0) {
            new TriviaHelper(getApplicationContext()).getNextQuestion(this);
            return;
        }

        // Update views with question info and other stats
        TextView questionView = findViewById(R.id.question);
        TextView categoryView = findViewById(R.id.category);
        TextView difficultyView = findViewById(R.id.difficulty);
        TextView scoreView = findViewById(R.id.score);
        TextView questionNumberView = findViewById(R.id.questionNumber);
        TextView livesView = findViewById(R.id.lives);
        questionView.setText(requestedQuestion.getQuestion());
        categoryView.setText(requestedQuestion.getCategory());
        String difficultyText = "Difficulty: " + requestedQuestion.getDifficulty();
        difficultyView.setText(difficultyText);
        scoreView.setText(Integer.toString(score));
        String questionNumberText = "Question " + questionNumber + ":";
        questionNumberView.setText(questionNumberText);
        answerButton.setText("Check Answer");

        // Make color of number of lives red if it's the last life
        if (lives < 1) {
            livesView.setTextColor(Color.parseColor("#DC9696"));
            livesView.setText("Lives: 0");
        } else {
            String livesText = "Lives: " + lives;
            livesView.setText(livesText);
        }
    }

    // function shows error when TriviaHelper was not able to receive questions
    @Override
    public void gotQuestionError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Function that checks similarity between correct and user answer
    // Similarity calculated using Levenshtein Edit Distance Algorithm in Java
    // https://gist.github.com/gabhi/11243437
    private boolean checkAnswer(String userAnswer) {
        String s1 = userAnswer.toLowerCase();
        String s2 = question.getAnswer().toLowerCase();
        int edits[][]=new int[s1.length()+1][s2.length()+1];
        for(int i=0;i<=s1.length();i++)
            edits[i][0]=i;
        for(int j=1;j<=s2.length();j++)
                edits[0][j]=j;
        for(int i=1;i<=s1.length();i++){
            for(int j=1;j<=s2.length();j++){
                int u=(s1.charAt(i-1)==s2.charAt(j-1)?0:1);
                edits[i][j]=Math.min(
                        edits[i-1][j]+1,
                        Math.min(edits[i][j-1]+1, edits[i-1][j-1]+u)
                    );
            }
        }
        double percentageIncorrect = ((double) edits[s1.length()][s2.length()]/(double) s2.length());
        TextView correctionView = findViewById(R.id.correction);
        if (percentageIncorrect < errorMargin) {
            return true;
        } else {
            lives--;
            return false;
        }
    }

    // Show the answer to the user and update stats based on the correctness of the user's answer
    private void showAnswer(boolean truth) {
        correct = truth;
        TextView correctionView = findViewById(R.id.correction);
        if (truth) {
            correctionView.setText("Correct\nthe answer was: " + question.getAnswer());
            correctionView.setTextColor(Color.parseColor("#96DC96"));
            int pointsQuestion = question.getDifficulty();
            if (pointsQuestion == 0) {
                score += 200;
            } else{
                score += question.getDifficulty();
            }
        } else {
            correctionView.setText("Wrong\nthe answer was: " + question.getAnswer());
            correctionView.setTextColor(Color.parseColor("#DC9696"));
            TextView livesView = findViewById(R.id.lives);
            String livesText = "Lives: " + lives;
            if (lives < 1) {
                livesView.setTextColor(Color.parseColor("#DC9696"));
            }
            livesView.setText(livesText);
        }
        correctionView.setVisibility(View.VISIBLE);
    }

    // Function for going to the next question, going to the scores or answering the question
    private void enterButton() {
        // If the question has to be answered
        if (answerButton.getTag() == "answer") {
            String userAnswer = answerEditText.getText().toString();
            showAnswer(checkAnswer(userAnswer));
            if (lives < 0) {
                answerButton.setText("Scores");
            } else {
                answerButton.setText("Next Question");
            }
            answerButton.setTag("next");
            answerEditText.setText("");
        } else {

            // If we are out of lives and are going to the scores
            if (lives < 0) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                intent.putExtra("score", score);
                startActivity(intent);
                finish();
            }

            // If we want the next question
            questionNumber++;
            new TriviaHelper(getApplicationContext()).getNextQuestion(MainActivity.this);
            System.out.println(score + " : " + lives);
            TextView correctionView = findViewById(R.id.correction);
            correctionView.setVisibility(View.INVISIBLE);
            answerButton.setTag("answer");
            answerEditText.setText("");
        }
    }

    // Listener for button "Check Answer" / "Next Question" / "Scores"
    private class CheckOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            enterButton();
        }
    }

    // Listener for Home button
    private class HomeOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, OpeningActivity.class);
            startActivity(intent);
        }
    }

    // Listener for "done" button in keyboard
    // Has the same action as "Check Answer" / "Next Question" / "Scores"
    private class onEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                enterButton();
            }
            return false;
        }
    }

    //  Save the current state.
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState); // always call super
        outState.putInt("score", score);
        outState.putSerializable("question", question);
        outState.putInt("lives", lives);
        outState.putString("answeredState", answerButton.getTag().toString());
        outState.putBoolean("correct", correct);
        outState.putInt("questionNumber", questionNumber);
    }

}

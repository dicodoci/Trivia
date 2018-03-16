package app.listview.pedor.com.trivia;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// TriviaHelper takes care of the Json download of questions
public class TriviaHelper implements Response.Listener<JSONArray>, Response.ErrorListener{
    private Context context;
    private String url = "http://jservice.io/api/random";
    private Question requestedQuestion;
    private Callback callbackActivity;

    // If error occurs send error message
    @Override
    public void onErrorResponse(VolleyError error) {
        callbackActivity.gotQuestionError(error.getMessage());
    }

    // If JSON was correctly retrieved get all question info and put them in an Question object
    @Override
    public void onResponse(JSONArray response) {
        String question;
        String answer;
        String category;
        int difficulty;
        int id;
        int invalidCount;

        try {
            JSONObject object = response.getJSONObject(0);
            question = object.getString("question");
            answer = object.getString("answer");

            // Remove things like <i> from questions
            answer = answer.replaceAll("\\<.*?\\> ?", "");

            category = object.getJSONObject("category").getString("title");
            difficulty = object.optInt("value");
            id = object.getInt("id");
            invalidCount = object.optInt("invalid_count");
            requestedQuestion = new Question(question, answer, category, difficulty, id, invalidCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callbackActivity.gotQuestion(requestedQuestion);
    }

    // Callback functions that return a question on succes and an error message on error
    public interface Callback {
        void gotQuestion(Question requestedQuestion);
        void gotQuestionError(String message);
    }

    // Constructor
    public TriviaHelper(Context context) {
        this.context = context;
    }

    // Request the JSON object from the url
    public void getNextQuestion(Callback activity) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, this, this);
        queue.add(jsonArrayRequest);
        callbackActivity = activity;
    }
}

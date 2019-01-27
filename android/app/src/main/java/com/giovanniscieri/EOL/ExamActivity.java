package com.giovanniscieri.EOL;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ExamActivity extends AppCompatActivity {
    private static final String TAG = "ExamActivity";
    private SessionManager session;
    private CustomAdapter customAdapter;
    private ListView questionListView;
    private ArrayList<Question> questionsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        questionListView = (ListView) findViewById(R.id.listView_question);

        Bundle extras = getIntent().getExtras();
        String idSet = extras.getString("idSet");
        String timerString = extras.getString("timer");
        long timer = Long.parseLong(timerString);
        timer = timer * 1000;

        questionSet(idSet);

        final CounterClass counter = new CounterClass(timer, 1000);
        counter.start();
    }


    public class CounterClass extends CountDownTimer{

        public CounterClass(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }

        public void onTick(long millisUntilFinished){
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            TextView timerView = (TextView) findViewById(R.id.timer);
            timerView.setText(hms);

        }

        public void onFinish(){
            getResponseAndFinish();
        }
    }


    private void questionSet(final String idSet) {
        String tag_string_req = "req_question";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_QUESTION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Question set Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        JSONArray jsonArrayQuestions  = jObj.getJSONArray("question");
                        ArrayList<Answer> currentAnswerList;
                        for(int i=0; i<jsonArrayQuestions.length(); i++){
                            //per ogni domanda
                            currentAnswerList = new ArrayList<>();
                            JSONObject jsonObjectQuestion = jsonArrayQuestions.getJSONObject(i);
                            Question q = new Question();
                            JSONArray jsonArrayAnswers =jsonObjectQuestion.getJSONArray("answers");
                            for(int j=0; j<jsonArrayAnswers.length(); j++){
                                //per ogni risposta i
                                JSONObject jsonObjectAnswer = jsonArrayAnswers.getJSONObject(j);
                                Answer a = new Answer();
                                a.setIdAnswer(jsonObjectAnswer.getString("idAnswer"));
                                a.setText(processString(jsonObjectAnswer.getString("translation")));
                                currentAnswerList.add(a);
                            }
                            q.setIdQuestion(jsonObjectQuestion.getString("idQuestion"));
                            q.setQuestion(processString((i+1)+")"+" "+jsonObjectQuestion.getString("translation")));
                            q.setType(jsonObjectQuestion.getString("type"));
                            q.setAnswersList(currentAnswerList);

                            questionsList.add(q);

                        }

                        customAdapter = new CustomAdapter(getApplicationContext(), questionsList);
                        questionListView.setAdapter(customAdapter);

                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Question set Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idSet", idSet);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void submitTest(final String idSet, final JSONArray testQuestions, final JSONArray testAnswers) {
        String tag_string_req = "submit test";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SUBMIT_TEST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Submit test Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        String info = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                        Intent i = new Intent(ExamActivity.this, SubjectActivity.class);
                        ExamActivity.this.finish();
                        startActivity(i);

                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Submit test Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idSet", idSet);
                params.put("questions", testQuestions.toString());
                params.put("answers", testAnswers.toString());


                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public String processString(String html) {
        Spanned spanned;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            spanned = Html.fromHtml(html);
        }
        return spanned.toString();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exam, menu);

        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.consegna) {

            AlertDialog.Builder adb = new AlertDialog.Builder(ExamActivity.this);
            adb.setTitle("Consegnare il test?");
            adb.setIcon(android.R.drawable.ic_dialog_alert);
            adb.setPositiveButton("CONSEGNA", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //getResponseAndFinish();
                    Toast.makeText(getApplicationContext(), "Test consegnato correttamente", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ExamActivity.this, SubjectActivity.class);
                    ExamActivity.this.finish();
                    startActivity(i);

                }
            });
            adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                } });
            adb.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getResponseAndFinish(){
        Bundle extras = getIntent().getExtras();
        String idSet = extras.getString("idSet");
        JSONArray testAnswers = new JSONArray();
        JSONArray testQuestions = new JSONArray();
        JSONObject jsonObject;
        int i = 0;

        for(Question q : questionsList){
            testQuestions.put(q.getIdQuestion());
            switch (q.getType()){
                case "MC":
                    boolean flag = false;
                    for(Answer a : q.getAnswersList()){
                        if(a.isChecked()){
                            testAnswers.put(""+a.getIdAnswer());
                            flag = true;
                        }
                    }
                    if (!flag) testAnswers.put("[]");
                    break;
                case "MR":
                    jsonObject = new JSONObject();
                    int j = 0;

                    for(Answer a : q.getAnswersList()){

                        if(a.isChecked()){
                            try {
                                jsonObject.put(j+"", ""+a.getIdAnswer());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                jsonObject.put(j+"", "");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        j++;

                    }
                    testAnswers.put(jsonObject);
                    break;
                default:
                    testAnswers.put("[]");
                    break;
            }
            i++;
        }
        Log.e("questions", testQuestions.toString());
        Log.e("answers", testAnswers.toString());
        submitTest(idSet, testQuestions, testAnswers);
    }

    private void logoutUser() {
        session.setLogin(false);

        Intent intent = new Intent(ExamActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}

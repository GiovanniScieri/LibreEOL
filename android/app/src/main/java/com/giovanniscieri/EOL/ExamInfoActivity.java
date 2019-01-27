package com.giovanniscieri.EOL;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ExamInfoActivity extends AppCompatActivity {
    private static final String TAG = "ExamInfoActivity";
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_info);

        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        Bundle extras = getIntent().getExtras();
        final String idExam = extras.getString("idExam");

        requestExamInfo(idExam);

        Button btn_registration = (Button) findViewById(R.id.button_register);
        btn_registration.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                makeQuestionsSet(idExam, session.getId());
            }
        });

    }


    private void requestExamInfo(final String idExam) {
        String tag_string_req = "req_exam_info";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_EXAMS_INFO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Request_exam_info Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        JSONObject exam = jObj.getJSONObject("exam");
                        TextView view_nomeesame = (TextView) findViewById(R.id.textView_nome_esame);
                        TextView view_descrizione = (TextView) findViewById(R.id.textView_descrizione_esame);
                        TextView view_dataeora = (TextView) findViewById(R.id.textView_dataeora);
                        TextView view_scadenza = (TextView) findViewById(R.id.textView_scadenza);
                        view_nomeesame.setText("Nome: "+exam.getString("name"));
                        view_dataeora.setText("Data e ora: "+exam.getString("datetime"));
                        view_scadenza.setText("Scadenza: "+exam.getString("regEnd"));

                        if(!(exam.getString("description").equals(""))){
                            view_descrizione.setText("Descrizione: "+exam.getString("description"));
                        }

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
                Log.e(TAG, "Request exam info Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idExam", idExam);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void makeQuestionsSet(final String idExam, final String idUser) {
        String tag_string_req = "create_test";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_NEW_TEST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Create test Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        Toast.makeText(getApplicationContext(), "Iscrizione effettuata con successo", Toast.LENGTH_SHORT).show();
                        String idTest = jObj.getString("idTest");
                        Intent intent = new Intent(ExamInfoActivity.this, ExamPasswordActivity.class);
                        intent.putExtra("idExam", idExam);
                        intent.putExtra("idTest", idTest);
                        ExamInfoActivity.this.finish();
                        startActivity(intent);

                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        if(errorMsg.equals("Sei già iscritto all'esame. Accedi")){
                            String idTest = jObj.getString("idTest");
                            Intent intent = new Intent(ExamInfoActivity.this, ExamPasswordActivity.class);
                            intent.putExtra("idExam", idExam);
                            intent.putExtra("idTest", idTest);
                            ExamInfoActivity.this.finish();
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Create test Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idExam", idExam);
                params.put("idUser", idUser);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void logoutUser() {
        session.setLogin(false);

        Intent intent = new Intent(ExamInfoActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

package com.giovanniscieri.EOL;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ExamPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ExamPasswordActivity";
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_password);

        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        Bundle extras = getIntent().getExtras();
        final String idExam = extras.getString("idExam");

        final EditText editText = (EditText) findViewById(R.id.editText_password_exam);

        Button btn_exam_register = (Button) findViewById(R.id.btn_exam_register);

        btn_exam_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = editText.getText().toString().trim();
                examRegister(password, idExam);
            }
        });
    }

    private void examRegister(final String password, final String idExam) {
        String tag_string_req = "exam_registration";


        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_EXAM_REGISTRATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Exam registration Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Bundle extras = getIntent().getExtras();
                        final String idTest = extras.getString("idTest");
                        String info = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder adb = new AlertDialog.Builder(ExamPasswordActivity.this);
                        adb.setTitle("Iniziare il test?");
                        adb.setIcon(android.R.drawable.ic_dialog_alert);
                        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startTest(idTest);
                            } });
                        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            } });
                        adb.show();


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
                Log.e(TAG, "Exam registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("password", password);
                params.put("idExam", idExam);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void startTest(final String idTest) {
        String tag_string_req = "start_test";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_START_TEST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Start test Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        String info = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                        String idSet = jObj.getString("idSet");
                        Intent i = new Intent(ExamPasswordActivity.this, ExamActivity.class);
                        if(info.equals("Test gia avviato")){
                            String remaining = jObj.getString("remaining");
                            i.putExtra("timer", remaining);
                        } else {
                            String duration = jObj.getString("duration");
                            i.putExtra("timer", duration);
                        }
                        i.putExtra("idSet", idSet);
                        ExamPasswordActivity.this.finish();
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
                Log.e(TAG, "Start exam Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idTest", idTest);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void logoutUser() {
        session.setLogin(false);

        Intent intent = new Intent(ExamPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

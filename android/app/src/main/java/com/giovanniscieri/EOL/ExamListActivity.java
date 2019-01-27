package com.giovanniscieri.EOL;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.List;
import java.util.Map;

public class ExamListActivity extends AppCompatActivity{
    private static final String TAG = "ExamListActivity";
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);

        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        Bundle extras = getIntent().getExtras();
        String idSubject = extras.getString("idSubject");
        String idUser = session.getId();

        requestExams(idSubject, idUser);

        final ListView mylist = (ListView) findViewById(R.id.examListview);
        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id){

                Data item = (Data) mylist.getItemAtPosition(pos);
                Intent intent = new Intent(ExamListActivity.this, ExamInfoActivity.class);
                intent.putExtra("idExam", item.id);
                ExamListActivity.this.finish();
                startActivity(intent);
            }
        });
    }

    private void requestExams(final String idSubject, final String idUser) {
        String tag_string_req = "req_exams";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_EXAMS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Request_exams Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONArray jsonArray  = jObj.getJSONArray("exam");
                        List<Data> output = new ArrayList<>();
                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Data d = new Data();
                            d.name = jsonObject.getString("name");
                            d.id = jsonObject.getString("idExam");
                            output.add(d);
                        }
                        ArrayAdapter<Data> arrayAdapter = new ArrayAdapter<>(ExamListActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, output);
                        ListView mylist = (ListView) findViewById(R.id.examListview);
                        mylist.setAdapter(arrayAdapter);

                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        TextView view = (TextView) findViewById(R.id.esami);
                        view.setText(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Request exams Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idSubject", idSubject);
                params.put("idUser", idUser);


                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void logoutUser() {
        session.setLogin(false);

        Intent intent = new Intent(ExamListActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}

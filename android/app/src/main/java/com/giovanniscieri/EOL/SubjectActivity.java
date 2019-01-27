package com.giovanniscieri.EOL;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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


public class SubjectActivity extends AppCompatActivity {
    private static final String TAG = "SubjectActivity";
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);


        TextView txtWelcome = (TextView) findViewById(R.id.welcome);

        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        String name = session.getName();
        String surname = session.getSurame();

        txtWelcome.setText("Benvenuto "+name+" "+surname+"!");

        requestSubject(session.getId());

        final ListView mylist = (ListView) findViewById(R.id.subjectListView);
        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id){
                // recupero il titolo memorizzato nella riga tramite l'ArrayAdapter
                Data item = (Data) mylist.getItemAtPosition(pos);
                Intent intent = new Intent(SubjectActivity.this, ExamListActivity.class);
                intent.putExtra("idSubject", item.id);
                startActivity(intent);
            }
        });
    }

    public void requestSubject(final String idUser) {
        String tag_string_req = "req_subjects";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SUBJECT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Subject Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        JSONArray jsonArray  = jObj.getJSONArray("subject");
                        List<Data> output = new ArrayList<>();
                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Data d = new Data();
                            d.name = jsonObject.getString("name");
                            d.id = jsonObject.getString("idSubject");
                            output.add(d);
                        }
                        ArrayAdapter<Data> arrayAdapter = new ArrayAdapter<>(SubjectActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, output);
                        ListView mylist = (ListView) findViewById(R.id.subjectListView);
                        mylist.setAdapter(arrayAdapter);
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Subject Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idUser", idUser);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared preferences
     * */
    private void logoutUser() {
        session.setLogin(false);

        Intent intent = new Intent(SubjectActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
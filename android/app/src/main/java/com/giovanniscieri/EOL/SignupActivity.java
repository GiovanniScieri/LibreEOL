package com.giovanniscieri.EOL;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import butterknife.ButterKnife;
import butterknife.Bind;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private ProgressDialog pDialog;


    @Bind(R.id.input_nome) EditText _nomeText;
    @Bind(R.id.input_cognome) EditText _cognomeText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_email2) EditText _email2Text;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_password2) EditText _password2Text;
    @Bind(com.giovanniscieri.EOL.R.id.btn_signup) Button _signupButton;
    @Bind(com.giovanniscieri.EOL.R.id.link_login) TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.giovanniscieri.EOL.R.layout.activity_signup);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final Spinner spinner = (Spinner) findViewById(R.id.group_spinner);
        populateSpinner();

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = _nomeText.getText().toString().trim();
                String surname = _cognomeText.getText().toString().trim();
                String email = _emailText.getText().toString().trim();
                String password = _passwordText.getText().toString().trim();
                String subgroup = spinner.getSelectedItem().toString();

                registerUser(name, surname, email, password, subgroup);
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(com.giovanniscieri.EOL.R.anim.push_left_in, com.giovanniscieri.EOL.R.anim.push_left_out);
            }
        });
    }



    private void registerUser(final String name, final String surname, final String email,
                              final String password, final String subgroup) {
        String tag_string_req = "req_register";

        pDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        pDialog.setIndeterminate(true);
        pDialog.setMessage("Registrazione...");
        showDialog();

        if (!validate()) {
            onSignupFailed();
            hideDialog();
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        Toast.makeText(getApplicationContext(), "Utente registrato con successo. Accedi!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        SignupActivity.this.finish();
                        startActivity(intent);
                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Log.e("test", errorMsg);
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("surname", surname);
                params.put("email", email);
                params.put("password", password);
                params.put("subgroup", subgroup);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void populateSpinner() {
        String tag_string_req = "req_spinner";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SUBGROUP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Spinner Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONArray jsonArray  = jObj.getJSONArray("subGroup");
                        List<String> output = new ArrayList<>();
                        for(int i=0; i<jsonArray.length(); i++){
                            output.add(jsonArray.getString(i));
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SignupActivity.this, android.R.layout.simple_spinner_dropdown_item, output);
                        Spinner spinner = (Spinner) findViewById(R.id.group_spinner);
                        spinner.setAdapter(arrayAdapter);
                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Log.e("test", errorMsg);
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Spinner Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Registrazione fallita", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String nome = _nomeText.getText().toString();
        String cognome = _cognomeText.getText().toString();
        String email = _emailText.getText().toString();
        String email2 = _email2Text.getText().toString();
        String password = _passwordText.getText().toString();
        String password2 = _password2Text.getText().toString();

        if (nome.isEmpty()) {
            _nomeText.setError("Inserisci nome");
            valid = false;
        } else {
            _nomeText.setError(null);
        }

        if (cognome.isEmpty()) {
            _cognomeText.setError("Inserisci cognome");
            valid = false;
        } else {
            _cognomeText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Inserisci un email valida");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (email2.isEmpty() || !(email2.equals(email))) {
            _email2Text.setError("Email non coincidono");
            valid = false;
        } else {
            _email2Text.setError(null);
        }

        if (password.isEmpty() || password.length() < 8) {
            _passwordText.setError("Minimo 8 caratteri");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (password2.isEmpty() || !(password2.equals(password))) {
            _password2Text.setError("Password non coincidono");
            valid = false;
        } else {
            _password2Text.setError(null);
        }

        return valid;
    }
}
package com.giovanniscieri.EOL;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import butterknife.ButterKnife;
import butterknife.Bind;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private SessionManager session;
    private ProgressDialog pDialog;

    @Bind(com.giovanniscieri.EOL.R.id.input_email) EditText _emailText;
    @Bind(com.giovanniscieri.EOL.R.id.input_password) EditText _passwordText;
    @Bind(com.giovanniscieri.EOL.R.id.btn_login) Button _loginButton;
    @Bind(com.giovanniscieri.EOL.R.id.link_signup) TextView _signupLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.giovanniscieri.EOL.R.layout.activity_login);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Progress dialog
        //pDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        //pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Controllo se l'utente è già loggato
        if (session.isLoggedIn()) {
            // Già loggato, parte Subject Activity
            Intent intent = new Intent(LoginActivity.this, SubjectActivity.class);
            startActivity(intent);
            finish();
        }

        //controllo il click del bottone "Login"
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = _emailText.getText().toString().trim();
                String password = _passwordText.getText().toString().trim();
                    //effettuo login
                    login(email, password);

            }
        });

        //controllo il click sul link "registrati"
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(com.giovanniscieri.EOL.R.anim.push_left_in, com.giovanniscieri.EOL.R.anim.push_left_out);
            }
        });
    }


    //funzione login
    public void login(final String email, final String password) {
        String tag_string_req = "req_login";

        //mostro il progress dialog per l'autenticazione
        pDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        pDialog.setIndeterminate(true);
        pDialog.setMessage("Autenticazione...");
        showDialog();

        //controllo se i parametri sono corretti, altrimenti concludo
        if (!validate()) {
            onLoginFailed();
            hideDialog();
            return;
        }

        //effettuo String Request con metodo POST sull'indirizzo URL_LOGIN
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {

            //se ottengo risposta
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // controllo il nodo error del json
                    if (!error) {
                        // utente loggato con successo
                        // creo sessione login
                        session.setLogin(true);

                        String id = jObj.getString("id");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String surname = user.getString("surname");
                        String email = user.getString("email");
                        String role = user.getString("role");

                        //salvo i dati dell'utente loggato
                        session.setUserData(id, name, surname, email, role);

                        // lancio Subject Activity
                        Intent intent = new Intent(LoginActivity.this, SubjectActivity.class);
                        LoginActivity.this.finish();
                        startActivity(intent);

                    } else {
                        // errore durante il login, mostro errore su toast
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

            //se non ricevo risposta
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            //setto i parametri da mandare
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
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



    @Override
    public void onBackPressed() {
        // Disable going back to the SubjectActivity
        moveTaskToBack(true);
    }


    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login fallito", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Inserisci un email valida");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 8) {
            _passwordText.setError("Minimo 8 caratteri");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}

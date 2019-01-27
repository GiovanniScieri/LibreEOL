package com.giovanniscieri.EOL;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "EOL-Login";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setUserData(String id, String name, String surname, String email, String role){
        editor.putString("id", id);
        editor.putString("name", name);
        editor.putString("surname", surname);
        editor.putString("email", email);
        editor.putString("role", role);

        editor.commit();

    }

    public String getId(){
        return pref.getString("id", "errore");
    }

    public String getName(){
        return pref.getString("name", "errore");
    }

    public String getSurame(){
        return pref.getString("surname", "errore");
    }

    public String getEmail(){
        return pref.getString("email", "errore");
    }

    public String getRole(){
        return pref.getString("role", "errore");
    }


}

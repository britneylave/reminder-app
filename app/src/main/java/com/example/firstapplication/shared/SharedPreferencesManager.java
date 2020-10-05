package com.example.firstapplication.shared;

import android.content.Context;
import android.content.SharedPreferences;

import static com.example.firstapplication.shared.Constant.SP_NAME;

public class SharedPreferencesManager {

    private static SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SharedPreferencesManager instance;

    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPreferencesManager getInstance(Context context) {
        if (instance == null)
            return instance = new SharedPreferencesManager(context);
        return instance;
    }

    public void saveString(String keySP, String value) {
        editor.putString(keySP, value);
        editor.apply();
    }

    public void saveLong(String keySP, long value) {
        editor.putLong(keySP, value);
        editor.commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0);
    }

    public void deleteString(String keySP){
        editor.remove(keySP);
        editor.commit();
    }

    public void deleteLog(String keySP) {
        editor.remove(keySP);
        editor.commit();
    }

    public void deleteAll() {
        editor.clear();
        editor.commit();
    }
}

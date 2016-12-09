package com.izv.dam.newquip.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.izv.dam.newquip.vistas.main.ModeloQuip;

/**
 * Created by Pablo on 29/11/2016.
 */

public class UserPreferences {
    public static final String FILE = "quip_preferences";
    public static final String INITIALIZED = "initialized";
    public static final String DEFAULT_FILTER = "filter";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public UserPreferences(Context context){
        preferences = context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        initialize();
    }

    private void initialize(){
        if(!preferences.contains(INITIALIZED)){
            editor.putBoolean(INITIALIZED, true);
            editor.putInt(DEFAULT_FILTER, ModeloQuip.ALL);
            editor.apply();
        }
    }

    public int getInt(String key){
        return preferences.getInt(key, 0);
    }

    public long getLong(String key){
        return preferences.getLong(key, 0);
    }

    public float getFloat(String key){
        return preferences.getFloat(key, 0);
    }

    public boolean getBoolean(String key){
        return preferences.getBoolean(key, false);
    }

    public String getString(String key){
        return preferences.getString(key, "");
    }

    public void setInt(String key, int value){
        editor.putInt(key, value);
        editor.apply();
    }

    public void setLong(String key, long value){
        editor.putLong(key, value);
        editor.apply();
    }

    public void setFloat(String key, float value){
        editor.putFloat(key, value);
        editor.apply();
    }

    public void setBoolean(String key, boolean value){
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void setString(String key, String value){
        editor.putString(key, value);
        editor.apply();
    }
}

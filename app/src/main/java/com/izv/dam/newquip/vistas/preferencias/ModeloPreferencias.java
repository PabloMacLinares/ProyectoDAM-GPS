package com.izv.dam.newquip.vistas.preferencias;

import android.content.Context;

import com.izv.dam.newquip.contrato.ContratoPreferencias;
import com.izv.dam.newquip.preferences.UserPreferences;

/**
 * Created by Pablo on 29/11/2016.
 */

public class ModeloPreferencias implements ContratoPreferencias.InterfaceModelo{

    private UserPreferences preferences;

    public ModeloPreferencias(Context context){
        preferences = new UserPreferences(context);
    }


    @Override
    public int getDefaultFilter() {
        return preferences.getInt(UserPreferences.DEFAULT_FILTER);
    }

    @Override
    public void setDefaultFilter(int filter) {
        preferences.setInt(UserPreferences.DEFAULT_FILTER, filter);
    }

}

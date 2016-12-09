package com.izv.dam.newquip.vistas.preferencias;

import android.content.Context;

import com.izv.dam.newquip.contrato.ContratoPreferencias;

/**
 * Created by Pablo on 29/11/2016.
 */

public class PresentadorPreferencias implements ContratoPreferencias.InterfacePresentador{

    private ContratoPreferencias.InterfaceVista vista;
    private ContratoPreferencias.InterfaceModelo modelo;

    public PresentadorPreferencias(ContratoPreferencias.InterfaceVista vista){
        this.vista = vista;
        this.modelo = new ModeloPreferencias((Context)vista);
    }

    @Override
    public void onResume() {
        this.vista.changeSpFilterSelection(
                this.modelo.getDefaultFilter()
        );
    }

    @Override
    public void onChangeFilter(int filter) {
        this.modelo.setDefaultFilter(filter);
    }
}

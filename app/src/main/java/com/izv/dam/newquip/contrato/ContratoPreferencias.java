package com.izv.dam.newquip.contrato;

/**
 * Created by Pablo on 29/11/2016.
 */

public interface ContratoPreferencias {
    interface InterfaceModelo{

        int getDefaultFilter();

        void setDefaultFilter(int filter);

    }

    interface InterfacePresentador{

        void onResume();

        void onChangeFilter(int filter);

    }

    interface InterfaceVista{

        void changeSpFilterSelection(int selection);

    }
}

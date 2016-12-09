package com.izv.dam.newquip.contrato;

import android.location.Location;

import com.izv.dam.newquip.pojo.Nota;

public interface ContratoNota {

    interface InterfaceModelo {

        void close();

        Nota getNota(long id);

        long saveNota(Nota n);

        void addLocation(long id, Location location);

    }

    interface InterfacePresentador {

        void onPause();

        void onResume();

        long onSaveNota(Nota n);

        void onAddLocation(long id, Location location);

    }

    interface InterfaceVista {

        void mostrarNota(Nota n);

    }

}
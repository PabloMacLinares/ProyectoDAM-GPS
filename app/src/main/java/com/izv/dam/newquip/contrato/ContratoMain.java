package com.izv.dam.newquip.contrato;

import android.database.Cursor;

import com.izv.dam.newquip.pojo.Nota;

public interface ContratoMain {

    interface InterfaceModelo {

        void updateNota(int position, boolean value);

        long deleteNota(int position);

        Nota getNota(int position);

        Cursor loadCursorNotas(int filter);

        Cursor loadCursorTareas();

        void setCursorNotas(Cursor cursorNotas);

        Cursor getCursorNotas();

        void setCursorTareas(Cursor cursorTareas);

        Cursor getCursorTareas();

        void close();

        int getFilter();
    }

    interface InterfacePresentador {

        void onResume();

        void onAddNota(int tipo);

        void onEditNota(int position);

        void onDeleteNota(int position);

        void onTryDeleteNota(int position);

        void onUpdateNota(int position, boolean value);

        void onLoadCursorNotas(int filter);

    }

    interface InterfaceVista {

        void showAddNota(int tipo);

        void showEditNota(Nota nota);

        void showConfirmDeleteNota(Nota n, int position);

        void showNotas(Cursor cursorNotas, Cursor cursoTareas);

        void showProgressBar(boolean show);

        void showSelectedFilter(int filter);

    }

}
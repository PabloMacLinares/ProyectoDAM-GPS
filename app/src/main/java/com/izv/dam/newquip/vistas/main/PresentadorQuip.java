package com.izv.dam.newquip.vistas.main;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.izv.dam.newquip.contrato.ContratoMain;

public class PresentadorQuip implements ContratoMain.InterfacePresentador{

    private ContratoMain.InterfaceModelo modelo;
    private ContratoMain.InterfaceVista vista;

    public PresentadorQuip(ContratoMain.InterfaceVista vista) {
        this.vista = vista;
        this.modelo = new ModeloQuip((Context)vista);
    }

    @Override
    public void onResume() {
        new AsyncTask<Void, Integer, Cursor[]>() {
            @Override
            protected void onPreExecute() {
                PresentadorQuip.this.vista.showProgressBar(true);
            }

            @Override
            protected Cursor[] doInBackground(Void... voids) {
                Cursor[] cs = new Cursor[2];
                cs[0] = PresentadorQuip.this.modelo.getCursorNotas();
                cs[1] = PresentadorQuip.this.modelo.getCursorTareas();
                return cs;
            }

            @Override
            protected void onPostExecute(Cursor[] cs) {
                PresentadorQuip.this.vista.showProgressBar(false);
                PresentadorQuip.this.vista.showNotas(cs[0], cs[1]);
                PresentadorQuip.this.vista.showSelectedFilter(PresentadorQuip.this.modelo.getFilter());
            }
        }.execute();
    }

    @Override
    public void onAddNota(int tipo) {
        this.vista.showAddNota(tipo);
    }

    @Override
    public void onEditNota(int position) {
        this.vista.showEditNota(this.modelo.getNota(position));
    }

    @Override
    public void onDeleteNota(int position) {
        this.modelo.deleteNota(position);
        this.vista.showNotas(this.modelo.getCursorNotas(), this.modelo.getCursorTareas());
    }

    @Override
    public void onTryDeleteNota(int position) {
        this.vista.showConfirmDeleteNota(this.modelo.getNota(position), position);
    }

    @Override
    public void onUpdateNota(int position, boolean value) {
        this.modelo.updateNota(position, value);
        this.vista.showNotas(this.modelo.getCursorNotas(), this.modelo.getCursorTareas());
    }

    @Override
    public void onLoadCursorNotas(int filter) {
        this.vista.showNotas(this.modelo.loadCursorNotas(filter), this.modelo.getCursorTareas());
        this.vista.showSelectedFilter(this.modelo.getFilter());
    }
}
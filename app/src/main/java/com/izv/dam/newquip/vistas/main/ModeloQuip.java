package com.izv.dam.newquip.vistas.main;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.izv.dam.newquip.basedatos.AyudanteORM;
import com.izv.dam.newquip.contrato.ContratoBaseDatos;
import com.izv.dam.newquip.contrato.ContratoContentProvider;
import com.izv.dam.newquip.contrato.ContratoMain;
import com.izv.dam.newquip.pojo.Localizacion;
import com.izv.dam.newquip.pojo.Nota;
import com.izv.dam.newquip.preferences.UserPreferences;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;

public class ModeloQuip implements ContratoMain.InterfaceModelo {

    public static final int ALL = 0;
    public static final int ONLY_NOTES = 1;
    public static final int ONLY_LISTS = 2;
    public static final int WITH_REMINDER = 3;
    public static final int COMPLETED = 4;
    public static final int NOT_COMPLETED = 5;

    private ContentResolver cr;
    private AyudanteORM ayudante;
    private Cursor cursorNotas;
    private Cursor cursorTareas;
    private int filter;
    private UserPreferences preferences;

    public ModeloQuip(Context c) {
        cr = c.getContentResolver();
        ayudante = new AyudanteORM(c);
        preferences = new UserPreferences(c);
    }

    @Override
    public void close() {
        cursorNotas.close();
        cursorTareas.close();
    }

    @Override
    public void updateNota(int position, boolean value) {
        cursorNotas.moveToPosition(position);
        Nota n = Nota.getNota(cursorNotas);
        n.setRealizado(value);
        updateNota(n);
        this.loadCursorNotas(filter);
    }

    private void updateNota(Nota n){
        Uri uri = ContentUris.withAppendedId(ContratoContentProvider.CONTENT_URI_NOTA, n.getId());
        cr.update(
                uri,
                n.getContentValues(true),
                "",
                new String[]{}
        );
    }

    @Override
    public long deleteNota(int position) {
        cursorNotas.moveToPosition(position);
        Nota n = Nota.getNota(cursorNotas);
        RuntimeExceptionDao<Localizacion, Integer> simpleDao = ayudante.getDataDao();
        DeleteBuilder<Localizacion, Integer> db = simpleDao.deleteBuilder();
        try {
            db.where().eq("idNota", n.getId());
            System.out.println("Deleted " + db.delete() + " locations");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        long id = this.deleteNota(n);
        this.loadCursorNotas(filter);
        this.loadCursorTareas();
        return id;
    }

    private long deleteNota(Nota n) {
        Uri uri = ContentUris.withAppendedId(ContratoContentProvider.CONTENT_URI_NOTA,n.getId());
        if (n.getTipo() == Nota.NOTA_LISTA) {
            String where = ContratoBaseDatos.TablaTareas.ID_NOTA + " =? ";
            String[] argumentos = new String[]{String.valueOf(n.getId())};
            cr.delete(ContratoContentProvider.CONTENT_URI_TAREA, where, argumentos);
        }
        return cr.delete(uri,"",null);
    }

    @Override
    public Nota getNota(int position) {
        cursorNotas.moveToPosition(position);
        Nota n = Nota.getNota(cursorNotas);
        n.setId(cursorNotas.getLong(0));
        if(n.getTipo() == Nota.NOTA_LISTA){
            n.setTareas(cursorTareas);
        }
        Log.v("ModeloQuip", "getNota(" + position + ") : " + n);
        return n;
    }

    @Override
    public Cursor loadCursorNotas(int filter) {
        if(cursorNotas != null && !cursorNotas.isClosed()){
            cursorNotas.close();
        }
        this.filter = filter;
        if(filter == ALL){//Todas las notas
            cursorNotas = cr.query(
                    ContratoContentProvider.CONTENT_URI_NOTA,
                    null,
                    null,
                    null ,
                    ContratoBaseDatos.TablaNota.SORT_ORDER_DEFAULT
            );
        }else if(filter == ONLY_NOTES){//Notas
            cursorNotas = cr.query(
                    ContratoContentProvider.CONTENT_URI_NOTA,
                    null,
                    ContratoBaseDatos.TablaNota.TIPO + " = " + Nota.NOTA_SIMPLE,
                    null ,
                    ContratoBaseDatos.TablaNota.SORT_ORDER_DEFAULT
            );
        }else if(filter == ONLY_LISTS){//Listas
            cursorNotas = cr.query(
                    ContratoContentProvider.CONTENT_URI_NOTA,
                    null,
                    ContratoBaseDatos.TablaNota.TIPO + " = " + Nota.NOTA_LISTA,
                    null ,
                    ContratoBaseDatos.TablaNota.SORT_ORDER_DEFAULT
            );
        }else if(filter == WITH_REMINDER){//Recordatorios
            cursorNotas = cr.query(
                    ContratoContentProvider.CONTENT_URI_NOTA,
                    null,
                    ContratoBaseDatos.TablaNota.RECORDATORIO + " is not null and " + ContratoBaseDatos.TablaNota.RECORDATORIO + " <> '' ",
                    null ,
                    ContratoBaseDatos.TablaNota.SORT_ORDER_DEFAULT
            );
        }else if(filter == COMPLETED){//Completadas
            cursorNotas = cr.query(
                    ContratoContentProvider.CONTENT_URI_NOTA,
                    null,
                    ContratoBaseDatos.TablaNota.REALIZADO + " = " + 1,
                    null ,
                    ContratoBaseDatos.TablaNota.SORT_ORDER_DEFAULT
            );
        }else if(filter == NOT_COMPLETED){//No completadas
            cursorNotas = cr.query(
                    ContratoContentProvider.CONTENT_URI_NOTA,
                    null,
                    ContratoBaseDatos.TablaNota.REALIZADO + " = " + 0,
                    null ,
                    ContratoBaseDatos.TablaNota.SORT_ORDER_DEFAULT
            );
        }
        return cursorNotas;
    }

    @Override
    public Cursor loadCursorTareas() {
        if(cursorTareas != null && !cursorTareas.isClosed()){
            cursorTareas.close();
        }
        cursorTareas = cr.query(
                ContratoContentProvider.CONTENT_URI_TAREA,
                null,
                null,
                null ,
                ContratoBaseDatos.TablaTareas.SORT_ORDER_DEFAULT
        );
        return cursorTareas;
    }

    @Override
    public void setCursorNotas(Cursor cursorNotas) {
        this.cursorNotas = cursorNotas;
    }

    @Override
    public Cursor getCursorNotas() {
        if(cursorNotas == null){
            return loadCursorNotas(preferences.getInt(UserPreferences.DEFAULT_FILTER));
        }
        return cursorNotas;
    }

    @Override
    public void setCursorTareas(Cursor cursorTareas) {
        this.cursorTareas = cursorTareas;
    }

    @Override
    public Cursor getCursorTareas() {
        if(cursorTareas == null){
            return loadCursorTareas();
        }
        return cursorTareas;
    }

    @Override
    public int getFilter() {
        return filter;
    }
}
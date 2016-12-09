package com.izv.dam.newquip.basedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.izv.dam.newquip.contrato.ContratoBaseDatos;
import com.izv.dam.newquip.contrato.ContratoContentProvider;

public class Ayudante extends SQLiteOpenHelper {
    //sqlite
    //tipos de datos https://www.sqlite.org/datatype3.html
    //fechas https://www.sqlite.org/lang_datefunc.html
    //trigger https://www.sqlite.org/lang_createtrigger.html
    private static final int VERSION =15;
    private static StringBuilder queryNota;
    private static StringBuilder queryTareas;

    public Ayudante(Context context) {
        super(context, ContratoContentProvider.BASEDATOS, null, VERSION);
        initQuerys();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("Ayudante", "Creating DataBase ...");

        //CREAR TABLA NOTA
        Log.v("sqlTablaNota",queryNota.toString());
        db.execSQL(queryNota.toString());

        //CREAR TABLA TAREAS
        Log.v("sqlTablaTareas",queryTareas.toString());
        db.execSQL(queryTareas.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("Ayudante", "Updating DataBase ...");
        /*db.execSQL("drop table if exists " + ContratoBaseDatos.TablaNota.TABLA);
        db.execSQL("drop table if exists " + ContratoBaseDatos.TablaTareas.TABLA);
        onCreate(db);*/
        //Actualización de la tabla nota
        String query = "create table backupnota as select * from  " + ContratoBaseDatos.TablaNota.TABLA;
        db.execSQL(query);
        query = "drop table if exists " + ContratoBaseDatos.TablaNota.TABLA;
        db.execSQL(query);
        db.execSQL(queryNota.toString());
        query = "insert into " + ContratoBaseDatos.TablaNota.TABLA + " select * from backupnota";
        db.execSQL(query);
        query = "drop table if exists backupnota";
        db.execSQL(query);

        //Actualización de la tabla tareas
        query = "create table backuptarea as select * from " + ContratoBaseDatos.TablaTareas.TABLA;
        db.execSQL(query);
        query = "drop table if exists " + ContratoBaseDatos.TablaTareas.TABLA;
        db.execSQL(query);
        db.execSQL(queryTareas.toString());
        query = "insert into " + ContratoBaseDatos.TablaTareas.TABLA + " select * from backuptarea";
        db.execSQL(query);
        query = "drop table if exists backuptarea";
        db.execSQL(query);
    }

    private void initQuerys(){
        queryNota = new StringBuilder();
        queryNota.append("create table if not exists ").append(ContratoBaseDatos.TablaNota.TABLA).append("(");
        queryNota.append(ContratoBaseDatos.TablaNota._ID).append(" integer primary key autoincrement, ");
        queryNota.append(ContratoBaseDatos.TablaNota.TIPO).append(" integer, ");
        queryNota.append(ContratoBaseDatos.TablaNota.TITULO).append(" text, ");
        queryNota.append(ContratoBaseDatos.TablaNota.NOTA).append(" text, ");
        queryNota.append(ContratoBaseDatos.TablaNota.REALIZADO).append(" integer, ");
        queryNota.append(ContratoBaseDatos.TablaNota.IMAGEN).append(" text, ");
        queryNota.append(ContratoBaseDatos.TablaNota.FECHA).append(" text, ");
        queryNota.append(ContratoBaseDatos.TablaNota.RECORDATORIO).append(" text, ");
        queryNota.append(ContratoBaseDatos.TablaNota.AUDIO).append(" text");
        queryNota.append(")");

        queryTareas = new StringBuilder();
        queryTareas.append("create table if not exists ").append(ContratoBaseDatos.TablaTareas.TABLA).append("(");
        queryTareas.append(ContratoBaseDatos.TablaTareas._ID).append(" integer primary key autoincrement, ");
        queryTareas.append(ContratoBaseDatos.TablaTareas.ID_NOTA).append(" integer, ");
        queryTareas.append(ContratoBaseDatos.TablaTareas.REALIZADA).append(" integer, ");
        queryTareas.append(ContratoBaseDatos.TablaTareas.TAREA).append(" text");
        queryTareas.append(")");
    }
}
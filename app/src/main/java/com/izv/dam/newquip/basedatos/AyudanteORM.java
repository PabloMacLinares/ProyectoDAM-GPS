package com.izv.dam.newquip.basedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.izv.dam.newquip.pojo.Localizacion;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Pablo on 04/12/2016.
 */

public class AyudanteORM extends OrmLiteSqliteOpenHelper{

    private static int version = 1;
    private static String database = "QuipORM";
    private RuntimeExceptionDao<Localizacion, Integer> runtimeDao;

    public AyudanteORM(Context context) {
        super(context, database, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try{
            TableUtils.createTable(connectionSource, Localizacion.class);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        if(newVersion != oldVersion){
            try {
                TableUtils.dropTable(connectionSource, Localizacion.class, true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public RuntimeExceptionDao<Localizacion, Integer> getDataDao(){
        if(runtimeDao == null){
            runtimeDao = getRuntimeExceptionDao(Localizacion.class);
        }
        return runtimeDao;
    }
}

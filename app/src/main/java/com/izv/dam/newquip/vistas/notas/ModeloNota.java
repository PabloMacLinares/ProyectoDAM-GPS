package com.izv.dam.newquip.vistas.notas;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.izv.dam.newquip.basedatos.AyudanteORM;
import com.izv.dam.newquip.contrato.ContratoBaseDatos;
import com.izv.dam.newquip.contrato.ContratoContentProvider;
import com.izv.dam.newquip.contrato.ContratoNota;
import com.izv.dam.newquip.pojo.Localizacion;
import com.izv.dam.newquip.pojo.Nota;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.Date;

public class ModeloNota implements ContratoNota.InterfaceModelo {

    private ContentResolver cr;
    private AyudanteORM ayudante;

    public ModeloNota(Context c) {
        cr = c.getContentResolver();
        ayudante = new AyudanteORM(c);
    }

    @Override
    public void close() {
    }

    @Override
    public Nota getNota(long id) {
        Log.v("ModeloNota", "getNota(), id: " + id);
        Uri uri = ContentUris.withAppendedId(ContratoContentProvider.CONTENT_URI_NOTA, id);
        Cursor c = cr.query(
                uri,
                ContratoBaseDatos.TablaNota.PROJECTION_ALL,
                "",
                new String[]{},
                ContratoBaseDatos.TablaNota.SORT_ORDER_DEFAULT
        );
        return Nota.getNota(c);
    }

    @Override
    public long saveNota(Nota n) {
        Log.v("ModeloNota", "saveNota(), id: " + n.getId() + ", Titulo: " + n.getTitulo() + ", nota: " + n.getNota());
        long r = 0;
        if(n.getId()==0) {
             r = this.insertNota(n);
        } else {
            this.updateNota(n);
        }
        return r;
    }

    @Override
    public void addLocation(long id, Location location) {
        if(id != 0){
            Log.v("ModeloNota", "addLocation(), id: " + id + ", location: " + location);
            RuntimeExceptionDao<Localizacion, Integer> simpleDao = ayudante.getDataDao();
            simpleDao.create(new Localizacion(id, new Date(), location.getLongitude(), location.getLatitude()));
        }
    }

    private long deleteNota(Nota n) {
        Log.v("ModeloNota", "deleteNota(), id: " + n.getId() + ", Titulo: " + n.getTitulo() + ", nota: " + n.getNota());
        RuntimeExceptionDao<Localizacion, Integer> simpleDao = ayudante.getDataDao();
        DeleteBuilder<Localizacion, Integer> db = simpleDao.deleteBuilder();
        try {
            db.where().eq("idNota", n.getId());
            System.out.println("Deleted " + db.delete() + " locations");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Uri uri = ContentUris.withAppendedId(ContratoContentProvider.CONTENT_URI_NOTA, n.getId());
        return cr.delete(
                uri,
                "",
                new String[]{}
        );
    }

    private long insertNota(Nota n) {
        Log.v("ModeloNota", "insertNota(), id: " + n.getId() + ", Titulo: " + n.getTitulo() + ", nota: " + n.getNota());
        if((n.getNota() == null || n.getNota().trim().compareTo("")==0) && (n.getTitulo() == null || n.getTitulo().trim().compareTo("")==0)) {
            this.deleteNota(n);
            return 0;
        }
        return ContentUris.parseId(cr.insert(
                ContratoContentProvider.CONTENT_URI_NOTA,
                n.getContentValues(false)
        ));
    }

    private int updateNota(Nota n) {
        Log.v("ModeloNota", "updateNota(), id: " + n.getId() + ", Titulo: " + n.getTitulo() + ", nota: " + n.getNota());
        Uri uri = ContentUris.withAppendedId(ContratoContentProvider.CONTENT_URI_NOTA, n.getId());
        if((n.getNota() == null || n.getNota().trim().compareTo("")==0) && (n.getTitulo() == null || n.getTitulo().trim().compareTo("")==0)) {
            this.deleteNota(n);
            return 0;
        }
        return cr.update(
                uri,
                n.getContentValues(true),
                "",
                new String[]{}
        );
    }
}
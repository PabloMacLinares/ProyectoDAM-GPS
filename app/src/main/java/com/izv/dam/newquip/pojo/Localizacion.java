package com.izv.dam.newquip.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Pablo on 04/12/2016.
 */
@DatabaseTable(tableName = "locations")
public class Localizacion {
    @DatabaseField
    private long idNota;

    @DatabaseField
    private Date fecha;
    @DatabaseField
    private double longitud;
    @DatabaseField
    private double latitud;

    public Localizacion(long idNota, Date fecha, double longitud, double latitud) {
        this.idNota = idNota;
        this.fecha = fecha;
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public Localizacion(){
        this(-1, null, -1, -1);
    }

    public long getIdNota() {
        return idNota;
    }

    public void setIdNota(long idNota) {
        this.idNota = idNota;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Localizacion{" +
                "idNota=" + idNota +
                ", fecha=" + fecha +
                ", longitud=" + longitud +
                ", latitud=" + latitud +
                '}';
    }
}

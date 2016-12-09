package com.izv.dam.newquip.contrato;

import android.net.Uri;

/**
 * Created by JoseAntonio on 29/10/2016.
 */

public class ContratoContentProvider {
    public final static String BASEDATOS = "quiip.sqlite";
    public static final String AUTHORITY ="com.izv.dam.newquip.notas";

    public static final String URI_NOTA ="content://"+AUTHORITY+"/"+ContratoBaseDatos.TablaNota.TABLA;
    public static final Uri CONTENT_URI_NOTA = Uri.parse(URI_NOTA);

    public static final String URI_TAREA ="content://"+AUTHORITY+"/"+ContratoBaseDatos.TablaTareas.TABLA;
    public static final Uri CONTENT_URI_TAREA = Uri.parse(URI_TAREA);

}

package com.izv.dam.newquip.vistas.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.izv.dam.newquip.R;
import com.izv.dam.newquip.vistas.notas.VistaNota;
import com.izv.dam.newquip.vistas.notas.VistaNotaLista;


/**
 * Implementation of App Widget functionality.
 */
public class WidgetNoteLauncher extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_note_launcher);

        Intent intentNota = new Intent(context, VistaNota.class);
        PendingIntent pendingIntentNota = PendingIntent.getActivity(context, 0, intentNota, 0);
        views.setOnClickPendingIntent(R.id.ibCreateNote, pendingIntentNota);

        Intent intentLista = new Intent(context, VistaNotaLista.class);
        PendingIntent pendingIntentLista = PendingIntent.getActivity(context, 0, intentLista, 0);
        views.setOnClickPendingIntent(R.id.ibCreateList, pendingIntentLista);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


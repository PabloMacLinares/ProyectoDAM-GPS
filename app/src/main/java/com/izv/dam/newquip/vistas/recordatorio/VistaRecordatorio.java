package com.izv.dam.newquip.vistas.recordatorio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import com.izv.dam.newquip.R;
import com.izv.dam.newquip.pojo.Nota;
import com.izv.dam.newquip.util.UtilFecha;
import com.izv.dam.newquip.vistas.notas.VistaNota;

import java.util.Date;

/**
 * Created by Sergio on 18/11/2016.
 */

public class VistaRecordatorio extends AppCompatActivity{
    private CalendarView calendar;
    private TextView tv1;
    private String fecha;
    private ImageView guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarRecordatorio);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        calendar = (CalendarView)findViewById(R.id.calendar);
        tv1 = (TextView)findViewById(R.id.tv1);
        guardar = (ImageView)findViewById(R.id.guardar);
        tv1.setText("Recordatorio: ");

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2){
                i1++;
                tv1.setText("Recordatorio: "+ i2 +" / "+ i1 +" / "+ i );
                fecha = i +"-"+ i1 +"-"+ i2;
            }

        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cierraYguarda();
            }
        });
    }
    private void cierraYguarda(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("fechaModificada", fecha);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}

package com.izv.dam.newquip.vistas.drawer;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.izv.dam.newquip.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by dam on 11/11/2016.
 */

public class VistaDrawer extends AppCompatActivity implements View.OnClickListener{
    ImageButton negro;
    ImageButton verde;
    ImageButton azul;
    ImageButton rojo;
    ImageButton amarillo;
    private Lienzo lienzo;
    float ppequenyo;
    float pmediano;
    float pgrande;
    float pdefecto;
    int GUARDAR_DIBUJO = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDrawer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        negro = (ImageButton) findViewById(R.id.colornegro);
        verde = (ImageButton) findViewById(R.id.colorverde);
        azul = (ImageButton) findViewById(R.id.colorazul);
        rojo = (ImageButton) findViewById(R.id.colorrojo);
        amarillo = (ImageButton) findViewById(R.id.coloramarillo);

        negro.setOnClickListener(this);
        verde.setOnClickListener(this);
        azul.setOnClickListener(this);
        rojo.setOnClickListener(this);
        amarillo.setOnClickListener(this);

        lienzo = (Lienzo) findViewById(R.id.lienzo);


        ppequenyo = 10;
        pmediano = 20;
        pgrande = 30;

        pdefecto = pmediano;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_drawer, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.nuevo:
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle("Nuevo Dibujo");
                newDialog.setMessage("¿Comenzar nuevo dibujo(perderás el dibujo actual)?");
                newDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){

                        lienzo.NuevoDibujo();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                newDialog.show();
                return true;


            case R.id.grande:
                lienzo.setTamanyoPunto(pgrande);
                Toast.makeText(getApplicationContext(),"ha seleccionado el tamaño: grande",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.mediano:
                lienzo.setTamanyoPunto(pmediano);
                Toast.makeText(getApplicationContext(),"ha seleccionado el tamaño: mediano",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.pequeno:
                lienzo.setTamanyoPunto(ppequenyo);
                Toast.makeText(getApplicationContext(),"ha seleccionado el tamaño: pequeño",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.goma:
                lienzo.setColor("#ffffff");//BLANCO
                return true;

            case R.id.guardar:
                //PermisosDibujo();

                AlertDialog.Builder salvarDibujo = new AlertDialog.Builder(this);
                salvarDibujo.setTitle("Salvar dibujo");
                salvarDibujo.setMessage("¿Salvar Dibujo a la galeria?");
                salvarDibujo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        lienzo.setDrawingCacheEnabled(true);
                        Bitmap data1 = lienzo.getDrawingCache();
                        String path2 = Environment.getExternalStorageDirectory().toString();
                        OutputStream fOut = null;
                        File file = new File(path2, "Quit"+Math.random()+".jpg");
                        if(!file.exists()){
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            fOut = new FileOutputStream(file);
                            data1.compress(Bitmap.CompressFormat.JPEG, 0, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                            try {
                                fOut.flush(); // Not really required
                                fOut.close(); // do not forget to close the stream
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
                            Toast.makeText(getApplicationContext(),"¡Dibujo salvado en la galeria!", Toast.LENGTH_SHORT).show();
                            cierraYguarda(file.getAbsolutePath());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
                salvarDibujo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                salvarDibujo.show();


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onClick(View v) {
        String color;

        switch (v.getId()){
            case R.id.colornegro:
                color = v.getTag().toString();
                lienzo.setColor(color);
                break;
            case R.id.colorverde:
                color = v.getTag().toString();
                lienzo.setColor(color);
                break;
            case R.id.colorazul:
                color = v.getTag().toString();
                lienzo.setColor(color);
                break;
            case R.id.colorrojo:
                color = v.getTag().toString();
                lienzo.setColor(color);
                break;
            case R.id.coloramarillo:
                color = v.getTag().toString();
                lienzo.setColor(color);
                break;
            default:

                break;
        }
    }
    private void cierraYguarda(String ruta){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("rutaDibujo", ruta);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}

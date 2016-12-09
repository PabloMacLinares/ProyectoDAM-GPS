package com.izv.dam.newquip.vistas.notas;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.izv.dam.newquip.R;
import com.izv.dam.newquip.audio.Audio;
import com.izv.dam.newquip.camara.Camara;
import com.izv.dam.newquip.contrato.ContratoNota;
import com.izv.dam.newquip.databinding.ActivityNotaBinding;
import com.izv.dam.newquip.pdf.Pdf;
import com.izv.dam.newquip.pojo.Nota;
import com.izv.dam.newquip.vistas.drawer.VistaDrawer;
import com.izv.dam.newquip.vistas.mapa.VistaMapa;
import com.izv.dam.newquip.vistas.recordatorio.VistaRecordatorio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class VistaNota extends AppCompatActivity implements
        ContratoNota.InterfaceVista,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private EditText editTextTitulo, editTextNota;
    private Nota nota = new Nota();
    private PresentadorNota presentador;
    private ImageView imagen;
    private ImageButton addFoto;
    private ImageButton addGaleria;
    private ImageButton addAudio;
    private ImageButton dtAudio;
    private ImageButton addDibujo;
    private ImageButton btPDF;
    private ImageButton btRecordatorio;
    private ImageButton btMap;
    private TextView tvRecordatorio;
    private File archivoAudio;
    private Button btPlay;
    private AppCompatActivity yo = this;
    private Context context = this;
    private Camara c = new Camara(yo);
    private final int ACTIVIDAD_FOTO = 200;
    private final int ACTIVIDAD_GALERIA = 300;
    private final int ACTIVIDAD_DRAWER = 400;
    private final int ACTIVIDAD_CALENDARIO = 111;

    private GoogleApiClient mGoogleApiClient;

    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNotaBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_nota);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presentador = new PresentadorNota(this);
        editTextTitulo = (EditText) findViewById(R.id.etTitulo);
        editTextNota = (EditText) findViewById(R.id.etNota);
        imagen = (ImageView)findViewById(R.id.imagenCargada);
        btPlay = (Button) findViewById(R.id.btPlay);
        btRecordatorio = (ImageButton) findViewById(R.id.btRecordatorio);
        btMap = (ImageButton) findViewById(R.id.btMap);
        tvRecordatorio = (TextView)findViewById(R.id.recordatorio);

        if (savedInstanceState != null) {
            nota = savedInstanceState.getParcelable("nota");
        } else {
            Bundle b = getIntent().getExtras();
            if(b != null ) {
                nota = b.getParcelable("nota");
            }else {
                nota.setFecha(new Date(System.currentTimeMillis()));
            }
        }
        System.out.println("Received nota: \n\t" + nota);
        binding.setNota(nota);
        System.out.println("Nota 1" + nota.toString());
        mostrarNota(nota);

        addFoto = (ImageButton) findViewById(R.id.ivAddFoto);
        addGaleria = (ImageButton) findViewById(R.id.ivAddGaleria);
        addAudio = (ImageButton) findViewById(R.id.ivAddAudio);
        addDibujo = (ImageButton) findViewById(R.id.ivAddDibujo);
        dtAudio = (ImageButton) findViewById(R.id.ivDetenerAudio);

        addFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.openCamara(context);
            }
        });
        addGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent, "Selecciona app de imagenes"), ACTIVIDAD_GALERIA);
            }
        });
        final Audio a = new Audio (yo,context);
        addAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dtAudio.setVisibility(View.VISIBLE);
                a.grabar();
            }
        });
        dtAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.detener();
                archivoAudio = a.getArchivo();
                nota.setRutaAudio(archivoAudio.getAbsolutePath());
                btPlay.setVisibility(View.VISIBLE);
                dtAudio.setVisibility(View.GONE);
                Toast.makeText(VistaNota.this, "Audio guardado", Toast.LENGTH_SHORT).show();
            }
        });
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.reproducir(nota.getRutaAudio());
            }
        });
        //DIBUJO
        addDibujo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VistaDrawer.class);
                startActivityForResult(intent,ACTIVIDAD_DRAWER);
            }
        });
        //PDF
        final Pdf p = new Pdf(yo,context);
        btPDF = (ImageButton) findViewById(R.id.btPDF);
        btPDF.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                p.generarPDF(nota);
                Toast.makeText(VistaNota.this, "PDF guardado", Toast.LENGTH_SHORT).show();
            }
        });

        //RECORDATORIO
        btRecordatorio.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(context, VistaRecordatorio.class);
                Bundle b = new Bundle();
                b.putParcelable("nota",nota);
                intent.putExtras(b);
                startActivityForResult(intent,ACTIVIDAD_CALENDARIO);
            }
        });

        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VistaMapa.class);
                Bundle b = new Bundle();
                b.putParcelable("nota", nota);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("request code", requestCode + "");
        Log.v("result code", resultCode + "");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTIVIDAD_FOTO:
                    Bitmap data1 = (Bitmap) data.getExtras().get("data");
                    imagen.setImageBitmap(data1);

                    String path2 = Environment.getExternalStorageDirectory().toString();
                    OutputStream fOut = null;
                    File file = new File(path2, "Quit"+nota.getId()+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                    if(!file.exists()){
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        fOut = new FileOutputStream(file);
                        Bitmap b = c.redimension(data1 , 300, 169);
                        data1.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                        try {
                            fOut.flush(); // Not really required
                            fOut.close(); // do not forget to close the stream
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
                        nota.setRutaImagen(file.getAbsolutePath());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case ACTIVIDAD_GALERIA:
                    Uri path = data.getData();
                    imagen.setImageURI(path);

                    Bitmap data2= Camara.drawableToBitmap(imagen.getDrawable());
                    String path3 = Environment.getExternalStorageDirectory().toString();
                    OutputStream fOut1 = null;
                    File file1 = new File(path3, "Quit"+nota.getId()+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                    if(!file1.exists()){
                        try {
                            file1.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        fOut1 = new FileOutputStream(file1);
                        data2.compress(Bitmap.CompressFormat.JPEG, 85, fOut1); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                        try {
                            fOut1.flush(); // Not really required
                            fOut1.close(); // do not forget to close the stream
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MediaStore.Images.Media.insertImage(getContentResolver(),file1.getAbsolutePath(),file1.getName(),file1.getName());
                        nota.setRutaImagen(file1.getAbsolutePath());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case ACTIVIDAD_DRAWER:
                    String rutaDibujo = data.getExtras().getString("rutaDibujo");
                    nota.setRutaImagen(rutaDibujo);
                    imagen.setVisibility(View.VISIBLE);
                    imagen.setImageURI(Uri.fromFile(new File(nota.getRutaImagen())));
                    break;
                case ACTIVIDAD_CALENDARIO:
                    Toast.makeText(this, "Hay recordatorio" , Toast.LENGTH_SHORT).show();
                    String recordatorio = data.getExtras().getString("fechaModificada");
                    nota.setRecordatorio(recordatorio);
                    if (nota.getRecordatorio() !=null){
                        tvRecordatorio.setText("Recordatorio: " + nota.getRecordatorio().toString());
                    }
                    saveNota();
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        Log.v("VistaNota", "onPause()");
        saveNota();
        System.out.println("Nota 2 " + nota.toString());
        presentador.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.v("VistaNota", "onResume()");
        presentador.onResume();
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v("VistaNota", "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        outState.putParcelable("nota", nota);
    }

    @Override
    public void mostrarNota(Nota n) {
        //editTextTitulo.setText(nota.getTitulo());
        //editTextNota.setText(nota.getNota());
        if(nota.getRutaImagen() != null && !nota.getRutaImagen().equals("")){
            imagen.setVisibility(View.VISIBLE);
            imagen.setImageURI(Uri.fromFile(new File(nota.getRutaImagen())));
        }
        if(nota.getRutaAudio() != null && !nota.getRutaAudio().equals("")){
            btPlay.setVisibility(View.VISIBLE);
        }
    }

    private void saveNota() {
        Log.v("VistaNota", "saveNota()");
        //nota.setTitulo(editTextTitulo.getText().toString());
        //nota.setNota(editTextNota.getText().toString());
        nota.setTipo(Nota.NOTA_SIMPLE);
        nota.setFecha(new Date());
        nota.setRecordatorio(tvRecordatorio.getText().toString().replace("Recordatorio: ",""));
        if(nota.getRutaAudio().length() != 0){
            nota.setRutaAudio(nota.getRutaAudio());
        }else if(archivoAudio == null || (archivoAudio.getAbsolutePath()== null && archivoAudio.getAbsolutePath().length() == 0)) {
            nota.setRutaAudio("");
        }
        long r = presentador.onSaveNota(nota);
        if(r > 0 & nota.getId() == 0){
            nota.setId(r);
        }
        Log.v("VistaNota", "Id nota guardada: " + nota.getId());
        if(location != null){
            presentador.onAddLocation(nota.getId(), location);
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "NO FINE LOCATION", Toast.LENGTH_SHORT);
            System.out.println("no fine location");
            return;
        }

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "NO COARSED LOCATION", Toast.LENGTH_SHORT);
            System.out.println("no coarsed location");
            return;

        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        System.out.println("Location changed: " + location);
    }
}
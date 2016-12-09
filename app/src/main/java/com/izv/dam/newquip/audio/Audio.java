package com.izv.dam.newquip.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by dam on 09/11/2016.
 */

public class Audio implements MediaPlayer.OnCompletionListener {
    private MediaRecorder recorder;
    private MediaPlayer player;
    private File archivo;
    private AppCompatActivity yo;
    private Context c;

    public Audio(AppCompatActivity yo, Context c) {
        this.yo = yo;
        this.c = c;
    }

    public Audio() {}

    public void grabar() {
        Toast.makeText(c, "Grabando...", Toast.LENGTH_SHORT).show();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File path = new File(Environment.getExternalStorageDirectory()
                .getPath());
        try {
            archivo = File.createTempFile("temporal", ".3gp", path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.setOutputFile(archivo.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
    }

    //}


    public void detener() {
        recorder.stop();
        recorder.release();
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        try {
            player.setDataSource(archivo.getAbsolutePath());
        } catch (IOException e) {
        }
        try {
            player.prepare();
        } catch (IOException e) {
        }
    }

    public void reproducir(String rutaAudio) {
        player = new MediaPlayer();
        try {
            player.setDataSource(rutaAudio);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(c, "Audio Finalizado", Toast.LENGTH_SHORT).show();
    }

    public File getArchivo() {
        return archivo;
    }

}


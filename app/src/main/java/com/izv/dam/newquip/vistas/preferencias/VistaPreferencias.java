package com.izv.dam.newquip.vistas.preferencias;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.izv.dam.newquip.R;
import com.izv.dam.newquip.contrato.ContratoPreferencias;

public class VistaPreferencias extends AppCompatActivity implements ContratoPreferencias.InterfaceVista{

    private PresentadorPreferencias presentador;

    private Spinner spFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presentador = new PresentadorPreferencias(this);

        init();
    }

    @Override
    protected void onResume() {
        presentador.onResume();
        super.onResume();
    }

    @Override
    public void changeSpFilterSelection(int selection) {
        spFilter.setSelection(selection);
    }

    private void init(){
        this.setTitle("Preferencias");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spFilter = (Spinner) findViewById(R.id.spFilter);
        initSpinner();

    }

    private void initSpinner(){
        String[] spFilterItems = new String[]{
                getString(R.string.all),
                getString(R.string.notes),
                getString(R.string.lists),
                getString(R.string.with_reminder),
                getString(R.string.completed),
                getString(R.string.not_completed)
        };
        ArrayAdapter<String> adapterSpFilter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                spFilterItems
        );
        spFilter.setAdapter(adapterSpFilter);

        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                VistaPreferencias.this.presentador.onChangeFilter(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}

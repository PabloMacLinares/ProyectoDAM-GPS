package com.izv.dam.newquip.vistas.notas;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.izv.dam.newquip.R;
import com.izv.dam.newquip.adaptadores.AdaptadorTarea;
import com.izv.dam.newquip.contrato.ContratoNotaLista;
import com.izv.dam.newquip.databinding.ActivityNotaListaBinding;
import com.izv.dam.newquip.pdf.Pdf;
import com.izv.dam.newquip.pojo.Nota;
import com.izv.dam.newquip.pojo.Tarea;

import java.util.Date;
import java.util.List;

/**
 * Created by dam on 25/10/2016.
 */

public class VistaNotaLista extends AppCompatActivity implements ContratoNotaLista.InterfaceVista {

    private PresentadorNotaLista presentador;
    private EditText etTitulo;
    private RecyclerView rvTareas;
    private AdaptadorTarea adaptador;
    private Nota nota = new Nota();
    private ImageButton ivPdf;
    private ImageButton ivBorrar;
    private AppCompatActivity yo = this;
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNotaListaBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_nota_lista);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presentador = new PresentadorNotaLista(this);

        ivPdf = (ImageButton) findViewById(R.id.ivPdf);
        //PDFS
        final Pdf p = new Pdf(yo,context);
        ivPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.generarPDF(nota);
            }
        });

        ivBorrar = (ImageButton) findViewById(R.id.ivBorrar);
        //BorrarTodo
        ivBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = nota.getTareas().size()-1 ; i >= 0 ; i--) {
                    presentador.onRemoveTarea(nota.getTareas().get(i).getId());
                    nota.getTareas().remove(i);
                    adaptador.notifyItemRemoved(i);
                }

            }
        });

        etTitulo = (EditText) findViewById(R.id.etTitulo);
        etTitulo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                nota.setTitulo(editable.toString());
            }
        });

        nota.setTitulo("Nueva lista");
        nota.setTipo(Nota.NOTA_LISTA);
        if (savedInstanceState != null) {
            nota = savedInstanceState.getParcelable("nota");
        } else {
            Bundle b = getIntent().getExtras();
            if(b != null ) {
                nota = b.getParcelable("nota");
                nota.setTareas((List) b.getParcelableArrayList("tareas"));
            }else {
                nota.setFecha(new Date(System.currentTimeMillis()));
            }
        }

        Log.v("VistaNotaLista", "Received nota: \n\t" + nota);
        rvTareas = (RecyclerView) findViewById(R.id.rvTareas);
        rvTareas.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdaptadorTarea(nota.getTareas());
        rvTareas.setAdapter(adaptador);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                presentador.onRemoveTarea(nota.getTareas().get(swipedPosition).getId());
                nota.getTareas().remove(swipedPosition);
                //adaptador.notifyItemRemoved(swipedPosition);
                adaptador.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvTareas);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnFlotNotList);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tarea t = new Tarea();
                t.setId(presentador.onAddTarea(t));
                t.setIdNota(nota.getId());
                nota.getTareas().add(0, t);
                //adaptador.notifyItemInserted(0);
                adaptador.notifyDataSetChanged();
                rvTareas.scrollToPosition(0);
            }
        });
        binding.setNota(nota);
    }

    @Override
    public void showNota(Nota n) {
        //etTitulo.setText(n.getTitulo());
    }

    @Override
    protected void onPause() {
        presentador.onPause(nota);
        super.onPause();
    }

    @Override
    protected void onResume() {
        presentador.onResume(nota);
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("nota", nota);
    }

}
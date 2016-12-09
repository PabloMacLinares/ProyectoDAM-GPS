package com.izv.dam.newquip.vistas.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.izv.dam.newquip.R;
import com.izv.dam.newquip.adaptadores.AdaptadorNota;
import com.izv.dam.newquip.contrato.ContratoMain;
import com.izv.dam.newquip.dialogo.DialogoBorrar;
import com.izv.dam.newquip.dialogo.OnBorrarDialogListener;
import com.izv.dam.newquip.pojo.Nota;
import com.izv.dam.newquip.vistas.notas.VistaNota;
import com.izv.dam.newquip.vistas.notas.VistaNotaLista;
import com.izv.dam.newquip.vistas.preferencias.VistaPreferencias;

import java.util.ArrayList;


public class VistaQuip extends AppCompatActivity implements ContratoMain.InterfaceVista , OnBorrarDialogListener, NavigationView.OnNavigationItemSelectedListener {

    private static final int PORTRAIT = 0;
    private static final int LANDSCAPE = 1;
    private static final int SQUARE = 2;

    private RecyclerView recycler;
    private AdaptadorNota adaptador;
    private PresentadorQuip presentador;
    private ProgressBar pbLoading;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        presentador = new PresentadorQuip(this);

        recycler = (RecyclerView) findViewById(R.id.rvNotas);
        int orientation = getOrientation();
        if(isTablet()){
            if(orientation == PORTRAIT) {
                recycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            }else{
                recycler.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            }
        }else{
            if(orientation == PORTRAIT) {
                recycler.setLayoutManager(new LinearLayoutManager(this));
            }else{
                recycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            }
        }
        adaptador = new AdaptadorNota(null);
        recycler.setAdapter(adaptador);

        adaptador.setOnItemClickListener(new AdaptadorNota.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                presentador.onEditNota(position);
            }
        });

        adaptador.setOnCheckBoxClickListener(new AdaptadorNota.OnCheckBoxClickListener() {
            @Override
            public void onCheckBoxClick(int position, boolean value) {
                presentador.onUpdateNota(position, value);
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                presentador.onTryDeleteNota(swipedPosition);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recycler);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_nota);
        fab.setColorNormal(getResources().getColor(R.color.colorAccent));
        fab.setColorPressed(getResources().getColor(R.color.colorAccent));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentador.onAddNota(Nota.NOTA_SIMPLE);
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.add_lista);
        fab2.setColorNormal(getResources().getColor(R.color.colorAccent));
        fab2.setColorPressed(getResources().getColor(R.color.colorAccent));
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentador.onAddNota(Nota.NOTA_LISTA);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if(drawer != null) {
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_all);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);

        pedirPermisos();
    }

    private Boolean isTablet() {
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            return true;
        }
        return false;
    }

    private int getOrientation() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        if(width > height){
            return LANDSCAPE;
        }else if(width < height){
            return PORTRAIT;
        }else{
            return SQUARE;
        }
    }

    @Override
    protected void onResume() {
        presentador.onResume();
        super.onResume();
    }

    @Override
    public void onBorrarPossitiveButtonClick(int position) {
        presentador.onDeleteNota(position);
        adaptador.notifyItemRemoved(position);
    }

    @Override
    public void onBorrarNegativeButtonClick() {
        presentador.onResume();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_all) {
            presentador.onLoadCursorNotas(ModeloQuip.ALL);
        }else if (id == R.id.nav_notas) {
            presentador.onLoadCursorNotas(ModeloQuip.ONLY_NOTES);
        } else if (id == R.id.nav_listas) {
            presentador.onLoadCursorNotas(ModeloQuip.ONLY_LISTS);
        } else if (id == R.id.nav_recordatorios) {
            presentador.onLoadCursorNotas(ModeloQuip.WITH_REMINDER);
        } else if (id == R.id.nav_completadas) {
            presentador.onLoadCursorNotas(ModeloQuip.COMPLETED);
        } else if (id == R.id.nav_no_completadas) {
            presentador.onLoadCursorNotas(ModeloQuip.NOT_COMPLETED);
        } else if (id == R.id.nav_preferencias){
            startActivity(new Intent(this, VistaPreferencias.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void showSelectedFilter(int filter) {
        if (filter == ModeloQuip.ALL){
            navigationView.setCheckedItem(R.id.nav_all);
            this.setTitle("Quip - " + getString(R.string.all));
        }else if(filter == ModeloQuip.ONLY_NOTES){
            navigationView.setCheckedItem(R.id.nav_notas);
            this.setTitle("Quip - " + getString(R.string.notes));
        }else if(filter == ModeloQuip.ONLY_LISTS){
            navigationView.setCheckedItem(R.id.nav_listas);
            this.setTitle("Quip - " + getString(R.string.lists));
        }else if(filter == ModeloQuip.WITH_REMINDER){
            navigationView.setCheckedItem(R.id.nav_recordatorios);
            this.setTitle("Quip - " + getString(R.string.with_reminder));
        }else if(filter == ModeloQuip.COMPLETED){
            navigationView.setCheckedItem(R.id.nav_completadas);
            this.setTitle("Quip - " + getString(R.string.completed));
        }else if(filter == ModeloQuip.NOT_COMPLETED){
            navigationView.setCheckedItem(R.id.nav_no_completadas);
            this.setTitle("Quip - " + getString(R.string.not_completed));
        }
    }

    @Override
    public void showAddNota(int tipo) {
        if(tipo == Nota.NOTA_SIMPLE || tipo == Nota.NOTA_LISTA) {
            Nota nota = new Nota();
            nota.setTipo(tipo);
            Intent intent = null;
            if(tipo == Nota.NOTA_SIMPLE){
                intent = new Intent(this, VistaNota.class);
            }else if(tipo == Nota.NOTA_LISTA){
                intent = new Intent(this, VistaNotaLista.class);
                intent.putParcelableArrayListExtra("tareas", (ArrayList<? extends Parcelable>) nota.getTareas());
            }
            if(intent != null) {
                intent.putExtra("nota", nota);
                startActivity(intent);
            }
        }
    }

    @Override
    public void showEditNota(Nota nota) {
        Intent intent = null;
        if(nota.getTipo() == Nota.NOTA_SIMPLE){
            intent = new Intent(this, VistaNota.class);
        }else if(nota.getTipo() == Nota.NOTA_LISTA){
            intent = new Intent(this, VistaNotaLista.class);
            intent.putParcelableArrayListExtra("tareas", (ArrayList<? extends Parcelable>) nota.getTareas());
        }
        if(intent != null) {
            intent.putExtra("nota", nota);
            startActivity(intent);
        }
    }

    @Override
    public void showConfirmDeleteNota(Nota n, int position) {
        DialogoBorrar fragmentBorrar = DialogoBorrar.newInstance(n, position);
        fragmentBorrar.show(getSupportFragmentManager(), "Dialogo borrar");
    }

    @Override
    public void showNotas(Cursor cursorNotas, Cursor cursoTareas) {
        adaptador.changeCursorTareas(cursoTareas);
        adaptador.changeCursorNotas(cursorNotas);

    }

    @Override
    public void showProgressBar(boolean show) {
        if(show){
            pbLoading.setVisibility(View.VISIBLE);
        }else{
            pbLoading.setVisibility(View.GONE);
        }
    }

    private void pedirPermisos(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);

            }
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 101);
            }
            if(!ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.CAMERA)){
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},124);
            }
        }else{
            System.out.println("Permisos concedidos");
        }
    }
}
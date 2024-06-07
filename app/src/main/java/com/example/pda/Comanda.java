package com.example.pda;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pda.conexion.Connexion;

import java.util.ArrayList;

public class Comanda extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout drawerLayout;
    private ImageView menu;
    int numeroMesas;
    private ArrayList<Button> listaMesas,listaBotonesMasConsumicion,listaBotonesMenosConsumicion,listaComentarios;
    private ArrayList<Consumiciones> listadoConsumiciones,listaPedidoCliente;
    private ArrayList<TextView> listaCantidades;
    private LinearLayout layoutMesas,layoutComanda,vtnSalir,vtnPagos,vtnAnulaciones,vtnComanda;
    private Connexion acdbh;
    private Spinner spinnerFamilias;
    private ListView listViewConsumiciones;
    private Button btnMarcharComanda;
    private TextView mesaSeleccionada;
    private int idBar;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda);

        acdbh = new Connexion(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        vtnAnulaciones = findViewById(R.id.anulaciones);
        vtnComanda = findViewById(R.id.comanda);
        vtnPagos = findViewById(R.id.pagos);
        vtnSalir = findViewById(R.id.salir);

        layoutMesas = findViewById(R.id.linearMesas);
        spinnerFamilias = findViewById(R.id.spinnerFamilias);
        layoutComanda = findViewById(R.id.linearComanda);

        listaMesas = new ArrayList<>();
        listadoConsumiciones = new ArrayList<>();
        listaPedidoCliente = new ArrayList<>();
        listaCantidades = new ArrayList<>();
        listaBotonesMasConsumicion = new ArrayList<>();
        listaBotonesMenosConsumicion = new ArrayList<>();
        listaComentarios = new ArrayList<>();

        intent = getIntent();
        cargarMesas(intent);
        rellenarSpinner();

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        vtnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Comanda.this, MainActivity.class);
                acdbh.close();
            }
        });

        vtnComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        vtnPagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Comanda.this, Pagos.class);
                acdbh.close();
            }
        });

        vtnAnulaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Comanda.this, Devoluciones.class);
                acdbh.close();
            }
        });
    }

    private void agregarEventoSpinner() {
        spinnerFamilias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String familiaSeleccionada = parent.getItemAtPosition(position).toString();
                acdbh.rellenarArrayFamilia(acdbh,listadoConsumiciones,familiaSeleccionada);
                rellenarListView(familiaSeleccionada);
                hacerPedido(acdbh);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void hacerPedido(Connexion acdbh) {
        btnMarcharComanda = findViewById(R.id.pedir);
        btnMarcharComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listaPedidoCliente.size() <= 0){
                    Toast.makeText(Comanda.this,  "Comanda Vacía", Toast.LENGTH_SHORT).show();
                } else if ( mesaSeleccionada.getText() == null || mesaSeleccionada.getText().equals("")) {
                    Toast.makeText(Comanda.this,  "Debes seleccionar una mesa", Toast.LENGTH_SHORT).show();
                }else{
                    acdbh.marcharComanda(acdbh,listaPedidoCliente,mesaSeleccionada.getText().toString(),getString(R.string.Pendiente),idBar);
                    listaPedidoCliente.removeAll(listaPedidoCliente);
                    listaCantidades.removeAll(listaCantidades);
                    listaBotonesMasConsumicion.removeAll(listaBotonesMasConsumicion);
                    listaBotonesMenosConsumicion.removeAll(listaBotonesMenosConsumicion);
                    layoutComanda.removeAllViews();
                    layoutMesas.removeAllViews();
                    mesaSeleccionada.setText("");
                    listaMesas.removeAll(listaMesas);
                    cargarMesas(intent);
                    Toast.makeText(Comanda.this,  "Marchando comanda", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void rellenarListView (String familiaSeleccionada){
        listViewConsumiciones = findViewById(R.id.listViewFamilia);
        AdaptadorListViewConsumiciones adapter = new AdaptadorListViewConsumiciones(Comanda.this,listadoConsumiciones);
        listViewConsumiciones.setAdapter(adapter);
        listViewConsumiciones.setOnItemClickListener(this);

    }

    private void rellenarSpinner() {
        ArrayList<String> listaFamilias = acdbh.insertarFamiliasSpinner(acdbh);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(Comanda.this,android.R.layout.simple_spinner_dropdown_item,listaFamilias);
        adaptador.setDropDownViewResource(R.layout.spinner_dropdown_item2);
        spinnerFamilias.setAdapter(adaptador);
        agregarEventoSpinner(); 
    }

    private void cargarMesas(Intent intent) {
        mesaSeleccionada = findViewById(R.id.mesaSeleccionada);
        if (intent != null) {
            numeroMesas = intent.getIntExtra("numero_mesas",0);
            idBar = intent.getIntExtra("idBar",0);
        }

        for(int i = 1; i <= numeroMesas; i++){
            Button button = new Button(this);
            button.setText(String.valueOf(i));
            if(acdbh.mesaOcupada(acdbh,i,getString(R.string.Pendiente))){
                button.setTextColor(Color.RED);

            }else{
                button.setTextColor(Color.GREEN);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            button.setLayoutParams(params);
            layoutMesas.addView(button);
            listaMesas.add(button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = listaMesas.indexOf(v);
                    mesaSeleccionada.setText(listaMesas.get(index).getText().toString());
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        agregarAComanda(position,listadoConsumiciones.get(position));
    }

    private void agregarAComanda(int position, Consumiciones consumiciones) {
        Consumiciones consumicion = consumiciones;
        consumicion.setPosicion(position);

        if(listaPedidoCliente.contains(consumicion)){
            int index = listaPedidoCliente.indexOf(consumicion);
            if (index != -1 && index < listaPedidoCliente.size()) {
                listaPedidoCliente.get(index).setCantidadConsumicion(listaPedidoCliente.get(index).getCantidadConsumicion() + 1);
                listaCantidades.get(index).setText(String.valueOf(listaPedidoCliente.get(index).getCantidadConsumicion()));
            }
        }else{
            consumicion.setCantidadConsumicion(1);
            insertarVistaComanda(consumicion);
        }
    }

    private void insertarVistaComanda(Consumiciones consumicion) {
        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setColumnCount(6); // Número de columnas que necesitas

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.setMargins(0, 0, 0, 16);
        gridLayout.setLayoutParams(layoutParams);

        // TextView para el nombre
        TextView nombre = new TextView(this);
        GridLayout.LayoutParams nombreParams = new GridLayout.LayoutParams();
        nombreParams.width = 0;
        nombreParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        nombreParams.columnSpec = GridLayout.spec(0, 1, 3f);
        nombre.setLayoutParams(nombreParams);
        nombre.setText(consumicion.getNombreConsumicion());
        nombre.setTextColor(Color.BLACK);
        nombre.setTextSize(15);

        // Botón +
        Button btnMas = new Button(this);
        GridLayout.LayoutParams btnMasParams = new GridLayout.LayoutParams();
        btnMasParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        btnMasParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        btnMasParams.columnSpec = GridLayout.spec(1, 1, 0.15f);
        btnMas.setLayoutParams(btnMasParams);
        btnMas.setText("+");

        // TextView para la cantidad
        TextView cantidad = new TextView(this);
        GridLayout.LayoutParams cantidadParams = new GridLayout.LayoutParams();
        cantidadParams.width = 0;
        cantidadParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        cantidadParams.columnSpec = GridLayout.spec(2, 2, 0.5f);
        //cantidadParams.setGravity(Gravity.CENTER);
        cantidad.setLayoutParams(cantidadParams);
        cantidad.setText(String.valueOf(consumicion.getCantidadConsumicion()));
        cantidad.setTextColor(Color.BLACK);
        cantidad.setTextSize(15);

        // Botón -
        Button btnMenos = new Button(this);
        GridLayout.LayoutParams btnMenosParams = new GridLayout.LayoutParams();
        btnMenosParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        btnMenosParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        btnMenosParams.columnSpec = GridLayout.spec(4, 1, 0.15f);
        btnMenos.setLayoutParams(btnMenosParams);
        btnMenos.setText("-");

        // Botón comentario
        Button btncomentario = new Button(this);
        GridLayout.LayoutParams btnComentarioParams = new GridLayout.LayoutParams();
        btnComentarioParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        btnComentarioParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        btnComentarioParams.columnSpec = GridLayout.spec(5, 1, 0.15f);
        btncomentario.setLayoutParams(btnComentarioParams);
        btncomentario.setText("✍");

        // Añadir vistas al GridLayout
        gridLayout.addView(nombre);
        gridLayout.addView(btnMas);
        gridLayout.addView(cantidad);
        gridLayout.addView(btnMenos);
        gridLayout.addView(btncomentario);

        // Añadir GridLayout al layout principal
        layoutComanda.addView(gridLayout);

        // Añadir a las listas correspondientes
        listaPedidoCliente.add(consumicion);
        listaBotonesMasConsumicion.add(btnMas);
        listaBotonesMenosConsumicion.add(btnMenos);
        listaCantidades.add(cantidad);
        listaComentarios.add(btncomentario);
        eventoMasMenosConsumiconComentarios(btnMenos, btnMas,btncomentario);
    }

    private void eventoMasMenosConsumiconComentarios(Button btnMenos, Button btnMas,Button btnComentario) {

        btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = listaBotonesMasConsumicion.indexOf(v);
                if (index != -1 && index < listaPedidoCliente.size()) {
                    listaPedidoCliente.get(index).setCantidadConsumicion(listaPedidoCliente.get(index).getCantidadConsumicion() + 1);
                    listaCantidades.get(index).setText(String.valueOf(listaPedidoCliente.get(index).getCantidadConsumicion()));
                }
            }
        });

        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = listaBotonesMenosConsumicion.indexOf(v);
                if (index != -1 && index < listaPedidoCliente.size() && listaPedidoCliente.get(index).getCantidadConsumicion() > 1) {
                    listaPedidoCliente.get(index).setCantidadConsumicion(listaPedidoCliente.get(index).getCantidadConsumicion() - 1);
                    listaCantidades.get(index).setText(String.valueOf(listaPedidoCliente.get(index).getCantidadConsumicion()));
                }else{
                    listaPedidoCliente.remove(index);
                    listaBotonesMasConsumicion.remove(index);
                    listaBotonesMenosConsumicion.remove(index);
                    listaCantidades.remove(index);
                    layoutComanda.removeViewAt(index);
                }
            }
        });

        btnComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = listaComentarios.indexOf(v);
                AlertDialog.Builder builder = new AlertDialog.Builder(Comanda.this);
                EditText editText = new EditText(Comanda.this);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setTitle("Comentario: ");
                builder.setMessage("¿Introduzca algun comentario");
                builder.setView(editText);

                builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listaPedidoCliente.get(index).setComentarios(editText.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity,secondActivity);
        intent.putExtra("idBar",idBar);
        intent.putExtra("numero_mesas",numeroMesas);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}
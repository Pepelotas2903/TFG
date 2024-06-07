package com.example.pda;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pda.conexion.Connexion;

import java.util.ArrayList;

public class Devoluciones extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private DrawerLayout drawerLayout;
    private ImageView menu;
    int numeroMesas;
    private LinearLayout layoutMesas,vtnPagos,vtnSalir,vtnAnulaciones,vtnComanda;
    private ArrayList<Button> listaMesas;
    private ArrayList<DetalleComanda> listaCuenta;
    private ListView listViewDevoluciones;
    private int idBar;
    private Intent intent;
    private Connexion acdbh;
    private TextView mesaSeleccionada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devoluciones);

        acdbh = new Connexion(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        vtnAnulaciones = findViewById(R.id.anulaciones);
        vtnComanda = findViewById(R.id.comanda);
        vtnPagos = findViewById(R.id.pagos);
        vtnSalir = findViewById(R.id.salir);
        listViewDevoluciones = findViewById(R.id.listViewDevoluciones);

        layoutMesas = findViewById(R.id.linearMesasDevoluciones);
        listaMesas = new ArrayList<>();
        listaCuenta = new ArrayList<>();

        intent = getIntent();
        cargarMesas(intent);


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        vtnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Devoluciones.this, MainActivity.class);
                acdbh.close();
            }
        });

        vtnComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Devoluciones.this, Comanda.class);
                acdbh.close();
            }
        });

        vtnPagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Devoluciones.this, Pagos.class);
                acdbh.close();

            }
        });

        vtnAnulaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

    }

    private void cargarMesas(Intent intent) {
        if(listaMesas.size() > 0){
            layoutMesas.removeAllViews();
            listaMesas.removeAll(listaMesas);
        }

        mesaSeleccionada = findViewById(R.id.txtMesaSeeccionadaDevoluciones);
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
                    rellenarCuenta();
                }
            });
        }
    }

    private void rellenarCuenta() {
        listaCuenta.removeAll(listaCuenta);
        acdbh.rellenarListaCuenta(acdbh,listaCuenta,idBar,mesaSeleccionada.getText().toString(),getString(R.string.Pendiente));
        if(listaCuenta.size() > 0){
            rellenarListViewDevoluciones();
        }else{
            listaCuenta.clear();
            rellenarListViewDevoluciones();
            Toast.makeText(Devoluciones.this, "La mesa esta sin ocupar", Toast.LENGTH_SHORT).show();
        }
    }

    private void rellenarListViewDevoluciones (){
        AdaptadorListViewDevoluciones adapter = new AdaptadorListViewDevoluciones(Devoluciones.this,listaCuenta);
        listViewDevoluciones.setAdapter(adapter);
        listViewDevoluciones.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        modificarComanda(position,listaCuenta.get(position));
    }

    private void modificarComanda(int position, DetalleComanda detalleComanda) {
        DetalleComanda detalle = detalleComanda;
        int index = listaCuenta.indexOf(detalle);
        if (index != -1 && index < listaCuenta.size()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Devoluciones.this);
            EditText editText = new EditText(Devoluciones.this);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setTitle("CANTIDADES: ");
            builder.setMessage("¿Introduzca el cuantas cantidades desea borrar?");
            builder.setView(editText);

            builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int cantidad = Integer.parseInt(editText.getText().toString());
                    if((detalle.getCantidad() - cantidad) < 0){
                        Toast.makeText(Devoluciones.this, "ERROR,Existencias insuficientes", Toast.LENGTH_SHORT).show();
                    }else if((detalle.getCantidad() - cantidad) > 0){
                        detalle.setCantidad(detalle.getCantidad() - cantidad);
                        acdbh.modificarComanda(acdbh,detalle);
                    }else{
                        acdbh.borrarDetalleComanda(acdbh,detalle,getString(R.string.Pagada));
                    }
                    mesaSeleccionada.setText("");
                    cargarMesas(intent);
                    rellenarCuenta();
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
    }

    public void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity,secondActivity);
        intent.putExtra("idBar",idBar);
        intent.putExtra("numero_mesas",numeroMesas);
        intent.putExtra("mesaSeleccionada",mesaSeleccionada.getText().toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }
}
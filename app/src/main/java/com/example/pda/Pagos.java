package com.example.pda;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pda.conexion.Connexion;

import java.util.ArrayList;

public class Pagos extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menu;
    int numeroMesas;
    private LinearLayout layoutMesas, linearCuenta,vtnSalir,vtnPagos,vtnAnulaciones,vtnComanda;
    private ArrayList<Button> listaMesas;
    private ArrayList<DetalleComanda> listaCuenta;
    private int idBar,sumaTotal;
    private Intent intent;
    private Connexion acdbh;
    private TextView mesaSeleccionada;
    private Button btnPagoEfectivo,btnPagoTarjeta,btnSepararCuenta,btnDarPropina;
    private TextView txtTotal,txtPropina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos);

        acdbh = new Connexion(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        vtnAnulaciones = findViewById(R.id.anulaciones);
        vtnComanda = findViewById(R.id.comanda);
        vtnPagos = findViewById(R.id.pagos);
        vtnSalir = findViewById(R.id.salir);

        btnDarPropina = findViewById(R.id.btnAgregarPropina);
        btnPagoEfectivo = findViewById(R.id.btnPagoEfectivo);
        btnPagoTarjeta = findViewById(R.id.btnPagoTarjeta);
        btnSepararCuenta = findViewById(R.id.btnSepararCuenta);


        layoutMesas = findViewById(R.id.linearMesasPagos);
        listaMesas = new ArrayList<>();
        listaCuenta = new ArrayList<>();

        intent = getIntent();
        cargarMesas(intent);

        btnDarPropina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mesaSeleccionada.getText().toString().equalsIgnoreCase("") && listaCuenta.size() > 0){
                    mostrarPopUpPropina();
                }else{
                    Toast.makeText(Pagos.this, "Seleccione una mesa con comandas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPagoEfectivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mesaSeleccionada.getText().toString().equalsIgnoreCase("") && listaCuenta.size() > 0){
                    nostrarPopUpPagarEfectivo();
                }else{
                    Toast.makeText(Pagos.this, "Seleccione una mesa con comandas", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnPagoTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mesaSeleccionada.getText().toString().equalsIgnoreCase("") && listaCuenta.size() > 0){
                    String totalCambiado = txtTotal.getText().toString().replace("€","");
                    borrarComanda(totalCambiado,getString(R.string.Tarjeta));
                    Toast.makeText(Pagos.this, "Comanda pagada", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Pagos.this, "Seleccione una mesa con comandas", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSepararCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mesaSeleccionada.getText().toString().equalsIgnoreCase("") && listaCuenta.size() > 0){
                    redirectActivity(Pagos.this, PagarSeparado.class);
                    acdbh.close();
                }else{
                    Toast.makeText(Pagos.this, "Seleccione una mesa con comandas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        vtnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Pagos.this, MainActivity.class);
                acdbh.close();
            }
        });
        vtnComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Pagos.this, Comanda.class);
                acdbh.close();
            }
        });

        vtnPagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        vtnAnulaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(Pagos.this, Devoluciones.class);
                acdbh.close();
            }
        });
    }

    private void nostrarPopUpPagarEfectivo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Pagos.this);
        EditText editText = new EditText(Pagos.this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setTitle("INGRESO: ");
        builder.setMessage("¿Con cuanto desea pagar?");
        builder.setView(editText);

        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if(Integer.parseInt(editText.getText().toString()) > 0){
                        Double ingreso = Double.parseDouble(editText.getText().toString());
                        String totalCambiado = txtTotal.getText().toString().replace("€","");
                        if(ingreso >= Double.parseDouble(totalCambiado) ){
                            Double vuelta = ingreso - Double.parseDouble(totalCambiado);
                            if(vuelta == 0){
                                Toast.makeText(Pagos.this, "Comanda pagada", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(Pagos.this, "Debe devolver: " + vuelta + "€", Toast.LENGTH_LONG).show();
                            }
                            borrarComanda(totalCambiado,getString(R.string.Efectivo));

                        }else{
                            Toast.makeText(Pagos.this, "Ingreso menor que Total a pagar", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(Pagos.this, "No has ingresado efectivo", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(Pagos.this, "Error al pagar, pruebe de nuevo", Toast.LENGTH_SHORT).show();
                }
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

    private void borrarComanda(String totalCambiado,String formaPago) {
        String pagoSinPropina = String.valueOf(Double.parseDouble(totalCambiado) - Double.parseDouble(txtPropina.getText().toString().replace("€","")));
        acdbh.borrarComanda(acdbh,listaCuenta.get(0).getIdComanda(),pagoSinPropina,formaPago,getString(R.string.Completa),getString(R.string.Pagada));
        cargarMesas(intent);
        listaCuenta.removeAll(listaCuenta);
        linearCuenta.removeAllViews();
        mesaSeleccionada.setText("");
        txtPropina.setText("");
        txtTotal.setText("");
    }

    private void mostrarPopUpPropina() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Pagos.this);
        EditText editText = new EditText(Pagos.this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setTitle("PROPINA: ");
        builder.setMessage("¿Cuanta propina desea añadir?");
        builder.setView(editText);

        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if(Integer.parseInt(editText.getText().toString()) < 1000){
                        String propinaAntigua = txtPropina.getText().toString().replace("€","");
                        txtPropina.setText(editText.getText().toString() + "€");
                        String totalCambiado = txtTotal.getText().toString().replace("€","");
                        txtTotal.setText((Integer.parseInt(totalCambiado) - Integer.parseInt(propinaAntigua) + Integer.parseInt(editText.getText().toString())) + "€");
                        dialog.dismiss();
                    }else {
                        Toast.makeText(Pagos.this, "Máximo de propina 1000€", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(Pagos.this, "Exceso de propina", Toast.LENGTH_SHORT).show();
                }
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

    private void cargarMesas(Intent intent) {
        if(listaMesas.size() > 0){
            layoutMesas.removeAllViews();
            listaMesas.removeAll(listaMesas);
        }

        mesaSeleccionada = findViewById(R.id.mesaSeleccionadaPagos);
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
        linearCuenta = findViewById(R.id.linearCuenta);
        txtTotal = findViewById(R.id.txtTotal);
        txtPropina = findViewById(R.id.txtPropina);
        sumaTotal = 0;
        linearCuenta.removeAllViews();
        listaCuenta.removeAll(listaCuenta);
        acdbh.rellenarListaCuenta(acdbh,listaCuenta,idBar,mesaSeleccionada.getText().toString(),getString(R.string.Pendiente));
        if(listaCuenta.size() > 0){
            agregarALaVista();
        }else{
            txtTotal.setText("");
            txtPropina.setText("");
            Toast.makeText(Pagos.this, "La mesa esta sin ocupar", Toast.LENGTH_SHORT).show();
        }
    }

    // Esta parte del codigo añade de forma dinamica elementos visuales a la vista
    private void agregarALaVista() {

        // Crear el TextView de "Cuenta"
        TextView tvCuenta = new TextView(this);
        tvCuenta.setText("Cuenta");
        tvCuenta.setTextSize(24);
        tvCuenta.setTypeface(Typeface.DEFAULT_BOLD);
        tvCuenta.setTextColor(Color.BLACK);
        tvCuenta.setGravity(Gravity.CENTER);
        linearCuenta.addView(tvCuenta);
        linearCuenta.addView(agregarLineaNegra(0,0));

        // Crear el TextView con el nombre de la mesa
        TextView tvMesa = new TextView(this);
        tvMesa.setText("Mesa: " + mesaSeleccionada.getText().toString());
        tvMesa.setPadding(0, 8, 0, 8);
        tvMesa.setTextSize(18);
        tvMesa.setTextColor(Color.BLACK);
        tvMesa.setGravity(Gravity.CENTER);
        linearCuenta.addView(tvMesa);
        linearCuenta.addView(agregarLineaNegra(0,0));

        // Crear el encabezado de las columnas usando GridLayout
        GridLayout filaGrid = new GridLayout(this);
        filaGrid.setColumnCount(7);
        filaGrid.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Añadir los encabezados de las columnas
        agregarTextoEncabezado(filaGrid, "CAN", 1);
        agregarTextoEncabezado(filaGrid, "ARTICULO", 3);
        agregarTextoEncabezado(filaGrid, "RONDA", 1);
        agregarTextoEncabezado(filaGrid, "P.U", 1);
        agregarTextoEncabezado(filaGrid, "IMPORTE", 1);

        linearCuenta.addView(filaGrid);
        int ultimaRonda = 1;
        for (DetalleComanda detalle : listaCuenta) {
            if(ultimaRonda != detalle.getNumeroRonda()){
                ultimaRonda = detalle.getNumeroRonda();
                agregarTextoCelda(filaGrid, "-", 1);
                agregarTextoCelda(filaGrid, "-----", 3);
                agregarTextoCelda(filaGrid, "-", 1);
                agregarTextoCelda(filaGrid, "-", 1);
                agregarTextoCelda(filaGrid, "-", 1);
            }
            agregarTextoCelda(filaGrid, String.valueOf(detalle.getCantidad()), 1);
            agregarTextoCelda(filaGrid, detalle.getNombreConsumicion(), 3);
            agregarTextoCelda(filaGrid, String.valueOf(detalle.getNumeroRonda()), 1);
            agregarTextoCelda(filaGrid, String.valueOf(detalle.getPrecioConsumicion()) + " €", 1);
            sumaTotal += detalle.getCantidad() * detalle.getPrecioConsumicion();
            agregarTextoCelda(filaGrid, String.valueOf(detalle.getCantidad() * detalle.getPrecioConsumicion()) + " €", 1);
        }

        txtTotal.setText(String.valueOf(sumaTotal) + "€");
        txtPropina.setText("0€");
    }

    private View agregarLineaNegra(int margenIzquierdo,int margenDerecho){
        View linea = new View(this);
        LinearLayout.LayoutParams paramsLinea1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
        );
        paramsLinea1.setMargins(margenIzquierdo, 8, 10, margenDerecho);
        linea.setLayoutParams(paramsLinea1);
        linea.setBackgroundColor(Color.BLACK);
        return linea;
    }

    private void agregarTextoEncabezado(GridLayout gridLayout, String texto, int span) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(2, 2, 2, 2);
        textView.setTextColor(Color.BLACK);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, span, 1f);
        params.width = 0;
        textView.setLayoutParams(params);
        gridLayout.addView(textView);
    }

    private void agregarTextoCelda(GridLayout gridLayout, String texto, int span) {
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(2, 2, 2, 2);
        textView.setTextColor(Color.BLACK);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, span, 1f);
        params.width = 0;
        textView.setLayoutParams(params);
        gridLayout.addView(textView);
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
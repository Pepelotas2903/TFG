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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pda.conexion.Connexion;

import java.util.ArrayList;

public class PagarSeparado extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menu;
    int numeroMesas;
    String mesaSelec;
    private LinearLayout linearCuentaSeparada,linearCuentaNueva,vtnSalir,vtnPagos,vtnAnulaciones,vtnComanda;
    private ArrayList<DetalleComanda> listaCuenta,listaCuentaSeparada;
    private ArrayList<Button> listaBotonBajar,listaBotonSubir;
    private int idBar,sumaTotal;
    private Intent intent;
    private Connexion acdbh;
    private TextView mesaSeleccionada;
    private Button btnPagoEfectivo,btnPagoTarjeta,btnDarPropina;

    private TextView txtTotal,txtPropina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagar_separado);

        acdbh = new Connexion(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        vtnAnulaciones = findViewById(R.id.anulaciones);
        vtnComanda = findViewById(R.id.comanda);
        vtnPagos = findViewById(R.id.pagos);
        vtnSalir = findViewById(R.id.salir);

        btnDarPropina = findViewById(R.id.btnPropinaSeparado);
        btnPagoEfectivo = findViewById(R.id.btnPagoEfectivoSeparado);
        btnPagoTarjeta = findViewById(R.id.btnTarjetaSeparado);

        listaCuenta = new ArrayList<>();
        listaCuentaSeparada = new ArrayList<>();
        listaBotonBajar = new ArrayList<>();
        listaBotonSubir = new ArrayList<>();

        intent = getIntent();
        descargarIntent(intent);
        rellenarCuenta();

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        vtnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(PagarSeparado.this, MainActivity.class);
                acdbh.close();
            }
        });
        vtnComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(PagarSeparado.this, Comanda.class);
                acdbh.close();
            }
        });

        vtnPagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(PagarSeparado.this, Pagos.class);
                acdbh.close();
            }
        });

        vtnAnulaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(PagarSeparado.this, Devoluciones.class);
                 acdbh.close();
            }
        });

        btnDarPropina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarPopUpPropina();
            }
        });

        btnPagoEfectivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listaCuentaSeparada.size() > 0){
                    nostrarPopUpPagarEfectivo();
                }else{
                    Toast.makeText(PagarSeparado.this, "Seleccione las consumiciones", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnPagoTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listaCuentaSeparada.size() > 0 && (!txtTotal.getText().toString().replace("€","").equalsIgnoreCase(""))){
                    String totalCambiado = txtTotal.getText().toString().replace("€","");
                    borrarComanda(totalCambiado,getString(R.string.Tarjeta));
                    Toast.makeText(PagarSeparado.this, "Pago realizado con exito", Toast.LENGTH_SHORT).show();
                    redirectActivity(PagarSeparado.this, Pagos.class);
                }else{
                    Toast.makeText(PagarSeparado.this, "Seleccione las consumiciones", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void mostrarPopUpPropina() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText editText = new EditText(this);
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
                        if(totalCambiado.equalsIgnoreCase("")){
                            totalCambiado = "0";
                        }
                        txtTotal.setText((Integer.parseInt(totalCambiado) - Integer.parseInt(propinaAntigua) + Integer.parseInt(editText.getText().toString())) + "€");
                        dialog.dismiss();
                    }else {
                        Toast.makeText(PagarSeparado.this, "Máximo de propina 1000€", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(PagarSeparado.this, "Exceso de propina", Toast.LENGTH_SHORT).show();
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

    private void rellenarCuenta() {
        linearCuentaSeparada = findViewById(R.id.linearCuentaSeparada);
        linearCuentaNueva = findViewById(R.id.linearCuentaSeparadaNueva);
        txtTotal = findViewById(R.id.txtTotalSeparada);
        txtPropina = findViewById(R.id.txtPropinaSeparada);
        txtPropina.setText("0€");
        acdbh.rellenarListaCuenta(acdbh,listaCuenta,idBar,mesaSeleccionada.getText().toString(),getString(R.string.Pendiente));
        rellenarListaCuentaSeparada();
        if(listaCuenta.size() > 0){
            agregarALaVista(listaCuenta,true);
        }else{
            txtTotal.setText("");
            txtPropina.setText("");
            Toast.makeText(PagarSeparado.this, "La mesa esta sin ocupar", Toast.LENGTH_SHORT).show();
        }
    }

    private void nostrarPopUpPagarEfectivo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PagarSeparado.this);
        EditText editText = new EditText(PagarSeparado.this);
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
                                Toast.makeText(PagarSeparado.this, "Comanda pagada", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(PagarSeparado.this, "Debe devolver: " + vuelta + "€", Toast.LENGTH_LONG).show();
                            }
                            borrarComanda(totalCambiado,getString(R.string.Efectivo));
                            Toast.makeText(PagarSeparado.this, "Pago realizado con exito", Toast.LENGTH_SHORT).show();
                            redirectActivity(PagarSeparado.this, Pagos.class);
                        }else{
                            Toast.makeText(PagarSeparado.this, "Ingreso menor que Total a pagar", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(PagarSeparado.this, "No has ingresado efectivo", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(PagarSeparado.this, "Error al pagar, pruebe de nuevo", Toast.LENGTH_SHORT).show();
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
        acdbh.borrarComandaParcial(acdbh,pagoSinPropina,formaPago,getString(R.string.Parcial),getString(R.string.Pagada),listaCuentaSeparada);
    }

    private void rellenarListaCuentaSeparada() {

        for(DetalleComanda detalle : listaCuenta){
            listaBotonSubir.add(new Button(this));
            listaBotonBajar.add(new Button(this));
            DetalleComanda detalleNuevo = new DetalleComanda(detalle.getIdComanda(),
                    detalle.getIdComanda(),
                    detalle.getIdConsumicion(),
                    detalle.getNombreConsumicion(),
                    detalle.getNumeroRonda(),
                    0,
                    detalle.getPrecioConsumicion());
            detalleNuevo.setCantidad(0);
            listaCuentaSeparada.add(detalleNuevo);
        }
    }

    private void agregarALaVista(ArrayList<DetalleComanda>listaUsar, boolean esListaComanda) {

        // Crear el TextView de "Cuenta"
        TextView tvCuenta = new TextView(this);
        tvCuenta.setText("Cuenta");
        tvCuenta.setTextSize(24);
        tvCuenta.setTypeface(Typeface.DEFAULT_BOLD);
        tvCuenta.setTextColor(Color.BLACK);
        tvCuenta.setGravity(Gravity.CENTER);

        if(esListaComanda){
            linearCuentaSeparada.addView(tvCuenta);
            linearCuentaSeparada.addView(agregarLineaNegra(0,0));
        }else{
            linearCuentaNueva.addView(tvCuenta);
            linearCuentaNueva.addView(agregarLineaNegra(0,0));
        }


        // Crear el TextView con el nombre de la mesa
        TextView tvMesa = new TextView(this);
        tvMesa.setText("Mesa: " + mesaSeleccionada.getText().toString());
        tvMesa.setPadding(0, 8, 0, 8);
        tvMesa.setTextSize(18);
        tvMesa.setTextColor(Color.BLACK);
        tvMesa.setGravity(Gravity.CENTER);

        if(esListaComanda){
            linearCuentaSeparada.addView(tvMesa);
            linearCuentaSeparada.addView(agregarLineaNegra(0,0));
        }else{
            linearCuentaNueva.addView(tvMesa);
            linearCuentaNueva.addView(agregarLineaNegra(0,0));
        }

        // Crear el encabezado de las columnas usando GridLayout
        GridLayout filaGrid = new GridLayout(this);
        filaGrid.setColumnCount(8);
        filaGrid.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Añadir los encabezados de las columnas
        agregarTextoEncabezado(filaGrid, "CAN", 1);
        agregarTextoEncabezado(filaGrid, "ARTI", 3);
        agregarTextoEncabezado(filaGrid, "RONDA", 1);
        agregarTextoEncabezado(filaGrid, "P.U", 1);
        agregarTextoEncabezado(filaGrid, "PVP", 1);
        agregarTextoEncabezado(filaGrid, "DIV", 1);

        if(esListaComanda){
            linearCuentaSeparada.addView(filaGrid);
        }else{
            linearCuentaNueva.addView(filaGrid);
        }

        //linearCuentaSeparada.addView(filaGrid);
        int ultimaRonda = 1;
        sumaTotal = 0;
        for (DetalleComanda detalle : listaUsar) {
            if(detalle.getCantidad() > 0){
                if(ultimaRonda != detalle.getNumeroRonda()){
                    ultimaRonda = detalle.getNumeroRonda();
                    agregarTextoCelda(filaGrid, "-", 1);
                    agregarTextoCelda(filaGrid, "-----", 3);
                    agregarTextoCelda(filaGrid, "-", 1);
                    agregarTextoCelda(filaGrid, "-", 1);
                    agregarTextoCelda(filaGrid, "-", 1);
                    agregarTextoCelda(filaGrid, "-", 1);
                }
                agregarTextoCelda(filaGrid, String.valueOf(detalle.getCantidad()), 1);
                agregarTextoCelda(filaGrid, detalle.getNombreConsumicion(), 3);
                agregarTextoCelda(filaGrid, String.valueOf(detalle.getNumeroRonda()), 1);
                agregarTextoCelda(filaGrid, String.valueOf(detalle.getPrecioConsumicion()) + " €", 1);
                if(!esListaComanda){
                    sumaTotal += detalle.getCantidad() * detalle.getPrecioConsumicion();
                }
                agregarTextoCelda(filaGrid, String.valueOf(detalle.getCantidad() * detalle.getPrecioConsumicion()) + " €", 1);
                agregarBotonSeparar(filaGrid,esListaComanda,detalle);
            }
        }

        if(!esListaComanda){
            int saberPorpina = 0;
            if(!txtPropina.getText().toString().replace("€","").equalsIgnoreCase("")){
              saberPorpina =Integer.parseInt(txtPropina.getText().toString().replace("€",""));
            }
            txtTotal.setText(String.valueOf(sumaTotal + saberPorpina) + "€");
        }
    }

    private void agregarBotonSeparar(GridLayout gridLayout,Boolean esListaComanda,DetalleComanda detalle) {
        Button button;
        if(esListaComanda){
            int indice = listaCuenta.indexOf(detalle);
            button = listaBotonBajar.get(indice);
            button.setText("↓");
            //listaBotonBajar.add(button);
        }else{
            int indice = listaCuentaSeparada.indexOf(detalle);
            button = listaBotonSubir.get(indice);
            button.setText("↑");
            //listaBotonSubir.add(button);
        }

        ViewGroup parent = (ViewGroup) button.getParent();
        if (parent != null) {
            parent.removeView(button);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index;
                Boolean esListaCuenta = true;
                if(listaBotonBajar.contains(v)){
                    index = listaBotonBajar.indexOf(v);
                }else{
                    index = listaBotonSubir.indexOf(v);
                    esListaCuenta = false;
                }

                if(esListaCuenta){
                    if (listaCuenta.get(index).getCantidad() > 0){
                        listaCuenta.get(index).setCantidad(listaCuenta.get(index).getCantidad() - 1);
                        listaCuentaSeparada.get(index).setCantidad(listaCuentaSeparada.get(index).getCantidad() + 1);
                    }

                }else{
                    if (listaCuentaSeparada.get(index).getCantidad() > 0){
                        listaCuentaSeparada.get(index).setCantidad(listaCuentaSeparada.get(index).getCantidad() - 1);
                        listaCuenta.get(index).setCantidad(listaCuenta.get(index).getCantidad() + 1);
                    }
                }
                linearCuentaSeparada.removeAllViews();
                linearCuentaNueva.removeAllViews();
                agregarALaVista(listaCuenta,true);
                agregarALaVista(listaCuentaSeparada,false);

            }
        });

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        params.width = 0;
        button.setLayoutParams(params);
        gridLayout.addView(button);
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

    private void descargarIntent(Intent intent) {
        mesaSeleccionada = findViewById(R.id.mesaSeleccionadaPagosSeparada);
        if (intent != null) {
            numeroMesas = intent.getIntExtra("numero_mesas",0);
            idBar = intent.getIntExtra("idBar",0);
            mesaSelec = intent.getStringExtra("mesaSeleccionada");
            mesaSeleccionada.setText(mesaSelec);
        }
    }

    public void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity,secondActivity);
        intent.putExtra("idBar",idBar);
        intent.putExtra("numero_mesas",numeroMesas);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }
}
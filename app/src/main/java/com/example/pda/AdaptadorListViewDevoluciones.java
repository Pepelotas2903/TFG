package com.example.pda;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorListViewDevoluciones extends ArrayAdapter {
    private Activity context;
    private ArrayList<DetalleComanda> listadoConsumiciones;

    public AdaptadorListViewDevoluciones(Activity context, ArrayList<DetalleComanda> listaPedidos) {
        super(context, R.layout.list_view_consumiciones_devoluciones, listaPedidos);
        this.context = context;
        this.listadoConsumiciones = listaPedidos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.list_view_consumiciones_devoluciones,null);

        ((TextView) item.findViewById(R.id.txt_nombre_articulo)).setText(listadoConsumiciones.get(position).getNombreConsumicion());
        ((TextView) item.findViewById(R.id.txt_precio_devolucion)).setText(String.valueOf(String.valueOf(listadoConsumiciones.get(position).getPrecioConsumicion())));
        ((TextView) item.findViewById(R.id.txt_cantidad_devolucion)).setText(String.valueOf(listadoConsumiciones.get(position).getCantidad()));
        ((TextView) item.findViewById(R.id.txt_ronda_devolucion)).setText(String.valueOf(listadoConsumiciones.get(position).getNumeroRonda()));
        return item;
    }
}

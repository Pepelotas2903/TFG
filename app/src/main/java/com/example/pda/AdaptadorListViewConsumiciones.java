package com.example.pda;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorListViewConsumiciones extends ArrayAdapter {
    private Activity context;
    private ArrayList<Consumiciones> listadoConsumiciones;

    public AdaptadorListViewConsumiciones(Activity context, ArrayList<Consumiciones> listaPedidos) {
        super(context, R.layout.list_view_consumicion, listaPedidos);
        this.context = context;
        this.listadoConsumiciones = listaPedidos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.list_view_consumicion,null);

        ((TextView) item.findViewById(R.id.txt_nombre_Consumicion)).setText(listadoConsumiciones.get(position).getNombreConsumicion());
        ((TextView) item.findViewById(R.id.txt_precio)).setText(String.valueOf(listadoConsumiciones.get(position).getPrecioConsumicion()));
        ImageView img = item.findViewById(R.id.img_view_logo);
        cambiarImagenLayout(img,position);
        return item;
    }
    private void cambiarImagenLayout(ImageView imagenConsumiciones,int position) {

        String[] familias = getContext().getResources().getStringArray(R.array.familias);

        if(listadoConsumiciones.get(position).getFamiliaSeleccionada().equalsIgnoreCase(familias[0])){
            imagenConsumiciones.setImageResource(R.drawable.refrescos);
        } else if (listadoConsumiciones.get(position).getFamiliaSeleccionada().equalsIgnoreCase(familias[1])) {
            imagenConsumiciones.setImageResource(R.drawable.zumos);
        }else if (listadoConsumiciones.get(position).getFamiliaSeleccionada().equalsIgnoreCase(familias[2])){
            imagenConsumiciones.setImageResource(R.drawable.ginebras);
        }else if (listadoConsumiciones.get(position).getFamiliaSeleccionada().equalsIgnoreCase(familias[3])){
            imagenConsumiciones.setImageResource(R.drawable.ron);
        }else if (listadoConsumiciones.get(position).getFamiliaSeleccionada().equalsIgnoreCase(familias[4])){
            imagenConsumiciones.setImageResource(R.drawable.vodka);
        }else if (listadoConsumiciones.get(position).getFamiliaSeleccionada().equalsIgnoreCase(familias[5])) {
            imagenConsumiciones.setImageResource(R.drawable.whiskey);
        }else if (listadoConsumiciones.get(position).getFamiliaSeleccionada().equalsIgnoreCase(familias[6])) {
            imagenConsumiciones.setImageResource(R.drawable.licores);
        }else if (listadoConsumiciones.get(position).getFamiliaSeleccionada().equalsIgnoreCase(familias[7])) {
            imagenConsumiciones.setImageResource(R.drawable.bebidas_calientes);
        }else if (listadoConsumiciones.get(position).getFamiliaSeleccionada().equalsIgnoreCase(familias[8])) {
            imagenConsumiciones.setImageResource(R.drawable.snaks);
        }else if (listadoConsumiciones.get(position).getFamiliaSeleccionada().equalsIgnoreCase(familias[9])) {
            imagenConsumiciones.setImageResource(R.drawable.cocteles);
        }

    }

}

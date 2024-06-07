package com.example.pda;

import android.widget.Button;

public class DetalleComanda {

    private int idDetalle;
    private int idComanda;
    private int idConsumicion;
    private String nombreConsumicion;
    private int numeroRonda;
    private int cantidad;
    private int precioConsumicion;

    public DetalleComanda(int idDetalle, int idComanda, int idConsumicion, String nombreConsumicion, int numeroRonda, int cantidad, int precioConsumicion) {
        this.idDetalle = idDetalle;
        this.idComanda = idComanda;
        this.idConsumicion = idConsumicion;
        this.nombreConsumicion = nombreConsumicion;
        this.numeroRonda = numeroRonda;
        this.cantidad = cantidad;
        this.precioConsumicion = precioConsumicion;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdComanda() {
        return idComanda;
    }

    public void setIdComanda(int idComanda) {
        this.idComanda = idComanda;
    }

    public int getIdConsumicion() {
        return idConsumicion;
    }

    public void setIdConsumicion(int idConsumicion) {
        this.idConsumicion = idConsumicion;
    }

    public String getNombreConsumicion() {
        return nombreConsumicion;
    }

    public void setNombreConsumicion(String nombreConsumicion) {
        this.nombreConsumicion = nombreConsumicion;
    }

    public int getNumeroRonda() {
        return numeroRonda;
    }

    public void setNumeroRonda(int numeroRonda) {
        this.numeroRonda = numeroRonda;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getPrecioConsumicion() {
        return precioConsumicion;
    }

    public void setPrecioConsumicion(int precioConsumicion) {
        this.precioConsumicion = precioConsumicion;
    }
}

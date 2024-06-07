package com.example.pda;

public class Consumiciones {

    private int idConsumicion;
    private String NombreConsumicion;
    private int precioConsumicion;
    private String familiaSeleccionada;
    private int posicion;
    private int cantidadConsumicion;
    private String comentarios;

    public Consumiciones(int idConsumicion, String nombreConsumicion, int precioConsumicion,String familiaSeleccionada) {
        this.idConsumicion = idConsumicion;
        NombreConsumicion = nombreConsumicion;
        this.precioConsumicion = precioConsumicion;
        this.familiaSeleccionada = familiaSeleccionada;
    }

    public int getIdConsumicion() {
        return idConsumicion;
    }

    public void setIdConsumicion(int idConsumicion) {
        this.idConsumicion = idConsumicion;
    }

    public String getNombreConsumicion() {
        return NombreConsumicion;
    }

    public void setNombreConsumicion(String nombreConsumicion) {
        NombreConsumicion = nombreConsumicion;
    }

    public int getPrecioConsumicion() {
        return precioConsumicion;
    }

    public void setPrecioConsumicion(int precioConsumicion) {
        this.precioConsumicion = precioConsumicion;
    }

    public String getFamiliaSeleccionada() {
        return familiaSeleccionada;
    }

    public void setFamiliaSeleccionada(String familiaSeleccionada) {
        this.familiaSeleccionada = familiaSeleccionada;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public int getCantidadConsumicion() {
        return cantidadConsumicion;
    }

    public void setCantidadConsumicion(int cantidadConsumicion) {
        this.cantidadConsumicion = cantidadConsumicion;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }
}

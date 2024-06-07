package com.example.pda.conexion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

import com.example.pda.Consumiciones;
import com.example.pda.DetalleComanda;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Clase Connexion que gestiona la conexión a la base de datos y las operaciones CRUD.
 */
public class Connexion extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BD_TFG_DAM.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructor de la clase Connexion.
     * @param context El contexto de la aplicación.
     */
    public Connexion( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    /**
     * Método llamado cuando la base de datos es creada por primera vez.
     * @param db La base de datos.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    /**
     * Método llamado cuando la base de datos necesita ser actualizada.
     * @param db La base de datos.
     * @param oldVersion La versión antigua de la base de datos.
     * @param newVersion La nueva versión de la base de datos.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Comprueba si las credenciales del usuario son correctas.
     * @param acdbh Instancia de Connexion.
     * @param password La contraseña cifrada.
     * @param usuario El nombre de usuario.
     * @param idBar El ID del bar.
     * @return El número de mesas si las credenciales son correctas, de lo contrario 0.
     */
    public static int comprobarPassword(Connexion acdbh, String password,String usuario,int idBar){
        int numeroMesas = 0;
        SQLiteDatabase db = acdbh.getReadableDatabase();
        String[] args = new String[]{password,usuario};
        String sql = "SELECT * FROM Localizacion WHERE Password = ? and Usuario = ?;";
        Cursor c = db.rawQuery(sql,args);
        if(c.moveToFirst()){
            idBar = c.getInt(0);
            numeroMesas = c.getInt(2);
        }
        return numeroMesas;
    }

    /**
     * Comprueba si una mesa está ocupada.
     * @param acdbh Instancia de Connexion.
     * @param i El número de mesa.
     * @param mesaOcu La disponibilidad de la mesa.
     * @return Verdadero si la mesa está ocupada, falso si no esta osupada.
     */
    public static boolean mesaOcupada(Connexion acdbh,int i,String mesaOcu){
        Boolean ocupada = false;
        SQLiteDatabase db = acdbh.getReadableDatabase();
        String[] args = new String[]{String.valueOf(i),mesaOcu};
        String sql = "SELECT * FROM Comanda WHERE NumeroMesa = ? AND Disponibilidad = ?;";
        Cursor c = db.rawQuery(sql,args);
        if(c.moveToFirst()){
            ocupada = true;
        }
        return ocupada;
    }

    /**
     * Inserta los nombres de las familias en un spinner.
     * @param acdbh Instancia de Connexion.
     * @return Una lista de nombres de familias.
     */
    public ArrayList<String> insertarFamiliasSpinner(Connexion acdbh) {
        SQLiteDatabase db = acdbh.getReadableDatabase();
        String sql = "SELECT NombreTipo FROM TipoConsumicion";
        Cursor c = db.rawQuery(sql,null);
        ArrayList<String>listaFamilias = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                listaFamilias.add(c.getString(0));
            }while(c.moveToNext());
        }
        return listaFamilias;
    }

    /**
     * Rellena un array con las consumiciones de una familia seleccionada.
     * @param acdbh Instancia de Connexion.
     * @param listadoConsumiciones Lista de consumiciones a rellenar.
     * @param familiaSeleccionada La familia seleccionada.
     */
    public void rellenarArrayFamilia(Connexion acdbh, ArrayList<Consumiciones> listadoConsumiciones, String familiaSeleccionada) {

        SQLiteDatabase db = acdbh.getReadableDatabase();
        String[] args = new String[]{familiaSeleccionada.trim()};
        String sql = "SELECT Consumicion.IdConsumicion,Consumicion.NombreConsumicion,Consumicion.Precio FROM Consumicion INNER JOIN TipoConsumicion ON Consumicion.IdTipoConsumicion = TipoConsumicion.IdTipoConsumicion WHERE TipoConsumicion.NombreTipo = ?;";
        Cursor c = db.rawQuery(sql,args);
        if(c.moveToFirst()){
            listadoConsumiciones.removeAll(listadoConsumiciones);
            do{
                listadoConsumiciones.add(new Consumiciones(c.getInt(0),c.getString(1),c.getInt(2),familiaSeleccionada));
            }while(c.moveToNext());
        }

    }

    /**
     * Realiza la operación de marchar una comanda.
     * @param acdbh Instancia de Connexion.
     * @param listadoConsumiciones Lista de consumiciones.
     * @param mesaSeleccionada La mesa seleccionada.
     * @param mesaOcupada La disponibilidad de la mesa.
     * @param idBar El ID del bar.
     */
    public void marcharComanda(Connexion acdbh, ArrayList<Consumiciones> listadoConsumiciones, String mesaSeleccionada, String mesaOcupada, int idBar) {
        SQLiteDatabase db = acdbh.getReadableDatabase();
        int idComanda = buscarIdComanda(mesaSeleccionada,mesaOcupada,idBar,db);
        if( idComanda == -1){
            ContentValues nuevoRegistro = new ContentValues();
            LocalDate date = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                date = LocalDate.now();
            }

            nuevoRegistro.put("IdBar",idBar);
            nuevoRegistro.put("Fecha",date.toString());
            nuevoRegistro.put("Disponibilidad",Integer.parseInt(mesaOcupada));
            nuevoRegistro.put("NumeroMesa",Integer.parseInt(mesaSeleccionada));
            db.insert("Comanda",null,nuevoRegistro);
            idComanda = buscarIdComanda(mesaSeleccionada,mesaOcupada,idBar,db);

        }
            int numRonda = buscarRondaMax(idComanda,db);
            for(int i = 0; i <listadoConsumiciones.size(); i++){
                ContentValues nuevoRegistro = new ContentValues();
                nuevoRegistro.put("IdDetalleComanda",buscarMaxdeDetalle(db));
                nuevoRegistro.put("IdComanda",idComanda);
                nuevoRegistro.put("IdConsumicion",buscarIdConsumicion(listadoConsumiciones.get(i).getNombreConsumicion(),db));
                nuevoRegistro.put("NumeroRonda",numRonda);
                nuevoRegistro.put("Cantiddad",listadoConsumiciones.get(i).getCantidadConsumicion());
                nuevoRegistro.put("Descripcion",listadoConsumiciones.get(i).getComentarios());
                db.insert("DetalleComanda",null,nuevoRegistro);
            }
    }

    /**
     * Busca el máximo ID de detalle de comanda.
     * @param db La base de datos.
     * @return El ID máximo de detalle de comanda.
     */
    private int buscarMaxdeDetalle(SQLiteDatabase db) {
        int idDetalle = 1;
        String sql = "SELECT MAX(IdDetalleComanda) FROM DetalleComanda;";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            idDetalle = c.getInt(0) + 1;
        }
        return idDetalle;
    }

    /**
     * Busca el ID de una consumición por su nombre.
     * @param nombreConsumicion El nombre de la consumición.
     * @param db La base de datos.
     * @return El ID de la consumición.
     */
    private int buscarIdConsumicion(String nombreConsumicion, SQLiteDatabase db) {
        int idConsumicion = 0;
        String[] args = new String[]{String.valueOf(nombreConsumicion)};
        String sql = "SELECT IdConsumicion FROM Consumicion WHERE NombreConsumicion = ?;";
        Cursor c = db.rawQuery(sql, args);
        if (c.moveToFirst()) {
            idConsumicion = c.getInt(0);
        }
        return idConsumicion;
    }

    /**
     * Busca el número máximo de ronda de una comanda.
     * @param idComanda El ID de la comanda.
     * @param db La base de datos.
     * @return El número máximo de ronda.
     */
    private int buscarRondaMax(int idComanda, SQLiteDatabase db) {
        int numRonda = 1;
        String[] args = new String[]{String.valueOf(idComanda)};
        String sql = "SELECT NumeroRonda FROM DetalleComanda WHERE IdComanda = ?;";
        Cursor c = db.rawQuery(sql, args);
        if (c.moveToFirst()) {
            numRonda = c.getInt(0) + 1;
        }
        return numRonda;
    }

    /**
     * Busca el ID de una comanda por mesa seleccionada, disponibilidad e ID de bar.
     * @param mesaSeleccionada La mesa seleccionada.
     * @param mesaOcupada La disponibilidad de la mesa.
     * @param idBar El ID del bar.
     * @param db La base de datos.
     * @return El ID de la comanda.
     */
    private int buscarIdComanda(String mesaSeleccionada, String mesaOcupada,int idBar, SQLiteDatabase db) {
        int idComanda = -1;
        String[] args = new String[]{mesaSeleccionada, mesaOcupada, String.valueOf(idBar)};
        String sql = "SELECT IdComanda FROM Comanda WHERE NumeroMesa = ? AND Disponibilidad = ? AND IdBar = ?;";
        Cursor c = db.rawQuery(sql, args);
        if (c.moveToFirst()) {
            idComanda = c.getInt(0);
        }
        return idComanda;
    }

    /**
     * Rellena una lista de cuentas con los detalles de una comanda.
     * @param acdbh Instancia de Connexion.
     * @param listaCuenta Lista de detalles de comanda.
     * @param idBar El ID del bar.
     * @param mesaSeleccionada La mesa seleccionada.
     * @param mesaOcupada La disponibilidad de la mesa.
     */
    public void rellenarListaCuenta(Connexion acdbh, ArrayList<DetalleComanda> listaCuenta, int idBar, String mesaSeleccionada, String mesaOcupada) {
        SQLiteDatabase db = acdbh.getReadableDatabase();
        int idComanda = buscarIdComanda(mesaSeleccionada,mesaOcupada,idBar,db);
        String[] args = new String[]{String.valueOf(idComanda)};
        String sql = "SELECT DetalleComanda.IdDetalleComanda,DetalleComanda.IdComanda,DetalleComanda.IdConsumicion,Consumicion.NombreConsumicion,DetalleComanda.NumeroRonda,DetalleComanda.Cantiddad,Consumicion.Precio FROM DetalleComanda INNER JOIN Comanda ON DetalleComanda.IdComanda = Comanda.IdComanda INNER JOIN Consumicion ON DetalleComanda.IdConsumicion = Consumicion.IdConsumicion WHERE DetalleComanda.IdComanda = ?;";
        Cursor c = db.rawQuery(sql, args);
        if(c.moveToFirst()){
            listaCuenta.removeAll(listaCuenta);
            do{
                listaCuenta.add(new DetalleComanda(c.getInt(0),c.getInt(1),c.getInt(2),c.getString(3),c.getInt(4),c.getInt(5),c.getInt(6)));
            }while(c.moveToNext());
        }
    }

    /**
     * Borra una comanda y registra el pago.
     * @param acdbh Instancia de Connexion.
     * @param idComanda El ID de la comanda.
     * @param total El total pagado.
     * @param formaPago La forma de pago.
     * @param completa Indica si el pago fue completo.
     * @param disponibilidad La disponibilidad de la mesa.
     */
    public void borrarComanda(Connexion acdbh, int idComanda,String total,String formaPago,String completa,String disponibilidad) {
        SQLiteDatabase db = acdbh.getWritableDatabase();
        String[] args = {String.valueOf(idComanda)};
        db.delete("DetalleComanda", "IdComanda = ?",args);

        ContentValues valores = new ContentValues();
        valores.put("IdComanda", idComanda);
        valores.put("PagoEntero", completa);
        valores.put("DescripcionPago","Pago:" + total + " con " + formaPago);
        db.insert("ComandaPagada", null, valores);

        valores = new ContentValues();
        valores.put("Disponibilidad",disponibilidad);
        args = new String[]{String.valueOf(idComanda)};
        db.update("Comanda", valores,"IdComanda = ?" ,args);
    }

    /**
     * Borra una comanda parcialmente y registra el pago.
     * @param acdbh Instancia de Connexion.
     * @param pagoSinPropina El total pagado sin propina.
     * @param formaPago La forma de pago.
     * @param parcial Indica si el pago fue parcial.
     * @param disponibilidad La disponibilidad de la mesa.
     * @param listaDetalle Lista de detalles de la comanda.
     */
    public void borrarComandaParcial(Connexion acdbh, String pagoSinPropina, String formaPago, String parcial, String disponibilidad,ArrayList<DetalleComanda>listaDetalle) {
        SQLiteDatabase db = acdbh.getWritableDatabase();
        for (DetalleComanda detalle:listaDetalle) {
            if(detalle.getCantidad() > 0){
                ContentValues values = new ContentValues();
                String[] args = { String.valueOf(detalle.getCantidad()),String.valueOf(detalle.getIdComanda()),String.valueOf(detalle.getIdConsumicion()),String.valueOf(detalle.getNumeroRonda())};
                String query = "UPDATE DetalleComanda SET Cantiddad = Cantiddad - ? WHERE IdComanda = ? AND IdConsumicion = ? AND NumeroRonda = ?;";
                db.execSQL(query,args);

                //borrar de detalle comnada si es 0 la cantidad
                args = new String[]{String.valueOf(detalle.getIdComanda())};
                db.delete("DetalleComanda", "IdComanda = ? AND Cantiddad = 0",args);
            }
        }

        modificarEstadoComanda(disponibilidad, listaDetalle.get(0).getIdComanda(), db);

        ContentValues valores = new ContentValues();
        valores.put("IdComanda", listaDetalle.get(0).getIdComanda());
        valores.put("PagoEntero", parcial);
        valores.put("DescripcionPago","Pago:" + pagoSinPropina + " con " + formaPago);
        db.insert("ComandaPagada", null, valores);
    }

    /**
     * Modifica el estado de una comanda.
     * @param disponibilidad La disponibilidad de la mesa.
     * @param idComanda El ID de la comanda.
     * @param db La base de datos.
     */
    private static void modificarEstadoComanda(String disponibilidad, int idComanda, SQLiteDatabase db) {
        String[] args = {String.valueOf(idComanda)};
        String sql = "SELECT * FROM DetalleComanda WHERE IdComanda = ?";
        Cursor c = db.rawQuery(sql, args);
        if(c.moveToFirst()){
        }else {
            ContentValues valores = new ContentValues();
            valores.put("Disponibilidad", disponibilidad);
            args = new String[]{String.valueOf(idComanda)};
            db.update("Comanda", valores,"IdComanda = ?" ,args);
        }
    }

    /**
     * Modifica un detalle de comanda.
     * @param acdbh Instancia de Connexion.
     * @param detalle El detalle de comanda a modificar.
     */
    public void modificarComanda(Connexion acdbh, DetalleComanda detalle) {
        SQLiteDatabase db = acdbh.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("Cantiddad",detalle.getCantidad());
        String[] args = new String[]{String.valueOf(detalle.getIdComanda()),String.valueOf(detalle.getNumeroRonda()),String.valueOf(detalle.getIdConsumicion())};
        db.update("DetalleComanda", valores,"IdComanda = ? AND NumeroRonda = ? AND IdConsumicion = ?" ,args);
    }

    /**
     * Borra un detalle de comanda.
     * @param acdbh Instancia de Connexion.
     * @param detalle El detalle de comanda a borrar.
     * @param disponibilidad La disponibilidad de la mesa.
     */
    public void borrarDetalleComanda(Connexion acdbh, DetalleComanda detalle, String disponibilidad) {
        SQLiteDatabase db = acdbh.getWritableDatabase();
        String[] args = {String.valueOf(detalle.getIdComanda()),String.valueOf(detalle.getNumeroRonda()),String.valueOf(detalle.getIdConsumicion())};
        db.delete("DetalleComanda", "IdComanda = ? AND NumeroRonda = ? AND IdConsumicion = ?",args);
        modificarEstadoComanda(disponibilidad,detalle.getIdComanda(),db);
    }
}



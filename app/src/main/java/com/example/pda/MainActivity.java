package com.example.pda;

import com.example.pda.conexion.*;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
/**
 * MainActivity es la actividad principal para el inicio de sesión del usuario y la gestión de sesiones.
 */
public class MainActivity extends AppCompatActivity {

    private EditText txtUsuario,txtPassword;
    private Button btnInicioSesion;
    private String passwordCifrada,passwordDescifrada;
    private String algoritmoCodificador = "SHA-256";
    private String claveEncriptacion = "alkhrgqiqh$%(/&%&";
    private String algoritmoAES = "AES";
    private String estadar = "UTF-8";
    private Connexion acdbh;
    private int idBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        acdbh = new Connexion(this);
        idBar = 0;
        //SQLiteDatabase db = dbHelper.getWritableDatabase();
        //acdbh = new Connexion(MainActivity.this);

        btnInicioSesion = findViewById(R.id.btnIngresar);
        btnInicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtUsuario = findViewById(R.id.usuario);
                txtPassword = findViewById(R.id.password);
                comprobacionSesion();
            }
        });
    }

    /**
     * Verifica la sesión del usuario comprobando las credenciales proporcionadas.
     */
    private void comprobacionSesion() {

        String usuario = txtUsuario.getText().toString();
        String password = txtPassword.getText().toString();

        passwordCifrada = cifrarDatos(password,claveEncriptacion);
        int numeroMesas = acdbh.comprobarPassword(acdbh,passwordCifrada.trim(),usuario,idBar);

        if(numeroMesas == 0){
            Toast.makeText(this, "Usuario o Contraseña incorrectos", Toast.LENGTH_LONG).show();
            txtPassword.setText("");
            txtUsuario.setText("");
        }else{
            redirectActivity(MainActivity.this, Comanda.class,numeroMesas);
        }

    }

    /**
     * Descifra los datos proporcionados usando la clave de encriptación dada.
     * @param datos Los datos a descifrar.
     * @param claveEncripDecrip La clave utilizada para el descifrado.
     * @return Los datos descifrados como una cadena.
     */
    private String descifrarDatos(String datos, String claveEncripDecrip){

        String datosDescriptadoString = null;
        try {
            SecretKeySpec secretKey = generateKey(claveEncripDecrip);
            Cipher cipher = Cipher.getInstance(algoritmoAES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] datosDecriptado = Base64.decode(datos,Base64.DEFAULT);
            byte[] datosDecriptadoBytes = cipher.doFinal(datosDecriptado);
            datosDescriptadoString = new String(datosDecriptadoBytes);

        }catch (Exception e){
            e.printStackTrace();
        }

        return datosDescriptadoString;
    }

    /**
     * Cifra los datos proporcionados usando la clave de encriptación dada.
     * @param datoAEncriptar Los datos a encriptar.
     * @param claveEncripDecrip La clave utilizada para la encriptación.
     * @return Los datos encriptados como una cadena.
     */
    private String cifrarDatos(String datoAEncriptar, String claveEncripDecrip) {

        String datosEncriptadosString = null;
        try {
            SecretKeySpec secretKey = generateKey(claveEncripDecrip);
            Cipher cipher = Cipher.getInstance(algoritmoAES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] datosEncriptados = cipher.doFinal(datoAEncriptar.getBytes());
            datosEncriptadosString = Base64.encodeToString(datosEncriptados, Base64.DEFAULT);

        }catch (Exception e){
            e.printStackTrace();
        }

        return datosEncriptadosString;
    }

    /**
     * Genera una clave secreta basada en la contraseña proporcionada.
     * @param pwd La contraseña utilizada para generar la clave.
     * @return El SecretKeySpec generado.
     */
    private SecretKeySpec generateKey (String pwd) {
        SecretKeySpec secretKey = null;
        try {
            MessageDigest sha = MessageDigest.getInstance(algoritmoCodificador);
            byte[] key = pwd.getBytes(estadar);
            key = sha.digest(key);
            secretKey = new SecretKeySpec(key,algoritmoAES);


        }catch (Exception e){
            e.printStackTrace();
        }
        return  secretKey;
    }

    /**
     * Redirige la actividad a otra actividad especificada.
     * @param activity La actividad actual.
     * @param secondActivity La actividad a la que se redirige.
     * @param numeroMesas El número de mesas que se pasa como dato extra.
     */
    public void redirectActivity(Activity activity, Class secondActivity,int numeroMesas){
        Intent intent = new Intent(activity,secondActivity);
        intent.putExtra("numero_mesas",numeroMesas);
        intent.putExtra("idBar",idBar);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}
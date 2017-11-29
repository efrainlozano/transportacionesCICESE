package com.transporte.cicese.transportaciones_cicese;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.transporte.cicese.transportaciones_cicese.funciones.funcionesGeneradoras;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Blanca Cecilia De Leon Rubio on 12/10/2017.
 */


public class RegistroPasajeroActivity extends AppCompatActivity {

    private EditText usuarioPasajero;
    private EditText contrasenaPasajero;
    private EditText nombrePasajero;
    private EditText aPaternoPasajero;
    private EditText aMaternoPasajero;
    private EditText numTelefonoPasajero;
    private Button registraPasajero;

    private Button popupD;
    private String dialogoMsg;

    funcionesGeneradoras fG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_pasajero);

        //Referencia a los controles de la interfaz
        usuarioPasajero = (EditText) findViewById(R.id.usuario_generado);
        contrasenaPasajero = (EditText) findViewById(R.id.contrasena_pasajero);
        nombrePasajero = (EditText) findViewById(R.id.nombre_pasajero);
        aPaternoPasajero = (EditText) findViewById(R.id.ap_pasajero);
        aMaternoPasajero = (EditText) findViewById(R.id.am_pasajero);
        numTelefonoPasajero = (EditText) findViewById(R.id.tel_pasajero);
        registraPasajero = (Button) findViewById(R.id.registroPasajero_btn);

        popupD = (Button) findViewById(R.id.popup3);
        dialogoMsg = "Este campo requiere un número de 10 dígitos.\nEl formato es el siguiente:" +
                "\n##########\ndonde los tres primeros números son la lada, éstos no se colocan entre \nparéntesis";


        //Si se presiona el icono de exclamacion en el campo de telefono
        popupD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUp();
            }
        });


        //Preparamos el metodo para registrar al pasajero
        registraPasajero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RegistroPasajeroActivity.SendPostRequest().execute();
            }
        });

    }

    private void PopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(dialogoMsg)
                .setTitle("Número de teléfono")
                .setCancelable(false)
                .setNeutralButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class SendPostRequest extends AsyncTask<String, Void, ArrayList> {

        protected ArrayList doInBackground(String... arg0) {
            fG= new funcionesGeneradoras(getApplicationContext());
            try {
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("tipo", "p");
                postDataParams.put("usuario", usuarioPasajero.getText().toString());
                postDataParams.put("contrasena", contrasenaPasajero.getText().toString());
                postDataParams.put("nombre", nombrePasajero.getText().toString());
                postDataParams.put("apellido_paterno", aPaternoPasajero.getText().toString());
                postDataParams.put("apellido_materno", aMaternoPasajero.getText().toString());
                postDataParams.put("numero_telefono", numTelefonoPasajero.getText().toString());

                return fG.functionPostRequest("ausuario",postDataParams);
            }
            catch(Exception e){
                Log.e("Exception",e.toString());
                return null;
            }

        }

        //Muestra en pantalla el resultado con un mensaje Toast
        @Override
        protected void onPostExecute(ArrayList result) {
            int responseCode=(Integer)result.get(0);
            if(responseCode==HttpsURLConnection.HTTP_OK) {
                Toast.makeText(getApplicationContext(), "El usuario ha sido registrado con éxito",Toast.LENGTH_SHORT).show();
            }
            else if(responseCode==HttpsURLConnection.HTTP_BAD_REQUEST){
                Toast.makeText(getApplicationContext(), "El usuario que intenta registrar ya está en uso",Toast.LENGTH_SHORT).show();
            }
            else if (responseCode==HttpsURLConnection.HTTP_FORBIDDEN){
                Toast.makeText(getApplicationContext(), "El usuario no pudo registrarse, revise los datos ingresados o contacte al administrador del sistema",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Ocurrió un problema al procesar la solicitud, inténtelo más tarde",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
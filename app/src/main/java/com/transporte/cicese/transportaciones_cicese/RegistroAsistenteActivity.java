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


public class RegistroAsistenteActivity extends AppCompatActivity {

    private EditText usuarioAsistente;
    private EditText contrasenaAsistente;
    private EditText nombreAsistente;
    private EditText aPaternoAsistente;
    private EditText aMaternoAsistente;
    private EditText numTelefonoAsistente;
    private Button registraAsistente;

    String usuario, contrasena, telefono, nombre, aPaterno, aMaterno, resource;

    private Button popupD;
    private String dialogoMsg;

    funcionesGeneradoras fG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_asistente);

        fG = new funcionesGeneradoras(getApplicationContext());
        //Referencia a los controles de la interfaz
        usuarioAsistente = (EditText) findViewById(R.id.usuario_generadoA);
        contrasenaAsistente = (EditText) findViewById(R.id.contrasena_asistente);
        nombreAsistente = (EditText) findViewById(R.id.nombre_asistente);
        aPaternoAsistente = (EditText) findViewById(R.id.ap_asistente);
        aMaternoAsistente = (EditText) findViewById(R.id.am_asistente);
        numTelefonoAsistente = (EditText) findViewById(R.id.tel_asistente);
        registraAsistente = (Button) findViewById(R.id.registroAsistente_btn);


        popupD = (Button) findViewById(R.id.popup2);
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
        registraAsistente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Datos capturados en el formulario
                usuario = usuarioAsistente.getText().toString();
                contrasena = contrasenaAsistente.getText().toString();
                nombre = nombreAsistente.getText().toString();
                aPaterno = aPaternoAsistente.getText().toString();
                aMaterno = aMaternoAsistente.getText().toString();
                telefono = numTelefonoAsistente.getText().toString();


                new RegistroAsistenteActivity.SendPostRequest().execute();
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

        protected void onPreExecute(){}

        protected ArrayList doInBackground(String... arg0) {

            try {
                resource="ausuario";
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("tipo", "a");
                postDataParams.put("usuario", usuario);
                postDataParams.put("contrasena", contrasena);
                postDataParams.put("nombre", nombre);
                postDataParams.put("apellido_paterno", aPaterno);
                postDataParams.put("apellido_materno", aMaterno);
                postDataParams.put("numero_telefono", telefono);
                return fG.functionPostRequest(resource,postDataParams);
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

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
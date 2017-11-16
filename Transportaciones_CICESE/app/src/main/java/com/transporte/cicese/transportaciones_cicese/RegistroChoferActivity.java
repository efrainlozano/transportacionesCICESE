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

public class RegistroChoferActivity extends AppCompatActivity {


    private EditText numeroEmpleadoChofer;
    private EditText contrasenaChofer;
    private EditText nombreChofer;
    private EditText aPaternoChofer;
    private EditText aMaternoChofer;
    private EditText numTelefonoChofer;
    private Button registraChofer;


    private Button popupD;
    private String dialogoMsg;
    funcionesGeneradoras fG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_chofer);

        //Referencia a los controles de la interfaz
        numeroEmpleadoChofer = (EditText) findViewById(R.id.numeroEmpleado);
        contrasenaChofer = (EditText) findViewById(R.id.contrasenaChofer);
        nombreChofer = (EditText) findViewById(R.id.nombreChofer);
        aPaternoChofer = (EditText) findViewById(R.id.aPaternoChofer);
        aMaternoChofer = (EditText) findViewById(R.id.aMaternoChofer);
        numTelefonoChofer = (EditText) findViewById(R.id.telefonoChofer);
        registraChofer = (Button) findViewById(R.id.registroChofer_btn);

        popupD = (Button) findViewById(R.id.popup);
        dialogoMsg = "Este campo requiere un número de 10 dígitos.\nEl formato es el siguiente:" +
                "\n##########\ndonde los tres primeros números son la lada, éstos no se colocan entre \nparéntesis";

        //Si se presiona el icono de exclamacion en el campo de telefono
        popupD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUp();
            }
        });

        //Preparamos el metodo para registrar al chofer
        registraChofer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Datos capturados en el formulario
                new RegistroChoferActivity.SendPostRequest().execute();
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
                    postDataParams.put("tipo", "c");
                    postDataParams.put("usuario", numeroEmpleadoChofer.getText().toString());
                    postDataParams.put("contrasena", contrasenaChofer.getText().toString());
                    postDataParams.put("nombre", nombreChofer.getText().toString());
                    postDataParams.put("apellido_paterno", aPaternoChofer.getText().toString());
                    postDataParams.put("apellido_materno", aMaternoChofer.getText().toString());
                    postDataParams.put("numero_telefono", numTelefonoChofer.getText().toString());

                return fG.functionPostRequest("ausuario", postDataParams);
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
                Toast.makeText(getApplicationContext(), "El número de empleado que intenta registrar ya está en uso",Toast.LENGTH_SHORT).show();
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
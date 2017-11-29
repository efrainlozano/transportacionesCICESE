package com.transporte.cicese.transportaciones_cicese;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import java.util.regex.Pattern;

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
    private EditText correoElectronicoChofer;
    private Button registraChofer;


    private Button popupT,popupE;
    private String dialogoMsg,dialogoMsg2;
    private ProgressDialog progressDialog;

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
        correoElectronicoChofer = (EditText) findViewById(R.id.emailChofer);
        registraChofer = (Button) findViewById(R.id.registroChofer_btn);

       popupT = (Button) findViewById(R.id.popupChoferT);
       popupE = (Button) findViewById(R.id.popupChoferC);

       dialogoMsg = getString (R.string.popup_msg);
       dialogoMsg2 = getString (R.string.popup_msg2);


        //Si se presiona el icono de exclamacion en el campo de telefono
        popupT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpC();
            }
        });

        //Si se presiona el icono de exclamacion en el campo de telefono
        popupE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpE();
            }
        });

        //Preparamos el metodo para registrar al chofer
        registraChofer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Datos capturados en el formulario
                Boolean validaChofer = false;
                if(numeroEmpleadoChofer.getText().toString().length()==0){
                    numeroEmpleadoChofer.setError("El campo es requerido");
                    validaChofer = true;
                }
                if(contrasenaChofer.getText().toString().length()==0){
                    contrasenaChofer.setError("El campo es requerido");
                    validaChofer = true;
                }
                if(nombreChofer.getText().toString().length()==0){
                    nombreChofer.setError("El campo es requerido");
                    validaChofer = true;
                }
                if(aMaternoChofer.getText().toString().length()==0){
                    aMaternoChofer.setError("El campo es requerido");
                    validaChofer = true;
                }
                if(numTelefonoChofer.getText().toString().length()==0){
                    numTelefonoChofer.setError("El campo es requerido");
                    validaChofer = true;
                }
                if(numTelefonoChofer.getText().toString().length()!=10
                        && numTelefonoChofer.getText().toString().length()!=0){
                    numTelefonoChofer.setError("Debe capturar un número de 10 dígitos");
                    validaChofer = true;
                }
                if(correoElectronicoChofer.getText().toString().length()==0){
                    correoElectronicoChofer.setError("El campo es requerido");
                    validaChofer = true;
                }
                if (!validarEmail(correoElectronicoChofer.getText().toString())){
                    correoElectronicoChofer.setError("Email no válido");
                    validaChofer = true;
                }
                if(validaChofer == false){
                    new RegistroChoferActivity.SendPostRequest().execute();
                    showProgress();
                }
            }
        });

    }

    private void showProgress() {
        progressDialog = new ProgressDialog(RegistroChoferActivity.this);
        progressDialog.setMessage("Cargando..."); // Setting Message
        progressDialog.setTitle("Por favor espere"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(15000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }).start();
    }

    //Validar correo electronico
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private void limpiarDatos() {
        numeroEmpleadoChofer.setText("");
        contrasenaChofer.setText("");
        nombreChofer.setText("");
        aPaternoChofer.setText("");
        aMaternoChofer.setText("");
        numTelefonoChofer.setText("");
        correoElectronicoChofer.setText("");
    }

    private void PopUpC() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(dialogoMsg)
                .setTitle(getString (R.string.tel_fono))
                .setCancelable(false)
                .setNeutralButton((getString (R.string.aceptar)),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void PopUpE() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(dialogoMsg2)
                .setTitle(getString (R.string.email))
                .setCancelable(false)
                .setNeutralButton((getString (R.string.aceptar)),
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
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "El usuario ha sido registrado con éxito",Toast.LENGTH_SHORT).show();
                limpiarDatos();
            }
            else if(responseCode==HttpsURLConnection.HTTP_BAD_REQUEST){
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "El número de empleado que intenta registrar ya está en uso",Toast.LENGTH_SHORT).show();
            }
            else if (responseCode==HttpsURLConnection.HTTP_FORBIDDEN){
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "El usuario no pudo registrarse, revise los datos ingresados o contacte al administrador del sistema",Toast.LENGTH_SHORT).show();
            }
            else{
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Ocurrió un problema al procesar la solicitud, inténtelo más tarde",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
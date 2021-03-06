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

import com.transporte.cicese.transportaciones_cicese.funciones.funcionesGeneradoras;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Blanca Cecilia De Leon Rubio on 12/10/2017.
 */


public class RegistroAsistenteActivity extends AppCompatActivity {

    private EditText usuarioAsistente;
    private EditText contrasenaAsistente,confirmaClave;
    private EditText nombreAsistente;
    private EditText aPaternoAsistente;
    private EditText aMaternoAsistente;
    private EditText numTelefonoAsistente;
    private Button registraAsistente;
    private EditText correoElectronicoAsistente;

    String usuario, contrasena, telefono, nombre, aPaterno, aMaterno, resource, correo;

    private Button popupTel, popupCor;
    private String dialogoMsg,dialogoMsg2;
    private ProgressDialog progressDialog;

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
        correoElectronicoAsistente = (EditText) findViewById(R.id.emailAsistente);
        confirmaClave=(EditText)findViewById(R.id.confirmaClaveAsistente);


       popupTel = (Button) findViewById(R.id.popupAsisT);
       popupCor = (Button) findViewById(R.id.popupAsisC);
       dialogoMsg = getString (R.string.popup_msg);
       dialogoMsg2 = getString (R.string.popup_msg2);

        //Si se presiona el icono de exclamacion en el campo de telefono
    popupTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpT();
            }
        });

        //Si se presiona el icono de exclamacion en el campo de telefono
        popupCor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpC();
            }
        });


        //Preparamos el metodo para registrar al pasajero
        registraAsistente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean validaCampos = false;
                //Datos capturados en el formulario
                if(usuarioAsistente.getText().toString().length()==0){
                    usuarioAsistente.setError("El campo es requerido" );
                    validaCampos = true;
                }
                if(contrasenaAsistente.getText().toString().length()==0){
                    contrasenaAsistente.setError("El campo es requerido");
                    validaCampos = true;
                }
                if(contrasenaAsistente.getText().toString().length()<8){
                    contrasenaAsistente.setError("Debe capturar una contraseña de al menos 8 caracteres");
                    validaCampos = true;
                }
                if(!contrasenaAsistente.getText().toString().equals(confirmaClave.getText().toString())){
                    confirmaClave.setError("Las contraseñas no coinciden");
                    validaCampos = true;
                }
                if(nombreAsistente.getText().toString().length()==0){
                    nombreAsistente.setError("El campo es requerido");
                    validaCampos = true;
                }
                if(aMaternoAsistente.getText().toString().length()==0){
                    aMaternoAsistente.setError("El campo es requerido");
                    validaCampos = true;
                }
                if(numTelefonoAsistente.getText().toString().length()==0){
                    numTelefonoAsistente.setError("El campo es requerido");
                    validaCampos = true;
                }
                if(numTelefonoAsistente.getText().toString().length()!=10
                        && numTelefonoAsistente.getText().toString().length()!=0){
                    numTelefonoAsistente.setError("Debe capturar un número de 10 dígitos");
                    validaCampos = true;
                }
                if(correoElectronicoAsistente.getText().toString().length()==0){
                    correoElectronicoAsistente.setError("El campo es requerido");
                    validaCampos = true;
                }
                if (!validarEmail(correoElectronicoAsistente.getText().toString())){
                    correoElectronicoAsistente.setError("Email no válido");
                    validaCampos = true;
                }
                if(validaCampos==false){
                    new registrarAsistente().execute();
                    showProgress();
                }
            }
        });

    }

    private void showProgress() {
        progressDialog = new ProgressDialog(RegistroAsistenteActivity.this);
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
        usuarioAsistente.setText("");
        contrasenaAsistente.setText("");
        nombreAsistente.setText("");
        aMaternoAsistente.setText("");
        aPaternoAsistente.setText("");
        numTelefonoAsistente.setText("");
        correoElectronicoAsistente.setText("");
    }

   private void PopUpT() {
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

    private void PopUpC() {
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
    public class registrarAsistente extends AsyncTask<String, Void, ArrayList> {
        //DESCRIPCION: ASYNKTASK para registrar en bd los datos de un nuevo asistente
        protected ArrayList doInBackground(String... arg0) {

            try {
                resource="ausuario";
                JSONObject postDataParams = new JSONObject();//JSON que contendra los parametros post a enviar
                postDataParams.put("tipo", "a");
                postDataParams.put("usuario", usuarioAsistente.getText().toString());
                postDataParams.put("contrasena", contrasenaAsistente.getText().toString());
                postDataParams.put("nombre", nombreAsistente.getText().toString());
                postDataParams.put("apellido_paterno", aPaternoAsistente.getText().toString());
                postDataParams.put("apellido_materno", aMaternoAsistente.getText().toString());
                postDataParams.put("numero_telefono", numTelefonoAsistente.getText().toString());
                return fG.functionPostRequest(resource,postDataParams);
            }
            catch(Exception e){
                Log.e("Exception",e.toString());
                return null;
            }
        }
        @Override
        protected void onPostExecute(ArrayList result) {
            int responseCode=(Integer)result.get(0);
            if(responseCode==HttpsURLConnection.HTTP_OK) {
                progressDialog.cancel();
                setConfirmation();
                limpiarDatos();
            }
            else if(responseCode==HttpsURLConnection.HTTP_BAD_REQUEST){
                progressDialog.cancel();
                error("El usuario que intenta registrar ya está en uso");
            }
            else if (responseCode==HttpsURLConnection.HTTP_FORBIDDEN){
                progressDialog.cancel();
                error("El usuario no pudo registrarse, revise los datos ingresados o contacte al administrador del sistema");
            }
            else{
                progressDialog.cancel();
                error("Ocurrió un problema al procesar la solicitud, inténtelo más tarde");
            }
        }
    }
    public void setConfirmation() {
        AlertDialog.Builder alert = new AlertDialog.Builder(RegistroAsistenteActivity.this);
        alert.setTitle("Desea continuar en esta ventana?");
        alert.setMessage("Su registro fue echo con exito, ¿Desea continuar registrando otros usuarios? o ¿Desea salir de esta ventana?");
        alert.setPositiveButton("Seguir aqui", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alert.setNegativeButton("Salir", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        alert.show();
    }
    public void error(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(RegistroAsistenteActivity.this);
        alert.setTitle("Ocurrio un error");
        alert.setMessage(message);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alert.show();
    }
}
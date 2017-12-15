package com.transporte.cicese.transportaciones_cicese;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.transporte.cicese.transportaciones_cicese.funciones.funcionesGeneradoras;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends AppCompatActivity {

    ArrayList fields,values;
    private Button inicio;
    String usuario, contrasena,resource;
    EditText usuarioEdit, contrasenaEdit;
    Button iniciar;
    char tipoUsuario;
    ImageView verClave;
    int input;

    String token;
    funcionesGeneradoras fG;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        trustAllCertificates();//Validamos todos los certificados por servidor de pruebas

        //Referencia a los controles de la interfaz
        usuarioEdit = (EditText) findViewById(R.id.usuario_editText);
        contrasenaEdit = (EditText) findViewById(R.id.contrasena_editText);
        iniciar = (Button) findViewById(R.id.iniciar_button);
        verClave=(ImageView)findViewById(R.id.verClave);
        final RadioButton radioAsistente = (RadioButton) findViewById(R.id.radio_asistente);
        final RadioButton radioChofer = (RadioButton) findViewById(R.id.radio_chofer);
        final RadioButton radioPasajero = (RadioButton) findViewById(R.id.radio_pasajero);

        //    ((EditText) findViewById(R.id.usuario_editText)).setHint("Número de empleado");

        verClave.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        input=contrasenaEdit.getInputType();
                        contrasenaEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                        return true;
                    case MotionEvent.ACTION_UP:
                        contrasenaEdit.setInputType(input);
                        return true;
                }
                return false;
            }
        });

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario = usuarioEdit.getText().toString();
                contrasena = contrasenaEdit.getText().toString();
                if(radioChofer.isChecked()==true){
                    tipoUsuario = 'c';
                }else if(radioAsistente.isChecked()==true){
                    tipoUsuario = 'a';
                }else if(radioPasajero.isChecked()==true){
                    tipoUsuario = 'p';
                }
                String token = FirebaseInstanceId.getInstance().getToken();
                iniciar.setEnabled(false);
                fields= new ArrayList(Arrays.asList("tipo","usuario","contrasena","token"));
                values= new ArrayList(Arrays.asList(tipoUsuario,usuario,contrasena,token));

                if(usuario.length()==0){
                    usuarioEdit.setError("El campo es requerido" );
                    iniciar.setEnabled(true);
                }
                if(contrasena.length()==0){
                    contrasenaEdit.setError("El campo es requerido" );
                    iniciar.setEnabled(true);
                }
                if(usuario.length()!=0&&contrasena.length()!=0){
                    new LoginActivity.ConsultarDatos().execute();
                    showProgress();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(LoginActivity.this);
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


    private class ConsultarDatos extends AsyncTask<String, Void, ArrayList> {
        // Do some validation here
        protected ArrayList doInBackground(String... urls) {
            fG= new funcionesGeneradoras(getApplicationContext());
            return fG.functionGetRequest("glogin", fields, values);
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            if ((Integer) result.get(0) == HttpsURLConnection.HTTP_OK) {//Integer.parseInt(result)==200 ---> ERROR
                Intent i = null;
                int idUsuario = 0;
                String jResult = result.get(1).toString().substring(1,result.get(1).toString().length()-1);
                try {
                    JSONObject jObject = new JSONObject(jResult);
                    switch (tipoUsuario) {
                        case 'c': //Inicio de chofer
                            idUsuario = jObject.getInt("id_chofer");
                            i = new Intent(LoginActivity.this, InicioChoferesActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            break;
                        case 'a': //Inicio de asistente
                            idUsuario = jObject.getInt("id_asistente");
                            i = new Intent(LoginActivity.this, InicioAsistentesActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            break;
                        case 'p': //Inicio de pasajero
                            idUsuario = jObject.getInt("id_invitado");
                            i = new Intent(LoginActivity.this, InicioPasajeroActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            break;

                    }
                    SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();

                    editor.putString("nUsuario", usuario);
                    editor.putInt("idUsuario", idUsuario);
                    editor.putString("idSolicitudes",jObject.getString("solicitudes"));
                    editor.putString("tipoUsuario",String.valueOf(tipoUsuario));
                    editor.commit();//Guardamos los datos del usuario que nos seran utiles mas adelante

                    i.putExtra("usuario", usuario);//Guardamos una cadena
                    //progressDialog.cancel();
                    startActivity(i);
                    limpiarDatos();
                    iniciar.setEnabled(true);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Usuario o contraseña inválidos",
                        Toast.LENGTH_SHORT).show();
                iniciar.setEnabled(true);
            }
        }
    }

    private void limpiarDatos() {
        usuarioEdit.setText("");
        contrasenaEdit.setText("");
    }

    public void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
    }
}

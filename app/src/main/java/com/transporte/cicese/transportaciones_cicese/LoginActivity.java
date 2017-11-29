package com.transporte.cicese.transportaciones_cicese;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.transporte.cicese.transportaciones_cicese.funciones.funcionesGeneradoras;

import org.json.JSONException;
import org.json.JSONObject;

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

    funcionesGeneradoras fG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        trustAllCertificates();//Validamos todos los certificados por servidor de pruebas

        //Referencia a los controles de la interfaz
        usuarioEdit = (EditText) findViewById(R.id.usuario_editText);
        contrasenaEdit = (EditText) findViewById(R.id.contrasena_editText);
        iniciar = (Button) findViewById(R.id.iniciar_button);
        final RadioButton radioAsistente = (RadioButton) findViewById(R.id.radio_asistente);
        final RadioButton radioChofer = (RadioButton) findViewById(R.id.radio_chofer);
        final RadioButton radioPasajero = (RadioButton) findViewById(R.id.radio_pasajero);

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

                iniciar.setEnabled(false);
                fields= new ArrayList(Arrays.asList("tipo","usuario","contrasena"));
                values= new ArrayList(Arrays.asList(tipoUsuario,usuario,contrasena));
                new LoginActivity.ConsultarDatos().execute();
            }
        });
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
                            i = new Intent(LoginActivity.this, InicioChoferesActivity.class);
                            break;
                        case 'a': //Inicio de asistente
                            idUsuario = jObject.getInt("id_asistente");
                            i = new Intent(LoginActivity.this, InicioAsistentesActivity.class);
                            break;
                        case 'p': //Inicio de pasajero
                            idUsuario = jObject.getInt("id_invitado");
                            i = new Intent(LoginActivity.this, InicioPasajeroActivity.class);
                            break;

                    }
                    SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("nUsuario", usuario);
                    editor.putInt("idUsuario", idUsuario);
                    editor.putString("tipoUsuario",String.valueOf(tipoUsuario));
                    Log.i("id", String.valueOf(idUsuario));
                    editor.commit();//Guardamos los datos del usuario que nos seran utiles mas adelante

                    i.putExtra("usuario", usuario);//Guardamos una cadena
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Usuario o contraseña inválidos",
                        Toast.LENGTH_SHORT).show();
            }
        }
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

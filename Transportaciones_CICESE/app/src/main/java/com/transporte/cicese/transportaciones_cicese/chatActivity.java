package com.transporte.cicese.transportaciones_cicese;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;


import com.google.firebase.iid.FirebaseInstanceId;
import com.transporte.cicese.transportaciones_cicese.adapters.customSpinnerAdapter;
import com.transporte.cicese.transportaciones_cicese.funciones.funcionesGeneradoras;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class chatActivity extends AppCompatActivity {

    private LinearLayout layout;
    private EditText messageArea;
    private String mensaje, miNombre,fecha,hora,resource;
    private int miId, idServicio, idSolicitud;
    private ImageView sendButton,notificacionesRapidas;
    private ScrollView scrollView;
    private String title = "",tipoUsuario;
    private ArrayList fields,values;
    private Spinner serviciosSpinner,solicitudesSpinner;
    private JSONArray serviciosResult,solicitudesResult;


  /* String recuperamos_variable_string;// = getIntent().getExtras().getString("textoR_1").toString();
   Boolean bool ;//= getIntent().getExtras().getBoolean("selecto");
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        serviciosSpinner = (Spinner) findViewById(R.id.serviciosSpinner);
        solicitudesSpinner = (Spinner) findViewById(R.id.solicitudesSpinner);

        //Del texto rapido
        /*Bundle extras = getIntent().getExtras();

        if (extras != null) {
            bool = getIntent().getExtras().getBoolean("selecto");      }
*/
        title="Chat";
        getSupportActionBar().setTitle(title);
        SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
        sendButton              = (ImageView)findViewById(R.id.sendButton);
        notificacionesRapidas   = (ImageView)findViewById(R.id.notificacionesRapidas);
        messageArea             = (EditText)findViewById(R.id.messageArea);
        layout                  = (LinearLayout) findViewById(R.id.layout1);
        scrollView              = (ScrollView)findViewById(R.id.scrollView);
        miId                    =  settings.getInt("idUsuario",0); //AQUI DEBERA DE IR AL SHAREDPREFERENCE DEL ID DEL USUARIO LOGUEADO
        miNombre                =  settings.getString("nUsuario","Default");
        tipoUsuario             =  settings.getString("tipoUsuario","Default");
        sendButton.setEnabled(false);
        switch (tipoUsuario) {
            case "p":
                String solicitud = settings.getString("idSolicitudes", "Default")
                        .replace("[", "").replace("]","");
                if(!solicitud.equals("Default")&&(solicitud!="")&&(solicitud!=null)){
                idSolicitud=Integer.parseInt(solicitud);
                   serviciosSpinner.setEnabled(true);
                   serviciosSpinner.setVisibility(View.VISIBLE);
                   new chatActivity.obtenerServicios().execute();
               }
                break;
            case "c":
                notificacionesRapidas.getLayoutParams().width=50;
                notificacionesRapidas.setVisibility(View.VISIBLE);
                notificacionesRapidas.setEnabled(true);
                solicitudesSpinner.setVisibility(View.VISIBLE);
                new chatActivity.obtenerSolicitudes().execute();
                break;
            default:
                solicitudesSpinner.setVisibility(View.VISIBLE);
                new chatActivity.obtenerSolicitudes().execute();
                break;
        }
        notificacionesRapidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarNotificacionesRapidas();
            }
        });
        //trustAllCertificates();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if(bool==true) {
                    Toast.makeText(getApplicationContext(),"Se ha recibido el dato",Toast.LENGTH_SHORT).show();
                    recuperamos_variable_string = getIntent().getExtras().getString("textoR_1");
                    messageArea.setText(recuperamos_variable_string);
                }else{*/
                mensaje = messageArea.getText().toString();//}
                new chatActivity.SendPostRequest().execute(getString(R.string.URI)+"/amensaje");
                messageArea.setText(null);
            }
        });

        solicitudesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                messageArea.setEnabled(false);
                if (position != 0) {
                    JSONObject jObject = null;
                    try {
                        serviciosSpinner.setEnabled(true);
                        jObject = new JSONObject(solicitudesResult.get(position - 1).toString());
                        serviciosSpinner.setAdapter(null);
                        idSolicitud=jObject.getInt("id_solicitud");
                        new chatActivity.obtenerServicios().execute(String.valueOf(idSolicitud));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    notificacionesRapidas.setEnabled(false);
                    serviciosSpinner.setVisibility(View.INVISIBLE);
                    messageArea.setEnabled(false);
                    layout.removeAllViews();

                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        messageArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0)
                    sendButton.setEnabled(true);
                else
                    sendButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        serviciosSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(serviciosResult.get(i-1).toString());
                        //idServicio = jObject.getInt("id_servicio");
                        idServicio=jObject.getInt("id_servicio");
                        layout.removeAllViews();
                        messageArea.setEnabled(true);
                        notificacionesRapidas.setEnabled(true);
                        new chatActivity.ConsultarDatos().execute();
                    } catch (JSONException e) {
                        messageArea.setEnabled(false);
                        e.printStackTrace();
                    }
                }
                else {
                    notificacionesRapidas.setEnabled(false);
                    messageArea.setEnabled(false);
                    layout.removeAllViews();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public class SendPostRequest extends AsyncTask<String, Void, ArrayList> {

        protected void onPreExecute(){}

        protected ArrayList doInBackground(String... urls) {
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            funcionesGeneradoras fG= new funcionesGeneradoras(getApplication());
            try {
                fecha = today.year+"-"+(today.month+1)+"-"+today.monthDay;
                hora=today.hour+":"+today.minute;
                JSONObject postDataParams = new JSONObject();
                //JSONObject jObject = new JSONObject(solicitudesResult.get(solicitudesSpinner.getSelectedItemPosition()-1).toString());
                postDataParams.put("idsolicitud",idSolicitud);
                postDataParams.put("tipo",tipoUsuario);
                postDataParams.put("idservicio", idServicio);
                postDataParams.put("mensaje", mensaje);
                postDataParams.put("remitente", miNombre);
                postDataParams.put("idrem", miId);
                postDataParams.put("hora", hora);
                postDataParams.put("fecha", fecha);

                return fG.functionPostRequest("amensaje",postDataParams);
            }
            catch(Exception e){
                return new ArrayList(Arrays.asList(0));
            }

        }

        @Override
        protected void onPostExecute(ArrayList result) {
            if((Integer)result.get(0)==HttpsURLConnection.HTTP_OK) {
                Toast.makeText(getApplicationContext(), "Recuerda recargar para ver nuevos mensajes entrantes", Toast.LENGTH_SHORT).show();
                addMessageBox("Tu:\n" + mensaje + "\n" + fecha + " - " + hora, 1);
            }
            Log.i("r",result.get(0).toString());
        }
    }
    public class enviarNotificacion extends AsyncTask<String, Void, ArrayList> {

        protected void onPreExecute(){}

        protected ArrayList doInBackground(String... mensaje) {
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            funcionesGeneradoras fG= new funcionesGeneradoras(getApplication());
            try {
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("idsolicitud",idSolicitud);
                postDataParams.put("mensaje",mensaje[0]);
                return fG.functionPostRequest("notificacionrapida",postDataParams);
            }
            catch(Exception e){
                return new ArrayList(Arrays.asList(0));
            }

        }

        @Override
        protected void onPostExecute(ArrayList result) {
        }
    }

    private class ConsultarDatos extends AsyncTask<String, Void, ArrayList> {
        // Do some validation here
        protected ArrayList doInBackground(String... urls) {
            funcionesGeneradoras fG= new funcionesGeneradoras(getApplication());
            fields=new ArrayList(Arrays.asList("id"));
            values=new ArrayList(Arrays.asList(idServicio));
            resource ="gmensajes";
            return fG.functionGetRequest(resource,fields,values);
        }
        @Override
        protected void onPostExecute(ArrayList response) {
            int responseCode = (Integer) response.get(0);

            if (responseCode==HttpsURLConnection.HTTP_OK) {
                String result = response.get(1).toString();
                result = result.substring(2, result.length() - 5);
                result = result + "}";
                result = result.replaceAll("\\\\", "");
                result = result.substring(0, 14) + "[" + result.substring(16, result.length());
                try {
                    JSONObject jObject = new JSONObject(result);
                    JSONArray jArray = jObject.getJSONArray("mensajeria");
                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        String mensaje = oneObject.getString("mensaje");
                        String userName = oneObject.getString("remitente");
                        String fecha = oneObject.getString("fecha");
                        String hora = oneObject.getString("hora");
                        String idMensaje = oneObject.getString("id");

                        if (Integer.parseInt(idMensaje) == miId) {
                            addMessageBox("Tu:\n" + mensaje + "\n" + fecha + " - " + hora, 1);
                        } else {
                            addMessageBox(userName + ":\n" + mensaje + "\n" + fecha + " - " + hora, 2);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"No tienes mensajes en este servicio",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(chatActivity.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private class obtenerServicios extends AsyncTask<String, Void, ArrayList> {
        // Do some validation here

        protected ArrayList doInBackground(String... urls) {
            // Do some validation here
            funcionesGeneradoras fG= new funcionesGeneradoras(getApplication());
            SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
            String tipoUsuario = settings.getString("tipoUsuario", "Default");
            //List<String> myList = new ArrayList<String>(Arrays.asList(arr.split(",")));
            ArrayList fields = new ArrayList(Arrays.asList("id"));
            ArrayList result = new ArrayList();
            ArrayList values;
            switch (tipoUsuario) {
                case "p":

                    values = new ArrayList(Arrays.asList(idSolicitud));
                    result = fG.functionGetRequest("gservicios", fields, values);
                    break;
                default:
                    values = new ArrayList(Arrays.asList(urls[0]));
                    result = fG.functionGetRequest("gservicios", fields, values);
                    break;

            }
            return result;
            //Funcion que regresa JSON con todos los datos de los pasajeros/invitados
        }

        protected void onPostExecute(ArrayList result) {
            if ((Integer) result.get(0) == HttpURLConnection.HTTP_OK) {
                String[] serviciosArray;
                customSpinnerAdapter spinnerAdapterServicios;
                String data = result.get(1).toString();
                try {
                    serviciosResult = new JSONArray(data);
                    serviciosArray = new String[serviciosResult.length() + 1];
                    serviciosArray[0] = "Seleccione un servicio de la lista";
                    for (int i = 0; i < serviciosResult.length(); i++) {
                        JSONObject oneObject = new JSONObject(serviciosResult.get(i).toString());
                        serviciosArray[i + 1] = oneObject.getString("fecha_encuentro") +
                                " - " + oneObject.getString("descripcion_lugar_encuentro").substring(0,14)+"..." +
                                " -> " + oneObject.getString("descripcion_lugar_destino").substring(0,14)+"...";
                    }
                    spinnerAdapterServicios = new customSpinnerAdapter(chatActivity.this, serviciosArray, null);
                    serviciosSpinner.setAdapter(spinnerAdapterServicios);
                    serviciosSpinner.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                serviciosSpinner.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "No hay servicios regitrados a esta solicitud", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private class obtenerSolicitudes extends AsyncTask<String, Void, ArrayList> {
        // Do some validation here

        protected ArrayList doInBackground(String... urls) {
            // Do some validation here
            funcionesGeneradoras fG= new funcionesGeneradoras(getApplication());
            SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
            String tipoUsuario = settings.getString("tipoUsuario", "Default");
            String arr = settings.getString("idSolicitudes", "Default").replace("[", "")
                    .replace("]", "").replace(" ", "");
            //List<String> myList = new ArrayList<String>(Arrays.asList(arr.split(",")));
            ArrayList fields = new ArrayList(Arrays.asList("idSolicitudes"));
            ArrayList values = new ArrayList(Arrays.asList(/*myList.get(0)*/arr));
            return fG.functionGetRequest("gdatasolicituds", fields, values);


            //Funcion que regresa JSON con todos los datos de los pasajeros/invitados
        }

        protected void onPostExecute(ArrayList result) {
            if ((Integer) result.get(0) == HttpURLConnection.HTTP_OK) {
                String[] solicitudesArray;
                customSpinnerAdapter spinnerAdapterServicios;
                String data = result.get(1).toString();
                try {
                    solicitudesResult = new JSONArray(data);
                    solicitudesArray = new String[solicitudesResult.length() + 1];
                    solicitudesArray[0] = "Seleccione una solicitud de la lista";
                    for (int i = 0; i < solicitudesResult.length(); i++) {
                        JSONObject oneObject = new JSONObject(solicitudesResult.get(i).toString());
                        solicitudesArray[i + 1] =
                                oneObject.getString("id_folio") + " - " +
                                        oneObject.getString("nombre_pasajero");
                    }
                    spinnerAdapterServicios = new customSpinnerAdapter
                            ( chatActivity.this, solicitudesArray, null);
                    solicitudesSpinner.setAdapter(spinnerAdapterServicios);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "No hay servicios regitrados a esta solicitud",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void mostrarNotificacionesRapidas() {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final Button cincoMinutos = new Button(this);
        final Button diezMinutos = new Button(this);
        final Button imprevisto = new Button(this);
        final Button enCamino = new Button(this);
        final Button teLlamare = new Button(this);
        cincoMinutos.setText("Llego en 5 minutos");
        diezMinutos.setText("Llego en 10 minutos");
        imprevisto.setText("Tuve un imprevisto");
        enCamino.setText("Me encuentro en camino");
        teLlamare.setText("Le llamaré en seguida");
        cincoMinutos.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        diezMinutos.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        imprevisto.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        enCamino.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        teLlamare.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout parent = new LinearLayout(this);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setGravity(Gravity.CENTER);
        parent.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        parent.addView(cincoMinutos);
        parent.addView(diezMinutos);
        parent.addView(imprevisto);
        parent.addView(enCamino);
        parent.addView(teLlamare);

        // popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("Notificaciones Rapidas");
        popDialog.setView(parent);
        cincoMinutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new chatActivity.enviarNotificacion().execute("Su chofer llegara en aproximadamente cinco minutos");
            }
        });
        diezMinutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new chatActivity.enviarNotificacion().execute("Su chofer llegara en aproximadamente diez minutos");
            }
        });
        imprevisto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new chatActivity.enviarNotificacion().execute("Su chofer tuvo un imprevisto y se retraso unos minutos");
            }
        });
        enCamino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new chatActivity.enviarNotificacion().execute("Su chofer se encuentra en camino a recogerlo en este momento");
            }
        });
        teLlamare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new chatActivity.enviarNotificacion().execute("Su chofer le llamara en unos momentos");
            }
        });
        // Button OK
        popDialog/*.setPositiveButton("Enviar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int rat=Integer.valueOf(String.valueOf(rating.getRating()).charAt(0));
                        if(rat!=0)
                            new ServiciosActivity.sendCalificacion().execute(currentServ, String.valueOf(rat));
                        else
                            Toast.makeText(getApplicationContext(),"La calificacion minima debe ser 1",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })*/.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        popDialog.create();
        popDialog.show();
    }
}


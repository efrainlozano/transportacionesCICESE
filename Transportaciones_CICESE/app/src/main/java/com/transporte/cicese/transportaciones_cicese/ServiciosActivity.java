package com.transporte.cicese.transportaciones_cicese;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.transporte.cicese.transportaciones_cicese.adapters.customSpinnerAdapter;
import com.transporte.cicese.transportaciones_cicese.funciones.funcionesGeneradoras;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by EFRA.LM on 16/10/2017.
 */

public class ServiciosActivity extends AppCompatActivity {
    String idSolicitud, title, tipoUsuario;
    int idUsuario,idServicio;
    TextView  desc_enc, long_enc, lat_enc, hor_enc, fec_enc,
            desc_dest, long_dest, lat_dest, model_ve, mar_ve,an_ve,col_ve,
            pla_ve,tip_ve,id_chofer,est_serv,cal_serv,
            fum_serv;
    Spinner spinnerServicios;
    Button consulta;
    funcionesGeneradoras fG;

    JSONArray serviciosResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        fG = new funcionesGeneradoras(getApplicationContext());

        title="Servicios";
        getSupportActionBar().setTitle(title);
        SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
        idUsuario=settings.getInt("idUsuario",0);
        idSolicitud=settings.getString("solicitudPasajero","Default");
        tipoUsuario= settings.getString("tipoUsuario","Default");

        consulta=(Button)findViewById(R.id.buttonBuscar);
        spinnerServicios = (Spinner) findViewById(R.id.buscarServicios);
        fum_serv = (TextView) findViewById(R.id.fum_servicio);
        est_serv = (TextView) findViewById(R.id.est_servicio);
        cal_serv = (TextView) findViewById(R.id.calif_servicio);
        desc_enc = (TextView) findViewById(R.id.descripcion_lugar_encuentro);
        long_enc = (TextView) findViewById(R.id.long_encuentro);
        lat_enc = (TextView) findViewById(R.id.lat_encuentro);
        hor_enc = (TextView) findViewById(R.id.hor_encuentro);
        fec_enc = (TextView) findViewById(R.id.fec_encuentro);
        desc_dest = (TextView) findViewById(R.id.desc_destino);
        long_dest = (TextView) findViewById(R.id.long_destino);
        lat_dest = (TextView) findViewById(R.id.lat_destino);
        model_ve = (TextView) findViewById(R.id.model_vehiculo);
        mar_ve = (TextView) findViewById(R.id.marc_vehiculo);
        an_ve = (TextView) findViewById(R.id.an_vehiculo);
        col_ve = (TextView) findViewById(R.id.col_vehiculo);
        pla_ve = (TextView) findViewById(R.id.pla_vehiculo);
        tip_ve = (TextView) findViewById(R.id.tip_vehiculo);
        id_chofer = (TextView) findViewById(R.id.idChofer);

        new ServiciosActivity.obtenerServicios().execute();


        spinnerServicios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i!=0) {
                    JSONObject jObject = null;
                    try {
                        jObject = serviciosResult.getJSONObject(i - 1);
                        idServicio = jObject.getInt("id_servicio");
                        long_enc.setText(jObject.getString("longitud_encuentro"));
                        long_dest.setText(jObject.getString("longitud_destino"));
                        lat_enc.setText(jObject.getString("latitud_encuentro"));
                        lat_dest.setText(jObject.getString("latitud_destino"));
                        desc_enc.setText(jObject.getString("descripcion_lugar_encuentro"));
                        desc_dest.setText(jObject.getString("descripcion_lugar_destino"));
                        hor_enc.setText(jObject.getString("hora_encuentro"));
                        fec_enc.setText(jObject.getString("fecha_encuentro"));
                        switch (jObject.getString("estado_servicio")){
                            case "s":
                                est_serv.setText("Sin comenzar");
                                break;
                            case "p":
                                est_serv.setText("En progreso");
                                break;
                            case "c":
                                est_serv.setText("Cancelado");
                                break;
                            case "t":
                                est_serv.setText("Terminado");
                                break;
                        }
                        cal_serv.setText(jObject.getString("calificacion_servicio"));
                        model_ve.setText(jObject.getString("modelo_vehiculo"));
                        mar_ve.setText(jObject.getString("marca_vehiculo"));
                        an_ve.setText(jObject.getString("anio_vehiculo"));
                        col_ve.setText(jObject.getString("color_vehiculo"));
                        pla_ve.setText(jObject.getString("numero_placas"));
                        tip_ve.setText(jObject.getString("tipo_vehiculo"));
                        fum_serv.setText(jObject.getString("fecha_ultima_modificacion"));
                        //jObject.getString("id_chofer")
                        //Enviar a mas informacion
                        id_chofer.setText(jObject.getString("nombre_chofer"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private class obtenerServicios extends AsyncTask<String, Void, ArrayList> {
        // Do some validation here

        protected ArrayList doInBackground(String... urls) {
            // Do some validation here
            ArrayList fields = new ArrayList(Arrays.asList("id","tipo"));
            ArrayList values = new ArrayList(Arrays.asList(idSolicitud,tipoUsuario));
            //Log.i("SolicitudOb",idSolicitud);
            return fG.functionGetRequest("gservicios",fields,values);//Funcion que regresa JSON con todos los datos de los pasajeros/invitados
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
                        JSONObject oneObject = serviciosResult.getJSONObject(i);
                        serviciosArray[i + 1] = oneObject.getString("fecha_ultima_modificacion")+" - "+oneObject.getString("descripcion_lugar_encuentro") + " -> " + oneObject.getString("descripcion_lugar_destino");
                    }
                    spinnerAdapterServicios = new customSpinnerAdapter(ServiciosActivity.this,serviciosArray, null);
                    spinnerServicios.setAdapter(spinnerAdapterServicios);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"No hay servicios regitrados a esta solicitud", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
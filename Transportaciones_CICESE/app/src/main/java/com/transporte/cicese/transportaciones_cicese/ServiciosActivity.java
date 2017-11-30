package com.transporte.cicese.transportaciones_cicese;


import android.content.Intent;
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
    private String title,encuentro,destino,latEn,latDes,lonEn,lonDes;
    int idServicio,st;
    private TextView desc_enc, hor_enc, fec_enc,
            desc_dest, model_ve, mar_ve, an_ve, col_ve,
            pla_ve, tip_ve, id_chofer, est_serv, cal_serv,
            fum_serv;
    private Spinner spinnerServicios, solicitudesSpinner;
    Button verEncuentro,verDestino, verRuta, stateServicio;
    private funcionesGeneradoras fG;
    private JSONArray serviciosResult, solicitudesResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        fG = new funcionesGeneradoras(getApplicationContext());

        title = "Servicios";
        getSupportActionBar().setTitle(title);


        stateServicio        = (Button) findViewById(R.id.stateServicio);
        verDestino           = (Button) findViewById(R.id.verDestino);
        verEncuentro         = (Button) findViewById(R.id.verEncuentro);
        verRuta              = (Button) findViewById(R.id.verRuta);
        spinnerServicios     = (Spinner) findViewById(R.id.buscarServicios);
        solicitudesSpinner   = (Spinner) findViewById(R.id.solicitudesSpinner);
        fum_serv             = (TextView) findViewById(R.id.fum_servicio);
        est_serv             = (TextView) findViewById(R.id.est_servicio);
       // cal_serv             = (TextView) findViewById(R.id.calif_servicio);
        desc_enc             = (TextView) findViewById(R.id.descripcion_lugar_encuentro);
        hor_enc              = (TextView) findViewById(R.id.hor_encuentro);
        fec_enc              = (TextView) findViewById(R.id.fec_encuentro);
        desc_dest            = (TextView) findViewById(R.id.desc_destino);
        model_ve             = (TextView) findViewById(R.id.model_vehiculo);
        mar_ve               = (TextView) findViewById(R.id.marc_vehiculo);
        an_ve                = (TextView) findViewById(R.id.an_vehiculo);
        col_ve               = (TextView) findViewById(R.id.col_vehiculo);
        pla_ve               = (TextView) findViewById(R.id.pla_vehiculo);
        tip_ve               = (TextView) findViewById(R.id.tip_vehiculo);
        id_chofer            = (TextView) findViewById(R.id.idChofer);

        SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
        String tipoUsuario = settings.getString("tipoUsuario", "Default");
        switch (tipoUsuario) {
            case "p":
                spinnerServicios.setEnabled(true);
                new ServiciosActivity.obtenerServicios().execute();
                break;
            default:
                solicitudesSpinner.setVisibility(View.VISIBLE);
                new ServiciosActivity.obtenerSolicitudes().execute();
                break;

        }
        final String[] estados = {"Sin Comenzar","En Progreso","Terminado","Cancelado"};

        stateServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actual=stateServicio.getText().toString();
                for (int aux=0;aux<estados.length-1;aux++)
                    if(actual==estados[aux])
                        st=aux;
                if (st==estados.length-1) st=0;
                else st++;
                stateServicio.setText(estados[st]);
            }
        });
        verEncuentro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ServiciosActivity.this,MapsActivity.class);
                i.putExtra("action","l");
                i.putExtra("latitud",latEn);
                i.putExtra("longitud",lonEn);
                i.putExtra("address",encuentro);
                startActivity(i);
            }
        });
        verDestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ServiciosActivity.this,MapsActivity.class);
                i.putExtra("action","l");
                i.putExtra("latitud",latDes);
                i.putExtra("longitud",lonDes);
                i.putExtra("address",destino);
                startActivity(i);
            }
        });
        verRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ServiciosActivity.this,MapsActivity.class);
                i.putExtra("action","r");
                i.putExtra("origen",encuentro);
                i.putExtra("destino",destino);
                startActivity(i);
            }
        });
        solicitudesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(solicitudesResult.get(position - 1).toString());
                        new ServiciosActivity.obtenerServicios().execute(jObject.getString("id_solicitud"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerServicios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(serviciosResult.get(i-1).toString());
                        //idServicio = jObject.getInt("id_servicio");

                        encuentro=jObject.getString("descripcion_lugar_encuentro");
                        destino=jObject.getString("descripcion_lugar_destino");
                        latDes=jObject.getString("latitud_destino");
                        latEn=jObject.getString("latitud_encuentro");
                        lonDes=jObject.getString("longitud_destino");
                        lonEn=jObject.getString("longitud_encuentro");
                        desc_enc.setText(encuentro);
                        desc_dest.setText(destino);
                        String hora=jObject.getString("hora_encuentro");
                        hor_enc.setText(hora);
                        Toast.makeText(getApplicationContext(),hora,Toast.LENGTH_SHORT).show();
                        fec_enc.setText(jObject.getString("fecha_encuentro"));
                        switch (jObject.getString("estado_servicio")) {
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
            SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
            String tipoUsuario = settings.getString("tipoUsuario", "Default");
            //List<String> myList = new ArrayList<String>(Arrays.asList(arr.split(",")));
            ArrayList fields = new ArrayList(Arrays.asList("id"));
            ArrayList result = new ArrayList();
            ArrayList values;
            switch (tipoUsuario) {
                case "p":
                    String solicitudes = settings.getString("idSolicitudes", "Default")
                            .replace("[", "").replace("]", "");
                    values = new ArrayList(Arrays.asList(solicitudes));
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
                    spinnerAdapterServicios = new customSpinnerAdapter(ServiciosActivity.this, serviciosArray, null);
                    spinnerServicios.setAdapter(spinnerAdapterServicios);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "No hay servicios regitrados a esta solicitud", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class obtenerSolicitudes extends AsyncTask<String, Void, ArrayList> {
        // Do some validation here

        protected ArrayList doInBackground(String... urls) {
            // Do some validation here
            SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
            String tipoUsuario = settings.getString("tipoUsuario", "Default");
            String arr = settings.getString("idSolicitudes", "Default").replace("[", "")
                    .replace("]", "").replace(" ", "");
            Log.i("arr",arr);
            //List<String> myList = new ArrayList<String>(Arrays.asList(arr.split(",")));
            ArrayList fields = new ArrayList(Arrays.asList("idSolicitudes"));
            ArrayList values = new ArrayList(Arrays.asList(arr));
            return fG.functionGetRequest("gDataSolicituds", fields, values);


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
                            (ServiciosActivity.this, solicitudesArray, null);
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
}
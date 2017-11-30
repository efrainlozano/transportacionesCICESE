package com.transporte.cicese.transportaciones_cicese;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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
    private String title,encuentro,destino,latEn,latDes,lonEn,lonDes, prev,currentServ;
    int st;
    private TextView desc_enc, hor_enc, fec_enc,
            desc_dest, model_ve, mar_ve, an_ve, col_ve,
            pla_ve, tip_ve, id_chofer, est_serv, fum_serv;
    private Spinner spinnerServicios, solicitudesSpinner;
    private Button verEncuentro,verDestino, verRuta, stateServicio, setEstado, calificar;
    private JSONArray serviciosResult, solicitudesResult;
    private RatingBar calificacion;
    private RelativeLayout servicioLayout;
    private Handler handler;
    private Boolean calChecker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);
        title = "Servicios";
        getSupportActionBar().setTitle(title);

        calChecker=true;
        stateServicio        = (Button) findViewById(R.id.stateServicio);
        verDestino           = (Button) findViewById(R.id.verDestino);
        verEncuentro         = (Button) findViewById(R.id.verEncuentro);
        verRuta              = (Button) findViewById(R.id.verRuta);
        setEstado            = (Button) findViewById(R.id.setEstado);
        calificar            = (Button) findViewById(R.id.buttonCalificar);
        spinnerServicios     = (Spinner) findViewById(R.id.buscarServicios);
        solicitudesSpinner   = (Spinner) findViewById(R.id.solicitudesSpinner);
        fum_serv             = (TextView) findViewById(R.id.fum_servicio);
        est_serv             = (TextView) findViewById(R.id.est_servicio);
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
        calificacion         = (RatingBar)findViewById(R.id.ratingServicio);
        servicioLayout       = (RelativeLayout) findViewById(R.id.despliegueInfo);

        setViewState(false);//Desabilitamos los elementos interactivos necesarios del layout

        SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
        String tipoUsuario = settings.getString("tipoUsuario", "Default");
        switch (tipoUsuario) {
            case "p":
                spinnerServicios.setEnabled(true);
                stateServicio.setVisibility(View.INVISIBLE);
                setEstado.setVisibility(View.INVISIBLE);
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
                prev=stateServicio.getText().toString();
                setEstado.setEnabled(true);
                for (int aux=0;aux<estados.length-1;aux++)
                    if(prev==estados[aux])
                        st=aux;
                if (st==estados.length-1) st=0;
                else st++;
                stateServicio.setText(estados[st]);
            }
        });
        id_chofer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(serviciosResult.get(spinnerServicios.getSelectedItemPosition()-1).toString());
                    String nombre = jObject.getString("nombre_chofer");
                    String numero = jObject.getString("telefono_chofer");
                    alertDialog("Informacion de chofer","Nombre del chofer:\n     "+nombre+"\n" +
                                                                      "Numero de telefono:\n     "+numero);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

        setEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] estadosBD = {"s","p","t","c"};
                setConfirmation(stateServicio.getText().toString(),estadosBD[st],currentServ,prev);

            }
        });
        calificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calificarServicio();
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
                else{
                    spinnerServicios.setAdapter(null);
                    setViewState(false);
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
                        currentServ = jObject.getString("id_servicio");
                        verDestino.setEnabled(true);
                        verEncuentro.setEnabled(true);
                        verRuta.setEnabled(true);
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
                        fec_enc.setText(jObject.getString("fecha_encuentro"));
                        switch (jObject.getString("estado_servicio")) {
                            case "s":
                                est_serv.setText("Este servicio aun no comienza");
                                stateServicio.setText("Sin comenzar");
                                break;
                            case "p":
                                est_serv.setText("El chofer se encuentra en camino");
                                stateServicio.setText("En progreso");
                                break;
                            case "c":
                                est_serv.setText("El chofer/asistente canceló el servicio");
                                stateServicio.setText("Cancelado");
                                break;
                            case "t":
                                est_serv.setText("Este servicio ya fue terminado");
                                stateServicio.setText("Finalizado");
                                int cal=jObject.getInt("calificacion_servicio");
                                if((cal==0)&&(calChecker)) {
                                    alertDialog("Califica tu servicio","Ahora puedes calificar tu servicio con el boton 'Califica tu servicio '" +
                                            "que se muestra en la seccion de detalles");
                                    calificar.setText("Califica tu servicio");
                                    calificar.setVisibility(View.VISIBLE);
                                    calificacion.setVisibility(View.INVISIBLE);
                                }
                                else if (cal!=0){
                                    calificacion.setVisibility(View.VISIBLE);
                                    calificar.setVisibility(View.INVISIBLE);
                                    calificacion.setRating(jObject.getInt("calificacion_servicio"));
                                }
                                break;
                        }
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
                        servicioLayout.setEnabled(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                        desc_enc.setText("");
                        desc_dest.setText("");
                        hor_enc.setText("");
                        fec_enc.setText("");
                        est_serv.setText("");
                        model_ve.setText("");
                        mar_ve.setText("");
                        an_ve.setText("");
                        col_ve.setText("");
                        pla_ve.setText("");
                        tip_ve.setText("");
                        fum_serv.setText("");
                        id_chofer.setText("");
                        setViewState(false);
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
            funcionesGeneradoras fG = new funcionesGeneradoras(getApplicationContext());
            SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
            String tipoUsuario = settings.getString("tipoUsuario", "Default");
            //List<String> myList = new ArrayList<String>(Arrays.asList(arr.split(",")));
            ArrayList fields = new ArrayList(Arrays.asList("id"));
            ArrayList result;
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
            funcionesGeneradoras fG = new funcionesGeneradoras(getApplicationContext());
            SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
            String tipoUsuario = settings.getString("tipoUsuario", "Default");
            String arr = settings.getString("idSolicitudes", "Default").replace("[", "")
                    .replace("]", "").replace(" ", "");
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

    private class sendState extends AsyncTask<String, Void, ArrayList> {
        // Do some validation here

        protected ArrayList doInBackground(String... valuesParam) {
            // Do some validation here
            funcionesGeneradoras fG = new funcionesGeneradoras(getApplicationContext()); //instanciamos la clase que regresa los datos para los spinners
            JSONObject postDataParams = new JSONObject();
            try {
                postDataParams.put("id_servicio", valuesParam[0]);
                postDataParams.put("estado", valuesParam[1]);
                //Funcion que regresa JSON con todos los datos de los pasajeros/invitados
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return fG.functionPostRequest("ucambiarestado", postDataParams);
        }

        protected void onPostExecute(ArrayList result) {
            if ((Integer) result.get(0) == HttpURLConnection.HTTP_OK) {
                Toast.makeText(getApplicationContext(), "Estado actualizado con exito",
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "No se pudo actualizar el estado",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void setConfirmation(String nuevo,final String car,final String id,final String prev) {
            AlertDialog.Builder alert = new AlertDialog.Builder(ServiciosActivity.this);
        alert.setTitle("Cambiar estado de servicio?");
        alert.setMessage("Esta seguro que desea cambiar el estado del servicio a '"+nuevo+"'");
        alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                new ServiciosActivity.sendState().execute(id,car);
                dialog.dismiss();

            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                stateServicio.setText(prev);
                dialog.dismiss();
            }
        });

        alert.show();
    }
    private class sendCalificacion extends AsyncTask<String, Void, ArrayList> {
        // Do some validation here

        protected ArrayList doInBackground(String... valuesParam) {
            // Do some validation here
            funcionesGeneradoras fG = new funcionesGeneradoras(getApplicationContext()); //instanciamos la clase que regresa los datos para los spinners
            JSONObject postDataParams = new JSONObject();
            try {
                postDataParams.put("id_servicio", valuesParam[0]);
                postDataParams.put("calificacion", valuesParam[1]);
                //Funcion que regresa JSON con todos los datos de los pasajeros/invitados
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList result= fG.functionPostRequest("acalificacion", postDataParams);
            result.add(valuesParam[1]);
            return result;
        }

        protected void onPostExecute(ArrayList result) {
            if ((Integer) result.get(0) == HttpURLConnection.HTTP_OK) {
                Toast.makeText(getApplicationContext(), "Gracias",
                        Toast.LENGTH_SHORT).show();
                calificar.setVisibility(View.INVISIBLE);
                calificacion.setVisibility(View.VISIBLE);
                calificacion.setRating(Integer.valueOf(result.get(2).toString().charAt(0)));
                calChecker=false;

            } else {
                Toast.makeText(getApplicationContext(), " No se pudo registrar tu calificacion",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void alertDialog(String title,String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(ServiciosActivity.this);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alert.show();
    }

    private void setViewState(boolean state){
        stateServicio.setEnabled(state);
        verRuta.setEnabled(state);
        verEncuentro.setEnabled(state);
        verDestino.setEnabled(state);
        if (!state)
            setEstado.setEnabled(state);
    }

    public void calificarServicio() {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final RatingBar rating = new RatingBar(this);
        rating.setNumStars(5);
        rating.setStepSize(1.0f);
        rating.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout parent = new LinearLayout(this);
        parent.setGravity(Gravity.CENTER);
        parent.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        parent.addView(rating);

        // popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("Calificacion");
        popDialog.setView(parent);

        // Button OK
        popDialog.setPositiveButton("Enviar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new ServiciosActivity.sendCalificacion().execute(currentServ,String.valueOf(rating.getRating()));
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        popDialog.create();
        popDialog.show();
    }
}
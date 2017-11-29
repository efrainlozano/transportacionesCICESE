package com.transporte.cicese.transportaciones_cicese;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.transporte.cicese.transportaciones_cicese.adapters.customSpinnerAdapter;
import com.transporte.cicese.transportaciones_cicese.funciones.funcionesGeneradoras;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class RegistroSolicitudActivity extends AppCompatActivity {

    //Selector de fecha y hora: botones
    private ImageButton seleccionaH, seleccionaF;
    int day,month,year,hour,minutos;

    private Button genera;
    String folioGenerado = null;

    private EditText folioET, longitud_destino, latitud_destino,longitud_encuentro,latitud_encuentro,
            descripcion_lugar_encuentro,descripcion_lugar_destino,
            hora_encuentro,fecha_encuentro,modelo_vehiculo,marca_vehiculo,
            anio_vehiculo,color_vehiculo,numero_placas,tipo_vehiculo;

    private Spinner spinner_chofer,spinner_pasajero;

    Button registrarSol, registrarServicio;

    int idSolicitud, idAsistente;

    JSONObject postDataParams;


    RelativeLayout registroSol,registroSer;

    funcionesGeneradoras fG;

    String[] invitadosArray, choferesArray;
    JSONArray invitadosResult, choferesResult;

    customSpinnerAdapter spinnerAdapterInvitado, spinnerAdapterChofer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_solicitud);
        SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
        idAsistente=settings.getInt("idUsuario",0);

        //Componentes ImageButton
        seleccionaF                 = (ImageButton) findViewById(R.id.selectorFecha);
        seleccionaH                 = (ImageButton) findViewById(R.id.selectorHora);

        //Componentes button
        registrarSol                =   (Button) findViewById(R.id.registrarSolicitud);
        registrarServicio           =   (Button) findViewById(R.id.registrarServicio);
        genera                      =   (Button) findViewById(R.id.generarFolio_btn);

        //Componentes EditText
        folioET                     =   (EditText) findViewById(R.id.folioSolicitud);
        longitud_destino            =   (EditText) findViewById(R.id.usuario_generado);
        latitud_destino             =   (EditText) findViewById(R.id.contrasena_pasajero);
        longitud_encuentro          =   (EditText) findViewById(R.id.nombre_pasajero);
        latitud_encuentro           =   (EditText) findViewById(R.id.ap_pasajero);
        descripcion_lugar_encuentro =   (EditText) findViewById(R.id.descripcion_lugar_encuentro);
        descripcion_lugar_destino   =   (EditText) findViewById(R.id.descripcion_lugar_destino);
        hora_encuentro              =   (EditText) findViewById(R.id.horaEncuentro);
        fecha_encuentro             =   (EditText) findViewById(R.id.fecha_encuentro);
        modelo_vehiculo             =   (EditText) findViewById(R.id.modelo_vehiculo);
        marca_vehiculo              =   (EditText) findViewById(R.id.marca_vehiculo);
        anio_vehiculo               =   (EditText) findViewById(R.id.anio_vehiculo);
        color_vehiculo              =   (EditText) findViewById(R.id.color_vehiculo);
        numero_placas               =   (EditText) findViewById(R.id.numero_placas);
        tipo_vehiculo               =   (EditText) findViewById(R.id.tipo_vehiculo);

        spinner_pasajero            =   (Spinner) findViewById(R.id.idInvitado);
        spinner_chofer              =   (Spinner) findViewById(R.id.id_chofer);

        fG = new funcionesGeneradoras(getApplicationContext());

        new RegistroSolicitudActivity.poblarInvitadoSpinner().execute();

        //Para seleccionar la fecha y hora
        seleccionaF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(RegistroSolicitudActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        fecha_encuentro.setText(year+"-"+(month+1)+"-"+dayOfMonth);
                                    }
                                }, year, month, day);
                                   datePickerDialog.show();
            }
        });
        seleccionaH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c =  Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minutos = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(RegistroSolicitudActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if(hourOfDay==0||hourOfDay==1||hourOfDay==2||hourOfDay==3||hourOfDay==4||hourOfDay==5||hourOfDay==6||hourOfDay==7||hourOfDay==8||hourOfDay==9){
                                    if(minute==0||minute==1||minute==2||minute==3||minute==4||minute==5||minute==6||minute==7||minute==8||minute==9){
                                        hora_encuentro.setText("0"+hourOfDay+":"+"0"+minute);
                                    }else{
                                        hora_encuentro.setText("0"+hourOfDay+":"+minute);
                                    }
                                }
                                else{
                                    if(minute==0||minute==1||minute==2||minute==3||minute==4||minute==5||minute==6||minute==7||minute==8||minute==9){
                                        hora_encuentro.setText(hourOfDay+":"+"0"+minute);
                                    }else {
                                        hora_encuentro.setText(hourOfDay + ":" + minute);
                                    }
                                }
                            }
                        }, hour, minutos, false);
                        timePickerDialog.show();
            }
        });



        //Para generar el folio
        genera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionesGeneradoras fG = new funcionesGeneradoras(getApplicationContext());
                folioGenerado = fG.generaFolio();
                folioET.setText(folioGenerado);
            }
        });

        //Para registrar la solicitud
        registrarSol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinner_pasajero.getSelectedItemPosition()!=0) {
                    new RegistroSolicitudActivity.registrarSolicitud().execute();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Por favor elegir a un pasajero del listado",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Se trata de registrar un servicio a una solicitud previamente creada
        registrarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RegistroSolicitudActivity.registrarServicio().execute();
            }
        });
    }
    public class registrarServicio extends AsyncTask<String, Void, ArrayList> {

        protected ArrayList doInBackground(String... urls) {
            fG= new funcionesGeneradoras(getApplicationContext());
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            try {
                postDataParams = new JSONObject();
                postDataParams.put("longitud_destino"           , longitud_destino.getText().toString());
                postDataParams.put("latitud_destino"            , latitud_destino.getText().toString());
                postDataParams.put("longitud_encuentro"         , longitud_encuentro.getText().toString());
                postDataParams.put("latitud_encuentro"          , latitud_encuentro.getText().toString());
                postDataParams.put("hora_encuentro"             , hora_encuentro.getText().toString());
                postDataParams.put("fecha_encuentro"            , fecha_encuentro.getText().toString());
                postDataParams.put("estado_servicio"            , "s");
                postDataParams.put("modelo_vehiculo"            , modelo_vehiculo.getText().toString());
                postDataParams.put("marca_vehiculo"             , marca_vehiculo.getText().toString());
                postDataParams.put("anio_vehiculo"              , anio_vehiculo.getText().toString());
                postDataParams.put("color_vehiculo"             , color_vehiculo.getText().toString());
                postDataParams.put("numero_placas"              , numero_placas.getText().toString());
                postDataParams.put("tipo_vehiculo"              , tipo_vehiculo.getText().toString());
                postDataParams.put("descripcion_lugar_encuentro", descripcion_lugar_encuentro.getText().toString());
                postDataParams.put("descripcion_lugar_destino"  , descripcion_lugar_destino.getText().toString());
                postDataParams.put("fecha_ultima_modificacion"  , today.year+"-"+(today.month+1)+"-"+today.monthDay);//Obtener fecha automaticamente
                postDataParams.put("id_solicitud"               , idSolicitud);
                postDataParams.put("id_chofer"                  , spinnerAdapterChofer.getId(spinner_chofer.getSelectedItemPosition()));

                return fG.functionPostRequest("aservicio"     ,postDataParams);
            }
            catch(Exception e){
                Log.e("Exception",e.toString());
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList responseResult) {
            int responseCode=(Integer)responseResult.get(0);
            if(responseCode==HttpURLConnection.HTTP_OK) {
                Toast.makeText(getApplicationContext(), "El servicio se ha registrado con éxito", Toast.LENGTH_SHORT).show();

                //Limpiamos los editText para poder agregar un nuevo servicio a esta solicitud
                longitud_destino.setText(""); latitud_destino.setText(""); longitud_encuentro.setText(""); latitud_encuentro.setText("");
                hora_encuentro.setText(""); fecha_encuentro.setText(""); modelo_vehiculo.setText(""); marca_vehiculo.setText("");
                anio_vehiculo.setText(""); color_vehiculo.setText(""); numero_placas.setText(""); tipo_vehiculo.setText("");
                descripcion_lugar_encuentro.setText(""); descripcion_lugar_destino.setText("");
            }
            else {
                Toast.makeText(getApplicationContext(), "Error al registrar el servicio, intentelo más tarde o contacte al administrador", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class registrarSolicitud extends AsyncTask<String, Void, ArrayList> {

        protected ArrayList doInBackground(String... urls) {
            try {

                postDataParams = new JSONObject();
                postDataParams.put("id_folio"       , folioET.getText().toString());
                postDataParams.put("id_invitado"    , spinnerAdapterInvitado.getId(spinner_pasajero.getSelectedItemPosition()-1));
                postDataParams.put("id_asistente"   , idAsistente);

                return fG.functionPostRequest("asolicitud",postDataParams);
            }
            catch(Exception e){
                Log.e("Exception",e.toString());
                return  new ArrayList(Arrays.asList(0));
            }

        }

        @Override
        protected void onPostExecute(ArrayList responseResult) {
            int responseCode=(Integer)responseResult.get(0);
            if(responseCode==HttpURLConnection.HTTP_OK) {
                String result = responseResult.get(1).toString();
                result = result.substring(1, result.length() - 1);
                try {
                    JSONObject jObject = new JSONObject(result);
                    idSolicitud = Integer.parseInt(jObject.getString("id_solicitud"));
                } catch (JSONException e) {
                    Log.e("JSONException",e.toString());
                }
                registroSer = (RelativeLayout) findViewById(R.id.relativeServicio);
                registroSer.setVisibility(View.VISIBLE);
                // -----NOTA-----
                registroSol = (RelativeLayout) findViewById(R.id.solicitudR);
                // -----NOTA-----
                registroSol.setEnabled(false);
                new RegistroSolicitudActivity.poblarChoferSpinner().execute();
                Toast.makeText(getApplicationContext(), "El servicio se ha registrado con éxito", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(getApplicationContext(), "Error al registrar solicitud, intentelo más tarde o contacte al administrador", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class poblarInvitadoSpinner extends AsyncTask<String, Void, ArrayList> {
        //AsyncTask encargado de poblar el spinner de invitados/pasajeros
        protected ArrayList doInBackground(String... strings) {
            ArrayList fields = new ArrayList(Arrays.asList("tipo"));
            ArrayList values = new ArrayList(Arrays.asList("p"));
            return fG.functionGetRequest("gusuarioscb",fields,values);//Funcion que regresa JSON con todos los datos de los pasajeros/invitados
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            if((Integer)result.get(0)==HttpURLConnection.HTTP_OK) {
                String data = result.get(1).toString();
                try {
                    invitadosResult = new JSONArray(data);
                    invitadosArray = new String[invitadosResult.length() + 1];
                    ArrayList index = new ArrayList();
                    invitadosArray[0] = "Selecciona un usuario pasajero de la lista";
                    for (int i = 0; i < invitadosResult.length(); i++) {
                        JSONObject oneObject = invitadosResult.getJSONObject(i);
                        invitadosArray[i + 1] = oneObject.getString("nombre") + " " + oneObject.getString("apellido_paterno") + " " + oneObject.getString("apellido_materno");
                        index.add(oneObject.getString("id_invitado"));
                    }
                    spinnerAdapterInvitado = new customSpinnerAdapter(RegistroSolicitudActivity.this, invitadosArray, index);
                    spinner_pasajero.setAdapter(spinnerAdapterInvitado);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"Ocurrió un problema, revise su conexión", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class poblarChoferSpinner extends AsyncTask<String, Void, ArrayList> {
        //AsyncTask encargado de poblar el spinner de invitados/pasajeros
        protected ArrayList doInBackground(String... strings) {
            ArrayList fields = new ArrayList(Arrays.asList("tipo"));
            ArrayList values = new ArrayList(Arrays.asList("c"));
            return fG.functionGetRequest("gusuarioscb",fields,values);//Funcion que regresa JSON con todos los datos de los pasajeros/invitados
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            if ((Integer)result.get(0)==HttpURLConnection.HTTP_OK) {
                String data = result.get(1).toString();
                try {
                    choferesResult = new JSONArray(data);
                    choferesArray = new String[choferesResult.length()];
                    ArrayList index = new ArrayList();
                    for (int i = 0; i < choferesResult.length(); i++) {
                        JSONObject oneObject = choferesResult.getJSONObject(i);
                        choferesArray[i] = oneObject.getString("numero_empleado") + " - " + oneObject.getString("nombre") + " " + oneObject.getString("apellido_paterno") + " " + oneObject.getString("apellido_materno");
                        index.add(oneObject.getString("id_chofer"));
                    }
                    spinnerAdapterChofer = new customSpinnerAdapter(RegistroSolicitudActivity.this, choferesArray, index);
                    spinner_chofer.setAdapter(spinnerAdapterChofer);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Ocurrió un problema al buscar usuarios choferes, revise su conexión", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
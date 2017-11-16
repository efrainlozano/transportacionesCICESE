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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.transporte.cicese.transportaciones_cicese.adapters.customSpinnerAdapter;
import com.transporte.cicese.transportaciones_cicese.funciones.funcionesGeneradoras;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by blanca on 29/10/2017.
 */

public class updateSolicitud extends AppCompatActivity {

    //Selector de fecha y hora: botones
    private ImageButton seleccionaH, seleccionaF;
    int day,month,year,hour,minutos;

    EditText folioEdit, longitud_destino,latitud_destino,longitud_encuentro,latitud_encuentro,descripcion_lugar_encuentro,descripcion_lugar_destino,
            hora_encuentro,fecha_encuentro,modelo_vehiculo,marca_vehiculo,anio_vehiculo,color_vehiculo,numero_placas,tipo_vehiculo,id_chofer,id_solicitud;
    Button buttonUpdateSolicitud,actualizarServicioButton, actualizarTodoButton;
    CheckBox checkBoxServicios;
    RelativeLayout servicioLayout, servicioForm, formContainer;
    Spinner solicitudesSpinner, invitadoSpinner, serviciosSpinner, choferSpinner, solicitudSpinner;

    String ida,idi,idf;
    int idUsuario, responseCode, idServicio;// idUsuario =Identificador del usuario logueado (Dato guardado como sharedpreference al iniciar sesion)
    String tipoUsuario; //Tipo de usuario logueado en el sistema(Dato guardado como sharedPreference al iniciar sesion)
    /*a=asistente
    * p=invitado/pasajero
    * c=chofer*/

    String[] solicitudesArray,invitadosArray, serviciosArray, choferesArray;
    JSONArray solicitudesResult, invitadosResult, serviciosResult, choferesResult;
    int idSolicitud; //Identificador de la solicitud que se esta modificando
    funcionesGeneradoras fG;
    customSpinnerAdapter spinnerAdapterSolicitudes,spinnerAdapterInvitado, spinnerAdapterServicios, spinnerAdapterChofer;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatesolicitudform);

        //Componentes ImageButton
        seleccionaF                 = (ImageButton) findViewById(R.id.selectorFechaU);
        seleccionaH                 = (ImageButton) findViewById(R.id.selectorHoraU);


        solicitudesSpinner          =(Spinner) findViewById(R.id.solicitudesSpinner);
        invitadoSpinner             =(Spinner)findViewById(R.id.invitadoSpinner);
        serviciosSpinner            =(Spinner) findViewById(R.id.serviciosSpinner);
        choferSpinner               =(Spinner) findViewById(R.id.choferSpinner);
        solicitudSpinner            =(Spinner) findViewById(R.id.solicitudSpinner);

        folioEdit                   =(EditText)findViewById(R.id.folioEdit);
        longitud_destino            =(EditText)findViewById(R.id.usuario_generado);
        latitud_destino             =(EditText)findViewById(R.id.contrasena_pasajero);
        longitud_encuentro          =(EditText)findViewById(R.id.nombre_pasajero);
        latitud_encuentro           =(EditText)findViewById(R.id.ap_pasajero);
        descripcion_lugar_encuentro =(EditText)findViewById(R.id.descripcion_lugar_encuentro);
        descripcion_lugar_destino   =(EditText)findViewById(R.id.descripcion_lugar_destino);
        hora_encuentro              =(EditText)findViewById(R.id.horaEncuentro);
        fecha_encuentro             =(EditText)findViewById(R.id.fecha_encuentro);
        modelo_vehiculo             =(EditText)findViewById(R.id.modelo_vehiculo);
        marca_vehiculo              =(EditText)findViewById(R.id.marca_vehiculo);
        anio_vehiculo               =(EditText)findViewById(R.id.anio_vehiculo);
        color_vehiculo              =(EditText)findViewById(R.id.color_vehiculo);
        numero_placas               =(EditText)findViewById(R.id.numero_placas);
        tipo_vehiculo               =(EditText)findViewById(R.id.tipo_vehiculo);

        SharedPreferences settings  = getSharedPreferences("prefs", MODE_PRIVATE);
        idUsuario                   = settings.getInt("idUsuario",0);
        tipoUsuario                 = settings.getString("tipoUsuario","Default");

        buttonUpdateSolicitud       =(Button)findViewById(R.id.buttonUpdateSolicitud);
        actualizarServicioButton    =(Button)findViewById(R.id.actualizarServicioButton);
        actualizarTodoButton        =(Button)findViewById(R.id.actualizarTodoButton);

        checkBoxServicios           =(CheckBox) findViewById(R.id.checkBoxServicios);

        servicioLayout              =(RelativeLayout) findViewById(R.id.servicioLayout);
        servicioForm                =(RelativeLayout) findViewById(R.id.servicioForm);
        formContainer               =(RelativeLayout) findViewById(R.id.formContainer);

        fG = new funcionesGeneradoras(getApplicationContext()); //instanciamos la clase que regresa los datos para los spinners

        new updateSolicitud.poblarSolicitudesSpinner().execute();

        new updateSolicitud.poblarInvitadoSpinner().execute();


        //Para seleccionar la fecha y hora
        seleccionaF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(updateSolicitud.this,
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(updateSolicitud.this,
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




        solicitudesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i>0) {//Si el campo seleccionado es valido
                    buttonUpdateSolicitud.setEnabled(true);
                    checkBoxServicios.setEnabled(true);
                    folioEdit.setEnabled(true);
                    invitadoSpinner.setEnabled(true);
                    try {
                        JSONObject oneObject = solicitudesResult.getJSONObject(i - 1);
                        idSolicitud = oneObject.getInt("id_solicitud");
                        folioEdit.setText(oneObject.getString("id_folio"));//Autollenamos el campo de folio de la solicitud
                        invitadoSpinner.setSelection(spinnerAdapterInvitado.getIndex(oneObject.getString("id_invitado"))+1);//Auto seleccionamos al invitado de esta solicitud

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    folioEdit.setText("");//Autollenamos el campo de folio de la solicitud
                    invitadoSpinner.setSelection(0);
                    buttonUpdateSolicitud.setEnabled(false);
                    checkBoxServicios.setChecked(false);
                    checkBoxServicios.setEnabled(false);
                    folioEdit.setEnabled(false);
                    invitadoSpinner.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        buttonUpdateSolicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (invitadoSpinner.getSelectedItemPosition()!=0) {//Si la opcion seleccionada del spinner de invitados es valida
                    new updateSolicitud.SendUpdateData().execute(getString(R.string.URI) + "/usolicitud");
                }
                else{
                    Toast.makeText(getApplicationContext(),"Debes seleccionar un pasajero para esta solicitud", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBoxServicios.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){//Manipulamos el estado de la interfaz
                    solicitudesSpinner.setEnabled(false);
                    folioEdit.setEnabled(false);
                    invitadoSpinner.setEnabled(false);
                    buttonUpdateSolicitud.setEnabled(false);
                    //Poblamos los spinners necesarios
                    new updateSolicitud.poblarServiciosSpinner().execute();
                    new updateSolicitud.poblarChoferSpinner().execute();
                }
                else{
                    servicioLayout.setVisibility(View.INVISIBLE);
                    solicitudesSpinner.setEnabled(true);
                    folioEdit.setEnabled(true);
                    invitadoSpinner.setEnabled(true);
                    buttonUpdateSolicitud.setEnabled(true);
                }

            }
        });

        serviciosSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i!=0){//Si el dato del spinner es valido ya que el primero es por default el mensaje de instruccion
                    servicioForm.setVisibility(View.VISIBLE);
                    try {
                        JSONObject oneObject = serviciosResult.getJSONObject(i-1);
                        idServicio= oneObject.getInt("id_servicio");
                        longitud_destino.setText(oneObject.getString("longitud_destino"));
                        latitud_destino.setText(oneObject.getString("latitud_destino"));
                        longitud_encuentro.setText(oneObject.getString("longitud_encuentro"));
                        latitud_encuentro.setText(oneObject.getString("latitud_encuentro"));
                        descripcion_lugar_encuentro.setText(oneObject.getString("descripcion_lugar_encuentro"));
                        descripcion_lugar_destino.setText(oneObject.getString("descripcion_lugar_destino"));
                        hora_encuentro.setText(oneObject.getString("hora_encuentro"));
                        fecha_encuentro.setText(oneObject.getString("fecha_encuentro"));
                        modelo_vehiculo.setText(oneObject.getString("modelo_vehiculo"));
                        marca_vehiculo.setText(oneObject.getString("marca_vehiculo"));
                        anio_vehiculo.setText(oneObject.getString("anio_vehiculo"));
                        color_vehiculo.setText(oneObject.getString("color_vehiculo"));
                        numero_placas.setText(oneObject.getString("numero_placas"));
                        tipo_vehiculo.setText(oneObject.getString("tipo_vehiculo"));
                        solicitudSpinner.setAdapter(spinnerAdapterSolicitudes);
                        solicitudSpinner.setSelection(spinnerAdapterSolicitudes.getIndex(Integer.toString(idSolicitud))+1);
                        choferSpinner.setSelection(spinnerAdapterChofer.getIndex(oneObject.getString("id_chofer")));



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    servicioForm.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        actualizarServicioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new updateSolicitud.sendUpdateServicio().execute(getString(R.string.URI)+"/uservicio");
            }
        });
        actualizarTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (invitadoSpinner.getSelectedItemPosition()!=0) {
                    new updateSolicitud.SendUpdateData().execute(getString(R.string.URI) + "/usolicitud");
                    new updateSolicitud.sendUpdateServicio().execute(getString(R.string.URI)+"/uservicio");
                }
                else{
                    Toast.makeText(getApplicationContext(),"Debes seleccionar un pasajero para esta solicitud", Toast.LENGTH_SHORT).show();
                }
                ;
            }
        });
    }
    public class sendUpdateServicio extends AsyncTask<String, Void, ArrayList> {

        protected ArrayList doInBackground(String... urls) {
            JSONObject postDataParams = new JSONObject();
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            try {
                postDataParams.put("id_servicio", idServicio);
                postDataParams.put("longitud_destino", longitud_destino.getText().toString());
                postDataParams.put("latitud_destino", latitud_destino.getText().toString());
                postDataParams.put("longitud_encuentro", longitud_encuentro.getText().toString());
                postDataParams.put("latitud_encuentro",latitud_encuentro.getText().toString());
                postDataParams.put("hora_encuentro",hora_encuentro.getText().toString());
                postDataParams.put("fecha_encuentro",fecha_encuentro.getText().toString());
                postDataParams.put("modelo_vehiculo", modelo_vehiculo.getText().toString());
                postDataParams.put("marca_vehiculo",marca_vehiculo.getText().toString());
                postDataParams.put("anio_vehiculo",anio_vehiculo.getText().toString());
                postDataParams.put("color_vehiculo",color_vehiculo.getText().toString());
                postDataParams.put("numero_placas",numero_placas.getText().toString());
                postDataParams.put("tipo_vehiculo",tipo_vehiculo.getText().toString());
                postDataParams.put("descripcion_lugar_encuentro",descripcion_lugar_encuentro.getText().toString());
                postDataParams.put("descripcion_lugar_destino",descripcion_lugar_destino.getText().toString());
                postDataParams.put("fecha_ultima_modificacion",today.year+"-"+(today.month+1)+"-"+today.monthDay);//Obtener fecha automaticamente
                postDataParams.put("id_solicitud",spinnerAdapterSolicitudes.getId(solicitudSpinner.getSelectedItemPosition()-1));
                postDataParams.put("id_chofer",spinnerAdapterChofer.getId(choferSpinner.getSelectedItemPosition()));

                return fG.functionPostRequest("uservicio",postDataParams);
            } catch (JSONException e) {
                return new ArrayList(Arrays.asList(0));
            }

        }

        @Override
        protected void onPostExecute(ArrayList result) {
            responseCode=(Integer)result.get(0);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(getApplicationContext(), "Informacion actualizada", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "Error al actualizar servicio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class poblarServiciosSpinner extends AsyncTask<String, Void, ArrayList> {
        //AsyncTask encargado de poblar el spinner de invitados/pasajeros
        protected ArrayList doInBackground(String... strings) {

            ArrayList fields = new ArrayList(Arrays.asList("id","tipo"));
            ArrayList values = new ArrayList(Arrays.asList(idSolicitud,tipoUsuario));
            return fG.functionGetRequest("gservicios",fields,values);//Funcion que regresa JSON con todos los datos de los pasajeros/invitados

        }

        @Override
        protected void onPostExecute(ArrayList result) {
            Log.i("Result",result.get(0).toString());
            if ((Integer) result.get(0) == HttpURLConnection.HTTP_OK) {
                servicioLayout.setVisibility(View.VISIBLE);
                String data = result.get(1).toString();
                try {
                    serviciosResult = new JSONArray(data);
                    serviciosArray = new String[serviciosResult.length()+1];
                    serviciosArray[0] = "Seleccione un servicio de la lista";
                    for (int i = 0; i < serviciosResult.length(); i++) {
                        JSONObject oneObject = serviciosResult.getJSONObject(i);
                        serviciosArray[i + 1] = oneObject.getString("descripcion_lugar_encuentro") + " -> " + oneObject.getString("descripcion_lugar_destino");
                    }
                    spinnerAdapterServicios = new customSpinnerAdapter(updateSolicitud.this, serviciosArray, null);
                    serviciosSpinner.setAdapter(spinnerAdapterServicios);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"No hay servicios regitrados a esta solicitud", Toast.LENGTH_SHORT).show();
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
                    invitadosArray[0] = "";
                    for (int i = 0; i < invitadosResult.length(); i++) {
                        JSONObject oneObject = invitadosResult.getJSONObject(i);
                        invitadosArray[i + 1] = oneObject.getString("nombre") + " " + oneObject.getString("apellido_paterno") + " " + oneObject.getString("apellido_materno");
                        index.add(oneObject.getString("id_invitado"));
                    }
                    spinnerAdapterInvitado = new customSpinnerAdapter(updateSolicitud.this, invitadosArray, index);
                    invitadoSpinner.setAdapter(spinnerAdapterInvitado);
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
                    spinnerAdapterChofer = new customSpinnerAdapter(updateSolicitud.this, choferesArray, index);
                    choferSpinner.setAdapter(spinnerAdapterChofer);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Ocurrió un problema al buscar choferes, revise su conexión", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class SendUpdateData extends AsyncTask<String, Void, ArrayList> {


        protected ArrayList doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]); // here is your URL path
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("id_solicitud", idSolicitud);
                postDataParams.put("id_folio", folioEdit.getText().toString());
                JSONObject oneObject =invitadosResult.getJSONObject(invitadoSpinner.getSelectedItemPosition());
                postDataParams.put("id_invitado", oneObject.getString("id_invitado"));

                return fG.functionPostRequest("usolicitud",postDataParams);
            }
            catch(Exception e){
                return new ArrayList(Arrays.asList(0));
            }

        }

        @Override
        protected void onPostExecute(ArrayList result) {
            responseCode=(Integer)result.get(0);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(getApplicationContext(), "Informacion actualizada", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "Error al actualizar servicio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class poblarSolicitudesSpinner extends AsyncTask<String,Void,ArrayList>{
        @Override
        protected ArrayList doInBackground(String... urls) {
            ArrayList values= new ArrayList(Arrays.asList(idUsuario,tipoUsuario));
            ArrayList fields = new ArrayList(Arrays.asList("id_usuario","tipo"));
            return fG.functionGetRequest("gSolicitudes",fields,values);
        }

        @Override
        protected void onPostExecute(ArrayList responseResult) {
            int responseCode= (Integer) responseResult.get(0);
            if (responseCode==HttpsURLConnection.HTTP_OK) {
                String result = responseResult.get(1).toString();
                try {
                    solicitudesResult = new JSONArray(result);
                    solicitudesArray = new String[solicitudesResult.length() + 1];
                    solicitudesArray[0] = "Seleccione la solicitud que desea modificar";
                    ArrayList index = new ArrayList();
                    for (int i = 0; i < solicitudesResult.length(); i++) {
                        JSONObject oneObject = solicitudesResult.getJSONObject(i);
                        solicitudesArray[i + 1] = oneObject.getString("id_folio");
                        index.add(oneObject.getString("id_solicitud"));
                    }
                    spinnerAdapterSolicitudes = new customSpinnerAdapter(updateSolicitud.this, solicitudesArray, index);
                    solicitudesSpinner.setAdapter(spinnerAdapterSolicitudes);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
package com.transporte.cicese.transportaciones_cicese.funciones;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.transporte.cicese.transportaciones_cicese.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by EFRA.LM on 16/10/2017.
 */

public class funcionesGeneradoras {
    private Context context;
    JSONObject resultado;
    ArrayList returnstm;
//save the context recievied via constructor in a local variable

    public funcionesGeneradoras(Context context){
        this.context=context;
    }
    //PARA INSTANCIAR clase:
    // funcionesGeneradoras fg = new funcionesGeneradoras(getApplicationContext());
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ArrayList functionPostRequest(String resource, JSONObject postDataParams) {
        returnstm=new ArrayList();
        try {
            URL url = new URL(context.getString(R.string.URI) +"/"+resource); // here is your URL path

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            returnstm.add(responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String line="";

                while((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
                returnstm.add(sb.toString());
                return returnstm;
            }
            else {
                return returnstm;
            }
        }
        catch(Exception e){
            if (returnstm.isEmpty()){
                returnstm.add(HttpsURLConnection.HTTP_UNAVAILABLE);
            }
            return returnstm;
        }
    }

    public ArrayList functionGetRequest(String resource, ArrayList fields, ArrayList values) {
        URL url = null; // here is your URL path
        returnstm = new ArrayList();
        try {
            String bundle=fieldsBuilder(fields,values);
            url = new URL(this.context.getString(R.string.URI)+"/"+resource+"?bundle="+bundle);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setDoInput(true);

            int responseCode=conn.getResponseCode();
            returnstm.add(responseCode);
            Log.i("Response",String.valueOf(responseCode));
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line="";

                while((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();


                returnstm.add(sb.toString());
                return returnstm;

            }
            else {
                return returnstm;
            }

        } catch(Exception e){
            if (returnstm.isEmpty()){
                returnstm.add(HttpsURLConnection.HTTP_UNAVAILABLE);
            }
            return returnstm;
        }

    }
    //Genera una cadena random de digitos de longitud 10
    public String generaFolio(){
        String caracteres="0123456789";
        String folio="";
        Random rand = new Random();
        int n;
        for (int i=0;i<8;i++){
            n = rand.nextInt(caracteres.length());
            folio=folio+caracteres.charAt(n);
        }
        return folio;
    }

    private String fieldsBuilder(ArrayList fields, ArrayList values){//Construimos la estructura de los parametros que se enviaran
        Map<String, String> builder = new HashMap<String, String>();
        for (int i=0;i<fields.size();i++){
            builder.put(fields.get(i).toString(),values.get(i).toString());
        }
        JSONObject obj=new JSONObject(builder);
        return obj.toString();
    }

    private String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}

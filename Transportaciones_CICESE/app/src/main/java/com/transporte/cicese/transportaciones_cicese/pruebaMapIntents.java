package com.transporte.cicese.transportaciones_cicese;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
/**
 * Created by hark_ on 20/11/2017.
 */

public class pruebaMapIntents extends AppCompatActivity {
    Button origenButton, vermap,destinoButton;
    TextView lat,lon;
    int PLACE_PICKER_REQUEST;
    String address,addressDestino;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_prueba);
        origenButton = (Button) findViewById(R.id.origenButton);
        destinoButton = (Button) findViewById(R.id.destinoButton);
        vermap = (Button) findViewById(R.id.verButton);
        lat = (TextView) findViewById(R.id.lat);
        lon = (TextView) findViewById(R.id.lon);

        origenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PLACE_PICKER_REQUEST = 1;
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(pruebaMapIntents.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                //Intent i = new Intent(RegistroSolicitudActivity.this,MapsActivity.class);
                //startActivityForResult(i,1);
            }
        });

        destinoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PLACE_PICKER_REQUEST = 2;
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(pruebaMapIntents.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                //Intent i = new Intent(RegistroSolicitudActivity.this,MapsActivity.class);
                //startActivityForResult(i,1);
            }
        });

        vermap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(pruebaMapIntents.this,MapsActivity.class);
                i.putExtra("origen",address);
                i.putExtra("destino",addressDestino);
                startActivity(i);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this,data);
                StringBuilder stBuilder = new StringBuilder();
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                this.address=address;
                lat.setText(latitude);
                lon.setText(longitude);
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this,data);
                String address = String.format("%s", place.getAddress());
                this.addressDestino=address;
            }
        }
    }

}

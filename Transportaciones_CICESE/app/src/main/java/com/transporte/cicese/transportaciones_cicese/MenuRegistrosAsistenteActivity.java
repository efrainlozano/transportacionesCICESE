package com.transporte.cicese.transportaciones_cicese;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MenuRegistrosAsistenteActivity extends AppCompatActivity {

    ImageButton rA,rC,rP,rS;
    Intent i;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_registros_asistente);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Referencia a los controles de la clase
        rC = (ImageButton) findViewById(R.id.imr1);
        rP = (ImageButton) findViewById(R.id.imr2);
        rA = (ImageButton) findViewById(R.id.imr3);
        rS = (ImageButton) findViewById(R.id.imr4);


        rC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(MenuRegistrosAsistenteActivity.this, RegistroChoferActivity.class);
                startActivityForResult(i,1);
            }
        });


        rP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(MenuRegistrosAsistenteActivity.this, RegistroPasajeroActivity.class);
                startActivityForResult(i,2);
            }
        });


        rA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(MenuRegistrosAsistenteActivity.this, RegistroAsistenteActivity.class);
                startActivityForResult(i,3);
            }
        });


        rS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(MenuRegistrosAsistenteActivity.this, RegistroSolicitudActivity.class);
                startActivityForResult(i,4);
            }
        });
    }
}

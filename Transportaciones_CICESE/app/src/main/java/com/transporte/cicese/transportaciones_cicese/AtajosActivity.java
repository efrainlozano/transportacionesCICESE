package com.transporte.cicese.transportaciones_cicese;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class AtajosActivity extends AppCompatActivity {

    Button Registros,Consultas,Updates;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atajos);

        Registros = (Button) findViewById(R.id.irARegistros);
        Consultas = (Button) findViewById(R.id.irAConsultas);
        Updates = (Button) findViewById(R.id.irAUpdate);

        Registros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(AtajosActivity.this, MenuRegistrosAsistenteActivity.class);
                startActivity(i);
            }
        });

        Consultas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(AtajosActivity.this, ServiciosActivity.class);
                startActivity(i);
            }
        });

        Updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(AtajosActivity.this, updateSolicitud.class);
                startActivity(i);
            }
        });
    }
}

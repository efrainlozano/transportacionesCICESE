package com.transporte.cicese.transportaciones_cicese;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TextoRapidoActivity extends AppCompatActivity {

    Button tr1,tr2,tr3,tr4,tr5;
    Intent i;
    Boolean select = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texto_rapido);

        //Referencia a los botones de la clase
        tr1 = (Button) findViewById(R.id.tr_1);
        tr2 = (Button) findViewById(R.id.tr_2);
        tr3 = (Button) findViewById(R.id.tr_3);
        tr4 = (Button) findViewById(R.id.tr_4);
        tr5 = (Button) findViewById(R.id.tr_5);


        //Cuando se selecciona el tr1
        tr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(TextoRapidoActivity.this, chatActivity.class);
                select = true;
               i.putExtra("selecto",select);
               //i.putExtra("textoR_1",tr1.getText().toString());
                startActivity(i);
            }
        });



        //Cuando se selecciona el tr1
        tr2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(TextoRapidoActivity.this, chatActivity.class);
                select = true;
                i.putExtra("selecto",select);
               //  i.putExtra("textoR_1",tr2.getText().toString());
                startActivity(i);
            }
        });



        //Cuando se selecciona el tr1
        tr3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(TextoRapidoActivity.this, chatActivity.class);
                select = true;
                i.putExtra("selecto",select);
             //    i.putExtra("textoR_1",tr3.getText().toString());
                startActivity(i);
            }
        });


        //Cuando se selecciona el tr1
        tr4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(TextoRapidoActivity.this, chatActivity.class);
                select = true;
                i.putExtra("selecto",select);
                // i.putExtra("textoR_1",tr4.getText().toString());
                startActivity(i);
            }
        });


        //Cuando se selecciona el tr1
        tr5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(TextoRapidoActivity.this, chatActivity.class);
                select = true;
                i.putExtra("selecto",select);
                // i.putExtra("textoR_1",tr5.getText().toString());
                startActivity(i);
            }
        });
    }
}

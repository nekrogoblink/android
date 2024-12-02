package com.example.pruebaandroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public void ConexionAMQTT(View view){
        Intent intent = new Intent(this, MqttActivity.class);
        startActivity(intent);
    }

    ;
    private EditText txtRut, txtNombre, txtObservaciones;
    private ListView lista;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CargarListaFirestore();
        db = FirebaseFirestore.getInstance();
        txtRut = findViewById(R.id.txtRut);
        txtNombre = findViewById(R.id.txtNombre);
        txtObservaciones = findViewById(R.id.txtObservaciones);
        lista = findViewById(R.id.lista);
    }

    public void enviarDatosFirestore(View view)
    {
        String rut = txtRut.getText().toString();
        String nombre = txtNombre.getText().toString();
        String observaciones = txtObservaciones.getText().toString();

        Map<String, Object> paciente = new HashMap <>();
        paciente.put("rut", rut);
        paciente.put("nombre", nombre);
        paciente.put("observaciones", observaciones);

        db.collection("pacientes")
                .document(rut)
                .set(paciente)
                .addOnSuccessListener(aVoid ->
                {
                    Toast.makeText(MainActivity.this, "Datos enviados a Firestore", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e ->
                {
                    Toast.makeText(MainActivity.this, "Error al enviar datos" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    public void CargarLista(View view)
    {
        CargarListaFirestore();
    }
    public void CargarListaFirestore()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pacientes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            List<String> listaPacientes = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                String linea = "--" + document.getString("rut") + "--" + document.getString("nombre") + "--" + document.getString("observaciones");
                                listaPacientes.add(linea);
                            }
                            ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                                    MainActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    listaPacientes
                            );
                            lista.setAdapter(adaptador);
                        } else
                        {
                            Log.e("TAG", "ERROR AL OBTENER DATOS", task.getException());
                        }
                    }
                });
    };
}
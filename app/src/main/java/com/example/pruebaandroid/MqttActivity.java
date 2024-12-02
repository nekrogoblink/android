package com.example.pruebaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.security.PrivateKey;

public class MqttActivity extends AppCompatActivity {

    private static String mqttHost = "tcp://sugarsoarer136.cloud.shiftr.io:1883";
    private static String IdUsuario = "AppAndroid";

    private static String Topico = "Datos paciente";
    private static String User = "sugarsoarer136";
    private static String Pass = "rdXexUNfId6YfLau";

    private TextView textView;
    private EditText editTextMessage;
    private Button botonEnvio;
    private MqttClient mqttClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt);


        textView = findViewById(R.id.textView);
        editTextMessage = findViewById(R.id.txtMensaje);
        botonEnvio = findViewById(R.id.botonEnvioMensaje);
        try {
            mqttClient = new MqttClient(mqttHost, IdUsuario, null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(User);
            options.setPassword(Pass.toCharArray());
            mqttClient.connect(options);
            Toast.makeText(this, "Aplicación conectada al Servidor MQTT", Toast.LENGTH_SHORT).show();
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTT", "CONEXIÓN PERDIDA");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload());
                    runOnUiThread(() -> textView.setText(payload));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT", "ENTREGA COMPLETA");
                }
            });
        }catch (MqttException e)
        {
            e.printStackTrace();
        }

        botonEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensaje = editTextMessage.getText().toString();
                try {
                    if (mqttClient !=null && mqttClient.isConnected())
                    {
                        mqttClient.publish(Topico, mensaje.getBytes(), 0, false);
                        textView.append("\n" + mensaje);
                        Toast.makeText(MqttActivity.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MqttActivity.this, "Error: No se puede enviar el mensaje. La conexión MQTT no está activa", Toast.LENGTH_SHORT).show();
                    }
                } catch (MqttException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
package com.vm.mqtt_camclient

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import com.vm.mqtt_camclient.ui.theme.MQTT_CamClientTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

lateinit var mqttClient: MqttAndroidClient

val _jpgImage = MutableStateFlow<Bitmap?>(null)
val jpgImage: StateFlow<Bitmap?> = _jpgImage.asStateFlow()


class MainActivity : ComponentActivity() {


//    private val myBroadcastReceiver = MyBroadcastReceiver()
    lateinit var bitmap: Bitmap



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        applicationContext.registerReceiver(broadcastReceiver, intentFilter, RECEIVER_EXPORTED);

        mqttConnect(applicationContext, "mqtt.eclipseprojects.io",
            "ExampleClientPub_378", "")

        enableEdgeToEdge()
        setContent {
            MQTT_CamClientTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)){
                        Column {
                            Greeting(
                                name = "Android",
                                modifier = Modifier.padding(innerPadding)
                            )

                            BitmapImage(jpgImage.collectAsState().value)
                        }
                    }
                }
            }
        }
    }

    fun mqttConnect(applicationContext: Context, brokeraddr: String, clientuser: String, clientpwd: String) {
        // ClientId is a unique id used to identify a client
        val clientId = MqttClient.generateClientId()


        // Create an MqttAndroidClient instance
        mqttClient = MqttAndroidClient ( applicationContext, "tcp://$brokeraddr", clientId )


        // ConnectOption is used to specify username and password
        val connOptions = MqttConnectOptions()
        connOptions.userName = clientuser
        connOptions.password = clientpwd.toCharArray()


        try {
            // Perform connection
            mqttClient.connect(connOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken)                        {
                    // Add here code executed in case of successful connection
                    Log.d("MQTT_D", "Connected!")
                    mqttSetReceiveListener()
                    mqttSubscribe("MQTT Examples cam", 1)
                }override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Add here code executed in case of failed connection
                    Log.d("MqttClient", "Connection failed")
                }
            })
        } catch (e: MqttException) {
            // Get stack trace
            Log.d("MQTT_D", "Exc $e")
            e.printStackTrace()
        }
    }

    fun mqttSetReceiveListener() {
        mqttClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                // Connection Lost
            }
            override fun messageArrived(topic: String, message: MqttMessage) {
                // A message has been received

                _jpgImage.value = BitmapFactory.decodeByteArray(message.payload, 0, message.payload.size )

                val data = String(message.payload, charset("UTF-8"))
                // Place the message into a specific TextBox object
//                editTextRcvMsg.editText!!.setText(data)
                Log.d("MQTT_D", "$topic $data")
            }
            override fun deliveryComplete(token: IMqttDeliveryToken) {
                // Delivery complete
            }
        })
    }

    fun mqttSubscribe(topic: String, qos: Int) {
        try {
            mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Successful subscribe
                }override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Failed subscribe
                }
            })
        } catch (e: MqttException) {
            // Check error
        }
    }

    fun mqttPublish(topic: String, msg: String, qos: Int) {
        try {
            val mqttMessage = MqttMessage(msg.toByteArray(charset("UTF-8")))
            mqttMessage.qos = qos
            mqttMessage.isRetained = false
            // Publish the message
            mqttClient.publish(topic, mqttMessage)
        } catch (e: Exception) {
            // Check exception
        }
    }

    fun mqttUnsubscribe(topic: String) {
        try {
            // Unsubscribe from topic
            mqttClient.unsubscribe(topic, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Successful unsubscribe
                }override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Failed unsubscribe
                }
            })
        } catch (e: MqttException) {
            // Check exception
        }
    }

    fun mqttDisconnect() {
        try {
            mqttClient.disconnect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    //Successful disconnection
                }
                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    //Failed disconnection
                }
            })
        } catch (e: MqttException) {
            // Check exception
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun BitmapImage(bitmap: Bitmap?) {
    if(bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "some useful description",
        )
    }else{
        Text("No bitmap")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MQTT_CamClientTheme {
        Greeting("Android")

    }
}
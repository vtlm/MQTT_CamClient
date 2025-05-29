package com.vm.mqtt_camclient.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
import javax.inject.Inject

@HiltViewModel
class AppViewModel  @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @ApplicationContext val context: Context
): ViewModel() {

    private lateinit var mqttClient: MqttAndroidClient

    val _jpgImage = MutableStateFlow<Bitmap?>(null)
    val jpgImage: StateFlow<Bitmap?> = _jpgImage.asStateFlow()


    init{
        mqttConnect(context, "mqtt.eclipseprojects.io",
            "ExampleClientPub_378", "")

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
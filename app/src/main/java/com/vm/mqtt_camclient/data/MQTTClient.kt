package com.vm.mqtt_camclient.data

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import timber.log.Timber

class MQTTClient (
    context: Context,
    brokeraddr: String,
    clientuser: String,
    clientpwd: String,
    private val topicsList: List<Pair<String, Int>>,
    val receivedMessageHandler: (topic: String, message: MqttMessage) -> Unit) {

    private lateinit var mqttClient: MqttAndroidClient

    var isConnected = false

    init{
        mqttConnect(context, brokeraddr, clientuser, clientpwd)
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
                override fun onSuccess(asyncActionToken: IMqttToken){
                    // Add here code executed in case of successful connection
                    Timber.tag("MQTT_D").d("Connected!")
                    mqttSetReceiveListener()

                    topicsList.forEach {
                        val (topic, qos) = it
                        mqttSubscribe(topic, qos)
                    }
                    isConnected = true
                }
                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Add here code executed in case of failed connection
                    Timber.tag("MqttClient").d("Connection failed")
                }
            })
        } catch (e: MqttException) {
            // Get stack trace
            Timber.tag("MQTT_D").d("Exc $e")
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

                receivedMessageHandler(topic, message)
//                _jpgImage.value = BitmapFactory.decodeByteArray(message.payload, 0, message.payload.size )

                val data = String(message.payload, charset("UTF-8"))
                // Place the message into a specific TextBox object
//                editTextRcvMsg.editText!!.setText(data)
//                Log.d("MQTT_D", "$topic $data")
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
            Log.e("ERRP","publish error");
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
package com.vm.mqtt_camclient.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttMessage

class MQTTCameraClient (
    context: Context,
    private val clientuser: String
) {

//    @Inject
//    lateinit var userPreferencesRepository: UserPreferencesRepository


    private var mqttClient: MQTTClient = MQTTClient(context,
        "mqtt.eclipseprojects.io",
         clientuser,
        "",
        listOf(Pair("MQTT Examples cam",1)),
        ::receivedMessageHandler)
   // @ApplicationContext val context: Context

    val _jpgImage = MutableStateFlow<Bitmap?>(null)
    val jpgImage: StateFlow<Bitmap?> = _jpgImage.asStateFlow()

    init{
        GlobalScope.launch {
            while (!mqttClient.isConnected) {
                delay(100)
            }
//            mqttClient.mqttPublish("Cmd", "getFrame", 1)
        }
    }

    private fun receivedMessageHandler(topic: String, message: MqttMessage){
        _jpgImage.value = BitmapFactory.decodeByteArray(message.payload, 0, message.payload.size )
        mqttClient.mqttPublish("Cmd","getFrame",1)
    }

}


//class MQTTCameraClient @Inject constructor(
//    private val clientId: String,
//    private val userPreferencesRepository: UserPreferencesRepository,
//    private val mqttClient: MQTT_Client,
//    @ApplicationContext val context: Context
//) {
//    val _jpgImage = MutableStateFlow<Bitmap?>(null)
//    val jpgImage: StateFlow<Bitmap?> = _jpgImage.asStateFlow()
//
//}
package com.vm.mqtt_camclient.data

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AppViewModel  @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @ApplicationContext val context: Context
): ViewModel() {

    val mqttCameraClient: MQTTCameraClient = MQTTCameraClient(context,"ExampleClientPub_378")

}
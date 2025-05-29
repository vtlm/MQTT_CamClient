package com.vm.mqtt_camclient.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MQTT_CameraClient @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @ApplicationContext val context: Context

) {

}
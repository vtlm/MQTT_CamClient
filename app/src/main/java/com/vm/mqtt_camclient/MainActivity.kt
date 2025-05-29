package com.vm.mqtt_camclient

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.vm.mqtt_camclient.data.AppViewModel
import com.vm.mqtt_camclient.ui.theme.MQTT_CamClientTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

//    private val myBroadcastReceiver = MyBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        applicationContext.registerReceiver(broadcastReceiver, intentFilter, RECEIVER_EXPORTED);

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

//                            BitmapImage(appViewModel.jpgImage.collectAsState().value)
                        }
                    }
                }
            }
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
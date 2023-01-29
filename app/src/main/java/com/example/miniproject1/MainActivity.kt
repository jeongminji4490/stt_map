package com.example.miniproject1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.firstttsproject.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.*


class MainActivity : AppCompatActivity() {

    private var speechRecognizer: SpeechRecognizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        setContent {
            Main()
        }
    }

    @Composable
    fun Main() {
        var text by rememberSaveable { mutableStateOf("") }
        val seoul = LatLng(37.532600, 127.024612)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(seoul, 10f)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = {
                    val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                    speechRecognizer?.startListening(speechRecognizerIntent)

                    speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(bundle: Bundle) {}
                        override fun onBeginningOfSpeech() {
                        }

                        override fun onRmsChanged(v: Float) {}
                        override fun onBufferReceived(bytes: ByteArray) {}
                        override fun onEndOfSpeech() {}
                        override fun onError(i: Int) {
                            Log.e("onError", i.toString())
                        }

                        override fun onResults(bundle: Bundle) {
                            val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            text = data!![0]
                        }

                        override fun onPartialResults(bundle: Bundle) {}
                        override fun onEvent(i: Int, bundle: Bundle) {}
                    })
                },
                Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(R.color.green),
                    contentColor = White)
            ) {
                Text(text = "Start")
            }
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = text,
                onValueChange = { text = it },
                Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = White,
                    textColor = colorResource(R.color.green)
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth(),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(position = seoul),
                    title = "Singapore",
                    snippet = "Marker in Singapore"
                )
            }
        }
    }

    private fun requestPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
        }
    }
}
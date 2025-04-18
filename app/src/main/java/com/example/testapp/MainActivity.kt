package com.example.testapp

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.Manifest.permission.SEND_SMS
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.example.testapp.auth.FirebaseAuthClient
import com.example.testapp.ui.theme.MealMateTheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

val LocalGoogleAuthUiClient: ProvidableCompositionLocal<FirebaseAuthClient> = compositionLocalOf<FirebaseAuthClient> {
    error("GoogleAuthUiClient not provided")
}

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        FirebaseAuthClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissionLauncher.launch(
                arrayOf(
                    READ_MEDIA_IMAGES,
                    READ_MEDIA_VIDEO,
                    READ_MEDIA_VISUAL_USER_SELECTED,
                    SEND_SMS
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, SEND_SMS))
        } else {
            requestPermissionLauncher.launch(arrayOf(READ_EXTERNAL_STORAGE, SEND_SMS))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalGoogleAuthUiClient provides googleAuthUiClient) {
                MealMateTheme {
                    RouteController(
                        googleAuthUiClient,
                        lifecycleOwner = this, // Pass the Activity instance as LifecycleOwner
                        context = applicationContext, // Pass the applicationContext
                        db = db
                    )
                }
            }
        }

        // Request media permissions
        checkAndRequestPermissions()
    }
}

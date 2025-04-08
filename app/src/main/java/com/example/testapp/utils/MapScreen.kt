package com.example.testapp.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap;
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun GoogleMapBox(
    coordinates: LatLng = LatLng(27.7172, 85.3240),
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onLocationSelected: ((LatLng) -> Unit)? = null,
    showOnlyMarker: Boolean = false
) {
    val defaultLocation = coordinates // Default to Kathmandu
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

    var isMapLoaded by remember { mutableStateOf(false) }
    var markerPosition by remember { mutableStateOf(coordinates) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .padding(paddingValues)
            .clip(RoundedCornerShape(9.dp))
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                if (!showOnlyMarker && onLocationSelected != null) {
                    markerPosition = latLng
                    onLocationSelected(latLng)
                }
            },
            onMapLoaded = { isMapLoaded = true },
            properties = if (showOnlyMarker) {
                MapProperties(
                    isMyLocationEnabled = false,
                    mapType = MapType.NORMAL,
                )
            } else {
                MapProperties()
            }
        ) {
            Marker(
                state = MarkerState(position = markerPosition),
                title = "Selected Location"
            )
        }
    }
}

suspend fun getPlaceNameFromCoordinates(latLng: LatLng): String {
    val url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=${latLng.latitude}&lon=${latLng.longitude}&zoom=18&addressdetails=1"

    return withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            // Important: Set a User-Agent as Nominatim requires it
            connection.setRequestProperty("User-Agent", "YourAppName")

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(response)

            jsonObject.getString("display_name")
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}


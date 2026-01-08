package s3521330manikantareddy.teesproject.geobasedreminderapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import s3521330manikantareddy.teesproject.geobasedreminderapp.geofence.GeoReminderService
//import s3521330manikantareddy.teesproject.geobasedreminderapp.geofence.GeoReminderService
import s3521330manikantareddy.teesproject.geobasedreminderapp.geofence.GeofenceHelper
import s3521330manikantareddy.teesproject.geobasedreminderapp.geofence.ReminderViewModel
import s3521330manikantareddy.teesproject.geobasedreminderapp.room.ReminderEntity
import s3521330manikantareddy.teesproject.geobasedreminderapp.ui.theme.MainBGColor
import java.util.Locale
import kotlin.jvm.java

suspend fun fetchAddress(context: Context, latLng: LatLng): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val result = withContext(Dispatchers.IO) {
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        }
        result?.firstOrNull()?.getAddressLine(0) ?: "Unknown Location"
    } catch (e: Exception) {
        "Unable to fetch address"
    }
}

fun isGpsEnabled(context: Context): Boolean {
    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    return try {
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } catch (e: Exception) {
        false
    }
}


fun hasFinePermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun hasBackgroundPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else true
}


@Composable
fun BackgroundPermissionDialog(
    onDismiss: () -> Unit,
    onRequestFine: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onRequestFine()
                onDismiss()
            }) {
                Text("Allow")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text("Allow Background Location?")
        },
        text = {
            Text(
                "To trigger geo-reminders even when the app is closed, we need Background Location permission.\n\n" +
                        "We only use your location to check when you enter or exit saved reminder areas."
            )
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun SetReminderScreen(
    viewModel: ReminderViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var reminderTitle by remember { mutableStateOf("") }
    var reminderMessage by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var reminderType by remember { mutableStateOf("Arrive") }
    var radius by remember { mutableStateOf(200f) }
    var addressText by remember { mutableStateOf("") }
    var isAddressLoading by remember { mutableStateOf(false) }

    val serviceIntent = Intent(context, GeoReminderService::class.java)


    var showPermissionDialog by remember { mutableStateOf(false) }

    val backgroundLocationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(context, "Background location granted.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                context,
                "Background location is required for geofencing.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val fineLocationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                ContextCompat.startForegroundService(context, serviceIntent)
                Toast.makeText(context, "Location permission granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(
                context,
                "Fine location permission is required to use geofencing.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(
                context,
                "Notification permission is required to show reminders",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(55.3781, -3.4360),
            5.5f
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Set Reminder", fontSize = 22.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainBGColor,
                    titleContentColor = Color.White,
                )
            )
        }
    ) { innerPadding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                val uiSettings = remember { MapUiSettings(zoomControlsEnabled = true) }
                val properties = remember { MapProperties(isMyLocationEnabled = false) }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = properties,
                    uiSettings = uiSettings,
                    onMapClick = { latLng ->
                        selectedLocation = latLng
                        isAddressLoading = true


                        coroutineScope.launch {

                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(latLng, 16f),
                                durationMs = 800
                            )

                            addressText = fetchAddress(context, latLng)
                            isAddressLoading = false
                        }
                    }
                ) {
                    selectedLocation?.let { latLng ->
                        Marker(
                            state = MarkerState(position = latLng),
                            title = "Selected Location"
                        )
                        Circle(
                            center = latLng,
                            radius = radius.toDouble(),
                            fillColor = Color(0x332196F3),
                            strokeColor = Color(0xFF2196F3),
                            strokeWidth = 3f
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                if (selectedLocation != null) {
                    if (isAddressLoading) {
                        Text(
                            "Fetching address...",
                            color = Color.Gray,
                            fontStyle = FontStyle.Italic
                        )
                    } else {
                        Text("Address : $addressText", fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (selectedLocation != null) {
                    Text("Radius: ${radius.toInt()} meters", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Slider(
                        value = radius,
                        onValueChange = { radius = it },
                        valueRange = 100f..500f,
                        steps = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = reminderTitle,
                    onValueChange = { reminderTitle = it },
                    label = { Text("Reminder Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = reminderMessage,
                    onValueChange = { reminderMessage = it },
                    label = { Text("Reminder Message") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Trigger Type", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TriggerChip("Arrive", reminderType) { reminderType = it }
                    TriggerChip("Leave", reminderType) { reminderType = it }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        // Validate
                        if (selectedLocation == null) {
                            Toast.makeText(
                                context,
                                "Please select location on map",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        if (reminderTitle.isBlank()) {
                            Toast.makeText(
                                context,
                                "Please enter reminder title",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        if (!hasNotificationPermission(context)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            return@Button
                        }

                        val fineGranted = hasFinePermission(context)
                        val bgGranted = hasBackgroundPermission(context)

                        when {
                            !fineGranted -> {
                                fineLocationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }

                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !bgGranted -> {
                                showPermissionDialog = true
                            }

                            else -> {
                                val reminder = ReminderEntity(
                                    id = System.currentTimeMillis().toString(),
                                    title = reminderTitle.trim(),
                                    message = reminderMessage.trim(),
                                    latitude = selectedLocation!!.latitude,
                                    longitude = selectedLocation!!.longitude,
                                    address = addressText,
                                    radius = radius,
                                    triggerType = reminderType
                                )

                                viewModel.addReminder(reminder) { success ->
                                    if (success) {
                                        Toast.makeText(
                                            context,
                                            "Reminder saved!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Failed to save reminder!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                coroutineScope.launch {
                                    registerGeofence(context, reminder)

                                    ContextCompat.startForegroundService(
                                        context,
                                        Intent(context, GeoReminderService::class.java)
                                    )


                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Reminder", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

    }

    if (showPermissionDialog) {
        BackgroundPermissionDialog(
            onDismiss = { showPermissionDialog = false },
            onRequestFine = {
                fineLocationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        )
    }
}

@Composable
fun TriggerChip(
    text: String,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    val selected = text == selectedValue

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFFE75959) else Color(0xFFF0F0F0),
        modifier = Modifier
            .clickable { onSelect(text) }
            .padding(vertical = 4.dp)
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) Color.White else Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

fun registerGeofence(context: Context, reminder: ReminderEntity) {
    val geofenceHelper = GeofenceHelper(context)
    val geofencingClient = LocationServices.getGeofencingClient(context)

    val transitionType = if (reminder.triggerType == "Arrive")
        Geofence.GEOFENCE_TRANSITION_ENTER
    else
        Geofence.GEOFENCE_TRANSITION_EXIT

    val geofence = geofenceHelper.getGeofence(
        reminder.id,
        reminder.latitude,
        reminder.longitude,
        reminder.radius,
        transitionType
    )

    val request = geofenceHelper.getGeofencingRequest(geofence)
    val pendingIntent = geofenceHelper.getPendingIntent()

    if (!hasBackgroundPermission(context)) {
        Toast.makeText(context, "Please allow Background Location for geofence", Toast.LENGTH_LONG)
            .show()
        return
    }

    geofencingClient.addGeofences(request, pendingIntent)
        .addOnSuccessListener {
            Toast.makeText(context, "Geofence registered", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            e.printStackTrace()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)

            Log.e("SetReminderScreen", "Failed to register geofence", e)
            Toast.makeText(context, "Failed to register geofence", Toast.LENGTH_LONG).show()
        }
}

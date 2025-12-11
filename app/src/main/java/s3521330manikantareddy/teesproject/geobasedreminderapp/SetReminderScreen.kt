package s3521330manikantareddy.teesproject.geobasedreminderapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import s3521330manikantareddy.teesproject.geobasedreminderapp.geofence.GeoReminderService
import s3521330manikantareddy.teesproject.geobasedreminderapp.geofence.GeofenceHelper
import s3521330manikantareddy.teesproject.geobasedreminderapp.geofence.ReminderViewModel
import s3521330manikantareddy.teesproject.geobasedreminderapp.room.ReminderEntity
import java.util.Locale

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

    // UI state
    var reminderTitle by remember { mutableStateOf("") }
    var reminderMessage by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var reminderType by remember { mutableStateOf("Arrive") }
    var radius by remember { mutableStateOf(200f) }
    var addressText by remember { mutableStateOf("") }
    var isAddressLoading by remember { mutableStateOf(false) }

    val activity = context as Activity
    val serviceIntent = Intent(context, GeoReminderService::class.java)
    ContextCompat.startForegroundService(context, serviceIntent)


    // permission dialog state
    var showPermissionDialog by remember { mutableStateOf(false) }

    val backgroundLocationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(context, "Background location granted.", Toast.LENGTH_SHORT).show()
        } else {
            // If denied permanently you may want to guide the user to settings
            Toast.makeText(context, "Background location is required for geofencing.", Toast.LENGTH_LONG).show()
        }
    }

    // permission launchers
    val fineLocationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // request background (Android Q+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                Toast.makeText(context, "Location permission granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Fine location permission is required to use geofencing.", Toast.LENGTH_LONG).show()
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Notification permission is required to show reminders", Toast.LENGTH_LONG).show()
        }
    }



    // camera state for map
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.5937, 78.9629), 4f)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Fixed top map
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

                    // launch suspend fetch from coroutine scope
                    coroutineScope.launch {
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

        // Scrollable content below map
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Address display
            if (selectedLocation != null) {
                if (isAddressLoading) {
                    Text("Fetching address...", color = Color.Gray, fontStyle = FontStyle.Italic)
                } else {
                    Text(addressText, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Radius slider
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

            // Title
            OutlinedTextField(
                value = reminderTitle,
                onValueChange = { reminderTitle = it },
                label = { Text("Reminder Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Message
            OutlinedTextField(
                value = reminderMessage,
                onValueChange = { reminderMessage = it },
                label = { Text("Reminder Message") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Trigger chips
            Text("Trigger Type", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TriggerChip("Arrive", reminderType) { reminderType = it }
                TriggerChip("Leave", reminderType) { reminderType = it }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save button with permission flow
            Button(
                onClick = {
                    // Validate
                    if (selectedLocation == null) {
                        Toast.makeText(context, "Please select location on map", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (reminderTitle.isBlank()) {
                        Toast.makeText(context, "Please enter reminder title", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!hasNotificationPermission(context)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        return@Button
                    }

                    // Check permissions: fine + background (if needed)
                    val fineGranted = hasFinePermission(context)
                    val bgGranted = hasBackgroundPermission(context)

                    when {
                        !fineGranted -> {
                            // Request fine location (dialog will chain to background request)
                            fineLocationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !bgGranted -> {
                            // show dialog explaining why and then request
                            showPermissionDialog = true
                        }
                        else -> {
                            // all permissions ok -> save & register
                            val reminder = ReminderEntity(
                                id = System.currentTimeMillis().toString(),
                                title = reminderTitle.trim(),
                                message = reminderMessage.trim(),
                                latitude = selectedLocation!!.latitude,
                                longitude = selectedLocation!!.longitude,
                                radius = radius,
                                triggerType = reminderType
                            )

                            // Save to Room via ViewModel
                            viewModel.addReminder(reminder) { success ->
                                if (success) {
                                    Toast.makeText(context, "Reminder saved!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to save reminder!", Toast.LENGTH_SHORT).show()
                                }
                            }

                            // Register geofence (in background coroutine)
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

    if (showPermissionDialog) {
        BackgroundPermissionDialog(
            onDismiss = { showPermissionDialog = false },
            onRequestFine = {
                // request fine first; the fine launcher will request background afterwards
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

    // ensure background permission is granted before registering
    if (!hasBackgroundPermission(context)) {
        Toast.makeText(context, "Please allow Background Location for geofence", Toast.LENGTH_LONG).show()
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

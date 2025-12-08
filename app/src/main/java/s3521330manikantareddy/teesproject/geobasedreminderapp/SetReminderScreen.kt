package s3521330manikantareddy.teesproject.geobasedreminderapp

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
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


@Preview(showBackground = true)
@Composable
fun SetReminderScreenPreview() {
    SetReminderScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun SetReminderScreen() {
    val context = LocalContext.current

    var reminderTitle by remember { mutableStateOf("") }
    var reminderMessage by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var reminderType by remember { mutableStateOf("Arrive") }
    var radius by remember { mutableStateOf(200f) }
    var addressText by remember { mutableStateOf("") }
    var isAddressLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // camera
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.5937, 78.9629), 4f)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Fixed top map (won't scroll)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp), // fixed height - adjust as needed
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
                    // update selected location immediately
                    selectedLocation = latLng
                    isAddressLoading = true

                    // launch suspend fetch from composition scope
                    coroutineScope.launch {
                        addressText = fetchAddress(context, latLng)
                        isAddressLoading = false
                    }
                }
            ) {
                // show marker + circle if location selected
                selectedLocation?.let { latLng ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = "Selected Location"
                    )
                    Circle(
                        center = latLng,
                        radius = radius.toDouble(), // meters
                        fillColor = Color(0x332196F3),     // semi-transparent (change color if you want)
                        strokeColor = Color(0xFF2196F3),
                        strokeWidth = 3f
                    )
                }
            }
        }

        // Scrollable content below the fixed map
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp) // spacing around content
        ) {
            // Address / Loading
            if (selectedLocation != null) {
                if (isAddressLoading) {
                    Text("Fetching address...", color = Color.Gray, fontStyle = FontStyle.Italic)
                } else {
                    Text(addressText, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Radius slider (visible only after selecting location)
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

            // Title field
            OutlinedTextField(
                value = reminderTitle,
                onValueChange = { reminderTitle = it },
                label = { Text("Reminder Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Message field
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

            // Save button
            Button(
                onClick = {
                    if (selectedLocation != null && reminderTitle.isNotBlank()) {

                        Toast.makeText(context, "Reminder saved!", Toast.LENGTH_SHORT).show()

                        onSaveReminder(
                            context = context,
                            title = reminderTitle.trim(),
                            location = selectedLocation!!,
                            type = reminderType,
                            message = reminderMessage.trim(),
                            radius = radius,
                            address = addressText
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Reminder", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(40.dp)) // bottom spacing so button isn't flush to bottom
        }
    }
}

// -------------------------- CHIP COMPONENT --------------------------
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

data class GeoReminder(
    val reminderId: String = "",
    val title: String = "",
    val message: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radius: Float = 200f,
    val triggerType: String = "", // Arrive / Leave
    val address: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

fun onSaveReminder(
    context: Context,
    title: String,
    location: LatLng,
    type: String,
    message: String,
    radius: Float,
    address: String
) {

    val reminder = GeoReminder(
        title = title,
        message = message,
        latitude = location.latitude,
        longitude = location.longitude,
        radius = radius,
        triggerType = type,
        address = address
    )

    saveReminderToFirebase(context, reminder) { success, msg ->
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}


fun saveReminderToFirebase(
    context: Context,
    reminder: GeoReminder,
    onResult: (Boolean, String) -> Unit
) {
    try {
        val email = UserPrefs.getEmail(context)
        if (email.isBlank()) {
            onResult(false, "User not logged in")
            return
        }

        val safeEmail = email.replace(".", "_")
        val ref = FirebaseDatabase.getInstance().getReference("Reminders/$safeEmail")

        val reminderId = ref.push().key ?: System.currentTimeMillis().toString()

        val reminderData = reminder.copy(reminderId = reminderId)

        ref.child(reminderId).setValue(reminderData)
            .addOnSuccessListener { onResult(true, "Reminder saved successfully") }
            .addOnFailureListener { onResult(false, it.message ?: "Unknown error") }

    } catch (e: Exception) {
        onResult(false, e.message ?: "Unexpected error")
    }
}

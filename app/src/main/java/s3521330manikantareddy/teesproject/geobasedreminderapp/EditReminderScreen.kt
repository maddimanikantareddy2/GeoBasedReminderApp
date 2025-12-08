package s3521330manikantareddy.teesproject.geobasedreminderapp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReminderScreen(
    reminder: GeoReminder,
    onBack: () -> Unit,
    onUpdateDone: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State fields prefilled
    var title by remember { mutableStateOf(reminder.title) }
    var message by remember { mutableStateOf(reminder.message) }
    var radius by remember { mutableStateOf(reminder.radius) }
    var triggerType by remember { mutableStateOf(reminder.triggerType) }
    var address by remember { mutableStateOf(reminder.address) }

    var loading by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Reminder") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        if (loading) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {},
                text = { Text("Updating reminder…") }
            )
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Reminder Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Radius Slider
            Text("Radius: ${radius.toInt()} meters")
            Slider(
                value = radius,
                onValueChange = { radius = it },
                valueRange = 100f..500f,
                steps = 3
            )

            // Trigger Type Toggle
            Text("Trigger Type", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TriggerChip("Arrive", triggerType) { triggerType = it }
                TriggerChip("Leave", triggerType) { triggerType = it }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    loading = true
                    showMessage = ""

                    val updated = reminder.copy(
                        title = title.trim(),
                        message = message.trim(),
                        radius = radius,
                        triggerType = triggerType,
                        address = address.trim()
                    )

                    scope.launch {
                        val success = updateReminder(context, updated)
                        loading = false

                        if (success) {
                            Toast.makeText(context, "Reminder updated", Toast.LENGTH_SHORT).show()
                            onUpdateDone()
                        } else {
                            showMessage = "Failed to update reminder"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Update Reminder")
            }

            if (showMessage.isNotEmpty()) {
                Text(showMessage, color = Color.Red)
            }
        }
    }
}

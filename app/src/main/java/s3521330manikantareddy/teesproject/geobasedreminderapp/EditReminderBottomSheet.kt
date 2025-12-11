package s3521330manikantareddy.teesproject.geobasedreminderapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import s3521330manikantareddy.teesproject.geobasedreminderapp.room.ReminderEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReminderBottomSheet(
    reminder: ReminderEntity,
    onDismiss: () -> Unit,
    onUpdate: (ReminderEntity) -> Unit,
    onDelete: (ReminderEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    var title by remember { mutableStateOf(reminder.title) }
    var message by remember { mutableStateOf(reminder.message) }
    var radius by remember { mutableStateOf(reminder.radius) }
    var triggerType by remember { mutableStateOf(reminder.triggerType) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        tonalElevation = 3.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {

            // ----- HEADER -----
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Edit Reminder",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ----- TITLE FIELD -----
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Reminder Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ----- MESSAGE FIELD -----
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ----- TRIGGER TYPE -----
            Text("Trigger Type", fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TriggerChip("Arrive", triggerType) { triggerType = it }
                TriggerChip("Leave", triggerType) { triggerType = it }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ----- RADIUS SLIDER -----
            Text("Radius: ${radius.toInt()} meters", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value = radius,
                onValueChange = { radius = it },
                valueRange = 100f..500f,
                steps = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(25.dp))

            // ----- SAVE BUTTON -----
            Button(
                onClick = {
                    val updated = reminder.copy(
                        title = title,
                        message = message,
                        triggerType = triggerType,
                        radius = radius
                    )
                    onUpdate(updated)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Update Reminder", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ----- DELETE BUTTON -----
            OutlinedButton(
                onClick = {
                    onDelete(reminder)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                )
            ) {
                Text("Delete Reminder", fontSize = 16.sp, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

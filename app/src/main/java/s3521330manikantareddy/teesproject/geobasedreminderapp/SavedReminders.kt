package s3521330manikantareddy.teesproject.geobasedreminderapp


import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import s3521330manikantareddy.teesproject.geobasedreminderapp.geofence.ReminderViewModel
import s3521330manikantareddy.teesproject.geobasedreminderapp.room.ReminderEntity


@Composable
fun SavedRemindersScreen(
    viewModel: ReminderViewModel,
    onEdit: (ReminderEntity) -> Unit = {},
    onDelete: (ReminderEntity) -> Unit = {}
) {

//    val viewModel: ReminderViewModel = viewModel()

    val reminders = viewModel.reminders

    val context = LocalContext.current

    var editingReminder by remember { mutableStateOf<ReminderEntity?>(null) }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Saved Reminders",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (reminders.isEmpty()) {
            Text(
                "No reminders saved.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 20.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(reminders, key = { it.id }) { reminder ->
                    ReminderItemCard(
                        reminder = reminder,
                        onToggle = { active ->
//                            viewModel.toggleActive(reminder.id, active)
                            viewModel.toggleActive(reminder.id, active) { success ->
                                if (!success) {
                                    Toast.makeText(
                                        context,
                                        "Failed to update status!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        },
                        onEdit = {
                            editingReminder = reminder
                        },
                        onDelete = {
//                            viewModel.deleteReminder(reminder)

                            viewModel.deleteReminder(reminder) { success ->
                                if (success) {
                                    Toast.makeText(context, "Reminder deleted!", Toast.LENGTH_SHORT)
                                        .show()
                                    onDelete(reminder)
                                } else {
                                    Toast.makeText(context, "Failed to delete!", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }


                        }
                    )
                }
            }
        }
    }

    if (editingReminder != null) {
        EditReminderBottomSheet(
            reminder = editingReminder!!,
            onDismiss = { editingReminder = null },
            onUpdate = { updated ->
                viewModel.updateReminder(updated) { success ->
                    // Optional: show snackbar
                }
            },
            onDelete = { reminder ->
                viewModel.deleteReminder(reminder) { success ->
                    // Remove geofence too
                }
            }
        )
    }
}


@Composable
fun ReminderItemCard(
    reminder: ReminderEntity,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val activeColor = if (reminder.isActive) Color(0xFF4CAF50) else Color(0xFFE0E0E0)

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            // Colored strip
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(70.dp)
                    .background(activeColor, RoundedCornerShape(50))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    reminder.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    reminder.message.ifBlank { "No message" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Row {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            "${reminder.latitude.format(3)}, ${reminder.longitude.format(3)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Text(
                        "${reminder.radius.toInt()}m • ${reminder.triggerType}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // Toggle Active/Inactive
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            if (reminder.isActive) "Active" else "Inactive",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = reminder.isActive,
                            onCheckedChange = onToggle
                        )
                    }

                    // Edit / Delete buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF2196F3).copy(alpha = 0.15f),
                            modifier = Modifier.clickable { onEdit() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color(0xFF2196F3)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit", color = Color(0xFF2196F3))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFFE91E63).copy(alpha = 0.15f),
                            modifier = Modifier.clickable { onDelete() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color(0xFFE91E63)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete", color = Color(0xFFE91E63))
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Double.format(decimals: Int) = "%.${decimals}f".format(this)




package s3521330manikantareddy.teesproject.geobasedreminderapp


import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import s3521330manikantareddy.teesproject.geobasedreminderapp.geofence.ReminderViewModel
import s3521330manikantareddy.teesproject.geobasedreminderapp.room.ReminderEntity
import s3521330manikantareddy.teesproject.geobasedreminderapp.ui.theme.MainBGColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedRemindersScreen(
    viewModel: ReminderViewModel,
    onEdit: (ReminderEntity) -> Unit = {},
    onDelete: (ReminderEntity) -> Unit = {}
) {

    val reminders = viewModel.reminders
    val context = LocalContext.current

    var editingReminder by remember { mutableStateOf<ReminderEntity?>(null) }
    var reminderToDelete by remember { mutableStateOf<ReminderEntity?>(null) }
    var reminderToToggle by remember { mutableStateOf<ReminderEntity?>(null) }
    var toggleTargetState by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Saved Reminders", fontSize = 22.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainBGColor,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
        ) {

            Spacer(modifier = Modifier.height(6.dp))

            if (reminders.isEmpty()) {
                Text(
                    text = "No reminders saved.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 20.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(reminders, key = { it.id }) { reminder ->
                        ReminderItemCard(
                            reminder = reminder,
                            onToggle = { newState ->
                                reminderToToggle = reminder
                                toggleTargetState = newState
                            },
                            onEdit = {
                                editingReminder = reminder
                            },
                            onDelete = {
                                reminderToDelete = reminder
                            }
                        )
                    }
                }
            }
        }
    }

    if (reminderToDelete != null) {
        AlertDialog(
            onDismissRequest = { reminderToDelete = null },
            title = { Text("Delete Reminder") },
            text = {
                Text(
                    "Are you sure you want to delete \"${reminderToDelete!!.title}\"?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val reminder = reminderToDelete!!
                        reminderToDelete = null

                        viewModel.deleteReminder(reminder) { success ->
                            Toast.makeText(
                                context,
                                if (success) "Reminder deleted!" else "Failed to delete!",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (success) onDelete(reminder)
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { reminderToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (reminderToToggle != null) {
        AlertDialog(
            onDismissRequest = { reminderToToggle = null },
            title = {
                Text(
                    if (toggleTargetState) "Activate Reminder"
                    else "Deactivate Reminder"
                )
            },
            text = {
                Text(
                    if (toggleTargetState)
                        "Do you want to activate this reminder?"
                    else
                        "Do you want to deactivate this reminder?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val reminder = reminderToToggle!!
                        reminderToToggle = null

                        viewModel.toggleActive(
                            reminder.id,
                            toggleTargetState
                        ) { success ->
                            if (!success) {
                                Toast.makeText(
                                    context,
                                    "Failed to update status!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { reminderToToggle = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (editingReminder != null) {
        EditReminderBottomSheet(
            reminder = editingReminder!!,
            onDismiss = { editingReminder = null },
            onUpdate = { updated ->
                viewModel.updateReminder(updated) { success ->
                    if (!success) {
                        Toast.makeText(
                            context,
                            "Failed to update reminder!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                editingReminder = null
            },
            onDelete = { reminder ->
                viewModel.deleteReminder(reminder) { success ->
                    if (success) {
                        Toast.makeText(
                            context,
                            "Reminder deleted!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                editingReminder = null
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
    val activeGradient = if (reminder.isActive)
        listOf(Color(0xFF4CAF50), Color(0xFF81C784))
    else
        listOf(Color(0xFFBDBDBD), Color(0xFFE0E0E0))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        Brush.horizontalGradient(activeGradient)
                    )
            )

            Column(modifier = Modifier.padding(18.dp)) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        reminder.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Switch(
                        checked = reminder.isActive,
                        onCheckedChange = onToggle
                    )
                }

                if (reminder.message.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        reminder.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        reminder.address.ifBlank { "Address not available" },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                    InfoChipSR(
                        icon = Icons.Default.MyLocation,
                        text = "${reminder.radius.toInt()} meters"
                    )

                    InfoChipSR(
                        icon = Icons.Default.DirectionsWalk,
                        text = reminder.triggerType
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }

                    TextButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChipSR(
    icon: ImageVector,
    text: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


fun Double.format(decimals: Int) = "%.${decimals}f".format(this)




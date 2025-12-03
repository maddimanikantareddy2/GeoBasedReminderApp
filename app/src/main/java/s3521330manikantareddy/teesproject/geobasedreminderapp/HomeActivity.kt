package s3521330manikantareddy.teesproject.geobasedreminderapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val sampleReminders = remember {
                mutableStateListOf(
                    Reminder(1, true, "Pick up dry cleaning", "Laundry Shop (50m radius)", TriggerType.ARRIVE),
                    Reminder(2, false, "Email team meeting summary", "Office Building (200m radius)", TriggerType.LEAVE),
                    Reminder(3, true, "Buy birthday cake ingredients", "Grocery Store (100m radius)", TriggerType.ARRIVE),
                    Reminder(4, true, "Check meter reading", "Home Address", TriggerType.LEAVE),
                )
            }

            GeoReminderHomeScreen(
                reminders = sampleReminders,
                onToggleReminder = { reminder, isActive ->
                    val index = sampleReminders.indexOfFirst { it.id == reminder.id }
                    if (index != -1) {
                        sampleReminders[index] = sampleReminders[index].copy(isActive = isActive)
                    }
                },
                onReminderClick = {
                },
                onCreateNewReminder = {

                }
            )

        }
    }
}

enum class TriggerType {
    ARRIVE, LEAVE
}

data class Reminder(
    val id: Int,
    var isActive: Boolean,
    val task: String,
    val locationName: String,
    val triggerType: TriggerType
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeoReminderHomeScreen(
    reminders: List<Reminder>,
    onToggleReminder: (Reminder, Boolean) -> Unit = { _, _ -> },
    onReminderClick: (Reminder) -> Unit = {},
    onCreateNewReminder: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Geo Reminders", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNewReminder,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add New Reminder")
            }
        }
    ) { paddingValues ->
        if (reminders.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "No reminders set yet.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Tap '+' to create your first location-based reminder.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Active Reminders",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(reminders, key = { it.id }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onToggle = onToggleReminder,
                        onClick = onReminderClick
                    )
                }
            }
        }
    }
}

@Composable
fun ReminderCard(
    reminder: Reminder,
    onToggle: (Reminder, Boolean) -> Unit,
    onClick: (Reminder) -> Unit
) {
    var checkedState by remember { mutableStateOf(reminder.isActive) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (checkedState) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 1. Task Title and Edit/View Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(reminder) }, // Clickable to edit/view details
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = reminder.task,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (checkedState) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Edit Reminder",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            // 2. Location Details and Toggle Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Location Name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = reminder.locationName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    // Trigger Type (Arrive/Leave)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val (icon, label) = when (reminder.triggerType) {
                            TriggerType.ARRIVE -> Icons.Default.Person to "Trigger on Arrival"
                            TriggerType.LEAVE -> Icons.Default.Person to "Trigger on Leaving"
                        }
                        Icon(
                            icon,
                            contentDescription = label,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Temporary Disable Switch
                Switch(
                    checked = checkedState,
                    onCheckedChange = { isActive ->
                        checkedState = isActive // Update local state immediately for responsiveness
                        onToggle(reminder, isActive) // Send event up to state manager
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GeoReminderHomeScreenPreview() {
    // Create a mock list of reminders
    val sampleReminders = remember {
        mutableStateListOf(
            Reminder(1, true, "Pick up dry cleaning", "Laundry Shop (50m radius)", TriggerType.ARRIVE),
            Reminder(2, false, "Email team meeting summary", "Office Building (200m radius)", TriggerType.LEAVE),
            Reminder(3, true, "Buy birthday cake ingredients", "Grocery Store (100m radius)", TriggerType.ARRIVE),
            Reminder(4, true, "Check meter reading", "Home Address", TriggerType.LEAVE),
        )
    }

    MaterialTheme {
        GeoReminderHomeScreen(
            reminders = sampleReminders,
            onToggleReminder = { reminder, isActive ->
                val index = sampleReminders.indexOfFirst { it.id == reminder.id }
                if (index != -1) {
                    sampleReminders[index] = sampleReminders[index].copy(isActive = isActive)
                }
            },
            onReminderClick = { /* Handle navigation to edit screen */ }
        )
    }
}
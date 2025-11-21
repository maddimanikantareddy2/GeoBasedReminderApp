package s3521330manikantareddy.teesproject.geobasedreminderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }
}

data class GeoReminder(
    val id: Int,
    val title: String,
    val locationName: String,
    val triggerType: String, // "Arrival" or "Exit"
    val isEnabled: Boolean = true
)

// ------------------------- HOME SCREEN -------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    val activeReminders = remember {
        mutableStateOf(
            listOf(
                GeoReminder(1, "Buy Groceries", "City Market", "Arrival", true),
                GeoReminder(2, "Pick Laundry", "Laundry Center", "Exit", true)
            )
        )
    }

    val disabledReminders = remember {
        mutableStateOf(
            listOf(
                GeoReminder(3, "Visit Gym", "Fitness Pro Gym", "Arrival", false)
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Geo Reminders") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            item {
                AddReminderCard(onClick = { /* Navigate */ })
            }

            item {
                SectionTitle("Active Reminders")
            }

            items(activeReminders.value) { reminder ->
                ReminderItemCard(
                    reminder = reminder,
                    isDisabled = false,
                    onToggle = { _, _ -> },
                    onMenuClick = {}
                )
            }

            item {
                SectionTitle("Disabled Reminders")
            }

            items(disabledReminders.value) { reminder ->
                ReminderItemCard(
                    reminder = reminder,
                    isDisabled = true,
                    onToggle = { _, _ -> },
                    onMenuClick = {}
                )
            }
        }
    }
}

// ------------------------- COMPONENTS -------------------------

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
fun AddReminderCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Add New Geo Reminder",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ReminderItemCard(
    reminder: GeoReminder,
    isDisabled: Boolean,
    onToggle: (GeoReminder, Boolean) -> Unit,
    onMenuClick: (GeoReminder) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDisabled)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = if (isDisabled) Color.Gray else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    reminder.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDisabled) Color.Gray else Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                TriggerChip(type = reminder.triggerType)

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    reminder.locationName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }

            Switch(
                checked = !isDisabled,
                onCheckedChange = { onToggle(reminder, it) }
            )

            IconButton(onClick = { onMenuClick(reminder) }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Options")
            }
        }
    }
}

@Composable
fun TriggerChip(type: String) {

    val icon = when (type) {
        "Arrival" -> Icons.Default.KeyboardArrowRight
        else -> Icons.Default.ArrowBack
    }

    val color = when (type) {
        "Arrival" -> Color(0xFF1976D2) // Blue
        else -> Color(0xFF2E7D32) // Green
    }

    AssistChip(
        onClick = {},
        label = { Text(type, color = Color.White) },
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = Color.White)
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color
        )
    )
}
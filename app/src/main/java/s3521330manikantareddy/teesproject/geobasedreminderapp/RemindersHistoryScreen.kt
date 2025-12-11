package s3521330manikantareddy.teesproject.geobasedreminderapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import java.util.Locale

@Composable
fun RemindersHistoryScreen(
    reminders: List<ReminderEntity>,
    onEdit: (ReminderEntity) -> Unit = {},
    onDelete: (ReminderEntity) -> Unit  ={}
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = upcoming, 1 = past

    val upcomingList = remember(reminders) {
        reminders.filter { it.isActive }  // or based on date logic
    }

    val pastList = remember(reminders) {
        reminders.filter { !it.isActive } // or based on date logic
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ---------------------- TAB ROW ----------------------
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFFF8F8F8),
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Upcoming") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Past") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------------------- REMINDER LIST ----------------------
        val listToShow = if (selectedTab == 0) upcomingList else pastList

        if (listToShow.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No reminders available", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(listToShow) { reminder ->
                    ReminderItemCard(
                        reminder = reminder,
                        onEdit = onEdit,
                        onDelete = onDelete
                    )
                }
            }
        }
    }
}

@Composable
fun ReminderItemCard(
    reminder: ReminderEntity,
    onEdit: (ReminderEntity) -> Unit,
    onDelete: (ReminderEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(reminder) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                reminder.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (reminder.message.isNotBlank()) {
                Text(
                    reminder.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Trigger type + radius
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Chip(label = reminder.triggerType)
                Chip(label = "Radius: ${reminder.radius.toInt()}m")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Created time
            Text(
                "Created: ${formatTime(reminder.createdAt)}",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Delete",
                    color = Color.Red,
                    modifier = Modifier.clickable { onDelete(reminder) }
                )
                Text(
                    "Edit",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onEdit(reminder) }
                )
            }
        }
    }
}

@Composable
fun Chip(label: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE7E7E7)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 12.sp
        )
    }
}

fun formatTime(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault())
    return format.format(date)
}

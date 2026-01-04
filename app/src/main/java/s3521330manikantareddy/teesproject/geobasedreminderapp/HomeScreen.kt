package s3521330manikantareddy.teesproject.geobasedreminderapp

import android.Manifest
import android.R
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import s3521330manikantareddy.teesproject.geobasedreminderapp.room.ReminderEntity
import s3521330manikantareddy.teesproject.geobasedreminderapp.ui.theme.MainBGColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    username: String,
    reminders: List<ReminderEntity>,
    onAddReminder: () -> Unit,
    onViewReminders: () -> Unit,
    onViewHistory: () -> Unit,
    onAbout: () -> Unit
) {
    val upcoming = reminders.filter { !it.isTriggered }.take(3)

    Scaffold(
        containerColor = Color(0xFFF7F8FC),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Home", fontSize = 22.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainBGColor,
                    titleContentColor = Color.White,
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            "Hi, $username 👋",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "You have ${upcoming.size} upcoming location reminders",
                            color = Color.Gray
                        )
                    }
                }

            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeActionCard(
                        title = "Add Reminder",
                        icon = Icons.Default.AddLocation,
                        accentColor = Color(0xFF4CAF50),
                        onClick = onAddReminder,
                        modifier = Modifier.weight(1f)
                    )

                    HomeActionCard(
                        title = "Saved",
                        icon = Icons.Default.List,
                        accentColor = Color(0xFF2196F3),
                        onClick = onViewReminders,
                        modifier = Modifier.weight(1f)
                    )

                    HomeActionCard(
                        title = "History",
                        icon = Icons.Default.History,
                        accentColor = Color(0xFFE91E63),
                        onClick = onViewHistory,
                        modifier = Modifier.weight(1f)
                    )
                }



            }

            item {
                Text(
                    "Upcoming Reminders",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            if (upcoming.isEmpty()) {
                item {
                    Text("No upcoming reminders", color = Color.Gray)
                }
            } else {
                items(upcoming) { reminder ->
                    MiniReminderCard(reminder)
                }
            }

            item {
                LocationStatusCard()
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAbout() },
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("About & Contact", fontWeight = FontWeight.SemiBold)
                            Text(
                                "App info and developer details",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

            }

            item {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {

            CenterAlignedTopAppBar(
                title = { Text("About & Contact", fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainBGColor,
                    titleContentColor = Color.White,
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {

                    Text(
                        "About Us",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "With the Geo-based Reminder App, users receive reminders "
                                + "when they arrive at or leave a specific location. "
                                + "This helps ensure important tasks are never missed "
                                + "based on real-world movement.",
                        color = Color.DarkGray
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {

                    Text(
                        "Developed By",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))
                    Text("Student Number: S3521330")
                    Text("Email: maddimanikantareddy2@gmail.com")
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {

                    Text(
                        "Contact Us",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))
                    Text("📧 maddimanikantareddy2@gmail.com")
                }
            }
        }
    }
}



@Composable
fun HomeActionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}




@Composable
fun MiniReminderCard(reminder: ReminderEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                reminder.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${reminder.triggerType} • ${reminder.radius.toInt()}m",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LocationStatusCard() {
    val context = LocalContext.current

    val hasLocationPermission =
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Text(
                "Permission Status",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            StatusRow("Location Permission", hasLocationPermission)
            StatusRow("Background Access", hasBackgroundPermission(context))
            StatusRow("GPS", isGpsEnabled(context))
            StatusRow("Notification Permission", hasNotificationPermission(context))
        }
    }

}

@Composable
fun StatusRow(label: String, enabled: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = if (enabled) Icons.Default.CheckCircle else Icons.Default.Error,
            tint = if (enabled) Color(0xFF4CAF50) else Color.Red,
            contentDescription = null
        )
        Spacer(Modifier.width(10.dp))
        Text(label, fontSize = 15.sp)
    }
}


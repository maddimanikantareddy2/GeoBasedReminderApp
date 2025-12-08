package s3521330manikantareddy.teesproject.geobasedreminderapp


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// -----------------------------------------------------
// DATA MODEL
// -----------------------------------------------------


// -----------------------------------------------------
// FIREBASE FETCH FUNCTION
// -----------------------------------------------------
suspend fun fetchReminders(context: Context): List<GeoReminder> {
    return try {
        val email = UserPrefs.getEmail(context)
        val safeEmail = email.replace(".", "_")

        val ref = FirebaseDatabase.getInstance()
            .getReference("Reminders/$safeEmail")

        val snapshot = ref.get().await()

        snapshot.children.mapNotNull {
            it.getValue(GeoReminder::class.java)
        }

    } catch (e: Exception) {
        emptyList()
    }
}

// -----------------------------------------------------
// DELETE REMINDER
// -----------------------------------------------------
suspend fun deleteReminder(context: Context, reminderId: String): Boolean {
    return try {
        val safeEmail = UserPrefs.getEmail(context).replace(".", "_")
        val ref = FirebaseDatabase.getInstance()
            .getReference("Reminders/$safeEmail/$reminderId")

        ref.removeValue().await()
        true

    } catch (e: Exception) {
        false
    }
}

// -----------------------------------------------------
// REMINDER LIST SCREEN
// -----------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedReminders() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var reminders by remember { mutableStateOf<List<GeoReminder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteReminderId by remember { mutableStateOf("") }

    // EDIT SCREEN STATES
    var showEditScreen by remember { mutableStateOf(false) }
    var selectedReminder by remember { mutableStateOf<GeoReminder?>(null) }

    // Load reminders
    LaunchedEffect(Unit) {
        isLoading = true
        reminders = fetchReminders(context)
        isLoading = false
    }

    // If Edit Screen is Open → show it FULL SCREEN
    if (showEditScreen && selectedReminder != null) {
        EditReminderScreen(
            reminder = selectedReminder!!,
            onBack = { showEditScreen = false },
            onUpdateDone = {
                showEditScreen = false
                scope.launch {
                    isLoading = true
                    reminders = fetchReminders(context)
                    isLoading = false
                }
            }
        )
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Saved Reminders", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // LOADING
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // EMPTY LIST
            else if (reminders.isEmpty()) {
                Text(
                    "No reminders found",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }

            // SHOW REMINDERS
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(reminders) { reminder ->

                        ReminderCard(
                            reminder = reminder,
                            onDelete = {
                                deleteReminderId = reminder.reminderId
                                showDeleteDialog = true
                            },
                            onEdit = {
                                selectedReminder = reminder
                                showEditScreen = true
                            }
                        )
                    }
                }
            }

            // DELETE POPUP
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },

                    title = { Text("Delete Reminder?") },
                    text = { Text("This action cannot be undone.") },

                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false

                            scope.launch {
                                val success = deleteReminder(context, deleteReminderId)
                                if (success) {
                                    Toast.makeText(context, "Reminder deleted", Toast.LENGTH_SHORT)
                                        .show()

                                    // Refresh list
                                    reminders = fetchReminders(context)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Delete failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }) {
                            Text("Delete", color = Color.Red)
                        }
                    },

                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

// -----------------------------------------------------
// REMINDER CARD UI
// -----------------------------------------------------
@Composable
fun ReminderCard(
    reminder: GeoReminder,
    onDelete: () -> Unit,
    onEdit: () -> Unit   // callback only
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(reminder.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(6.dp))
            Text(reminder.message, fontSize = 14.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Address: ${reminder.address}", fontSize = 13.sp)

            Spacer(modifier = Modifier.height(6.dp))
            Text("Radius: ${reminder.radius.toInt()} m", fontSize = 13.sp)

            Text("Trigger: ${reminder.triggerType}", fontSize = 13.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color(0xFF2196F3),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onEdit() }  // correct call
                )

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onDelete() }
                )
            }

        }
    }
}



suspend fun updateReminder(
    context: Context,
    reminder: GeoReminder
): Boolean {
    return try {
        val safeEmail = UserPrefs.getEmail(context).replace(".", "_")
        val ref = FirebaseDatabase.getInstance()
            .getReference("Reminders/$safeEmail/${reminder.reminderId}")

        ref.setValue(reminder).await()
        true

    } catch (e: Exception) {
        false
    }
}

package s3521330manikantareddy.teesproject.geobasedreminderapp

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import s3521330manikantareddy.teesproject.geobasedreminderapp.ui.theme.MainBGColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    initialName: String,
    email: String,
    initialDob: String,
    initialPlace: String,
    onLogout: () -> Unit
) {

    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf(initialName) }
    var dob by remember { mutableStateOf(initialDob) }
    var place by remember { mutableStateOf(initialPlace) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", fontSize = 22.sp) },
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3E3E3)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileField(
                label = "Name",
                value = name,
                enabled = isEditing,
                onValueChange = { name = it }
            )

            ProfileField(
                label = "Email",
                value = email,
                enabled = false
            )

            ProfileField(
                label = "Place",
                value = place,
                enabled = isEditing,
                onValueChange = { place = it }
            )

            ProfileField(
                label = "Date of Birth",
                value = dob,
                enabled = isEditing,
                onValueChange = { dob = it }
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    if (isEditing) {
                        updateProfileInFirebase(
                            context = context,
                            email = email,
                            name = name,
                            dob = dob,
                            place = place
                        )
                    }
                    isEditing = !isEditing
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isEditing) "Save Changes" else "Edit Profile")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                )
            ) {
                Text("Logout", color = Color.Red)
            }
        }
    }
}


@Composable
fun ProfileField(
    label: String,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}


fun updateProfileInFirebase(
    context: Context,
    email: String,
    name: String,
    dob: String,
    place: String
) {
    val db = FirebaseDatabase.getInstance()
    val ref = db.getReference("UserAccounts")

    val key = email.replace(".", ",")

    val updates = mapOf(
        "name" to name,
        "dob" to dob,
        "location" to place
    )

    ref.child(key).updateChildren(updates)
        .addOnSuccessListener {
            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Toast.makeText(
                context,
                "Failed to update profile: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
}


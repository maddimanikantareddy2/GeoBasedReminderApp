package s3521330manikantareddy.teesproject.geobasedreminderapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResetPasswordScreen()
        }
    }
}


@Composable
fun ResetPasswordScreen() {

    var email by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var step2 by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val context = LocalContext.current.findActivity()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = colorResource(id = R.color.bg_color),
            ),
    ) {


        Spacer(modifier = Modifier.height(54.dp))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_location_reminder),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
            )

        }

        Spacer(modifier = Modifier.height(12.dp))


        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Geo Based Reminder App",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),

            )

        Spacer(Modifier.height(20.dp))


        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Reset Account Password",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(modifier = Modifier.height(54.dp))


            if (!step2) {


                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Enter Email") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon",
                            tint = Color.Black
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                    ),
                )

                Spacer(Modifier.height(12.dp))


                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    value = dob,
                    onValueChange = { dob = it },
                    placeholder = { Text("Date of Birth (dd-mm-yyyy)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date Icon",
                            tint = Color.Black
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                    ),
                )

                Spacer(Modifier.height(20.dp))


                Text(
                    modifier = Modifier
                        .clickable {
                            loading = true
                            errorMessage = ""
                            successMessage = ""

                            val key = email.replace(".", ",")

                            FirebaseDatabase.getInstance().getReference("SignedUpUsers").child(key).get()
                                .addOnSuccessListener { snapshot ->
                                    loading = false

                                    if (!snapshot.exists()) {
                                        errorMessage = "User not found"
                                        return@addOnSuccessListener
                                    }

                                    val dbEmail = snapshot.child("email").value?.toString() ?: ""
                                    val dbDob = snapshot.child("dob").value?.toString() ?: ""

                                    if (dbEmail == email && dbDob == dob) {
                                        step2 = true // show new password fields
                                    } else {
                                        errorMessage = "Email or DOB incorrect"
                                    }
                                }
                                .addOnFailureListener {
                                    loading = false
                                    errorMessage = "Error: ${it.localizedMessage}"
                                }
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .background(
                            color = colorResource(id = R.color.fg_color),
                            shape = RoundedCornerShape(
                                10.dp
                            )
                        )
                        .border(
                            width = 2.dp,
                            color = colorResource(id = R.color.fg_color),
                            shape = RoundedCornerShape(
                                10.dp
                            )
                        )
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    text = "Verify",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colorResource(id = R.color.bg_color),
                    )
                )
            }

            if (step2) {


                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    placeholder = { Text("New Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Date Icon",
                            tint = Color.Black
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                    ),
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("Confirm Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Date Icon",
                            tint = Color.Black
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                    ),
                )



                Spacer(Modifier.height(20.dp))


                Text(
                    modifier = Modifier
                        .clickable {
                            errorMessage = ""
                            successMessage = ""

                            if (newPassword != confirmPassword) {
                                errorMessage = "Passwords do not match"
                                return@clickable
                            }

                            loading = true

                            val key = email.replace(".", ",")

                            FirebaseDatabase.getInstance().getReference("SignedUpUsers").child(key).child("password").setValue(newPassword)
                                .addOnSuccessListener {
                                    loading = false
                                    successMessage = "Password updated successfully!"

                                    context!!.startActivity(
                                        Intent(
                                            context,
                                            LoginActivity::class.java
                                        )
                                    )
                                    context.finish()
                                }
                                .addOnFailureListener {
                                    loading = false
                                    errorMessage = "Failed to update password"
                                }

                        }
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .background(
                            color = colorResource(id = R.color.fg_color),
                            shape = RoundedCornerShape(
                                10.dp
                            )
                        )
                        .border(
                            width = 2.dp,
                            color = colorResource(id = R.color.fg_color),
                            shape = RoundedCornerShape(
                                10.dp
                            )
                        )
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    text = "Update Password",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colorResource(id = R.color.bg_color),
                    )
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        if (loading) Text("Processing...")

        if (errorMessage.isNotEmpty())
            Text(errorMessage, color = MaterialTheme.colorScheme.error)

        if (successMessage.isNotEmpty())
            Text(successMessage, color = MaterialTheme.colorScheme.primary)

}

@Preview(showBackground = true)
@Composable
fun ResetPasswordScreenPreview() {
    ResetPasswordScreen()
}
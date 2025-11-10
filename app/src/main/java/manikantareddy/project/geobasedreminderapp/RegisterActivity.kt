package manikantareddy.project.geobasedreminderapp


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import kotlin.jvm.java



class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoinAppScreen()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinAppScreen() {
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userLocation by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }

    val context = LocalContext.current as Activity

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
                painter = painterResource(id = R.drawable.ic_location_reminder), // Replace with your actual SVG drawable
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



        Spacer(modifier = Modifier.height(54.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = userName,
            onValueChange = { userName = it },
            placeholder = { Text("Enter Name") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Email Icon",
                    tint = Color.White
                )
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = userEmail,
            onValueChange = { userEmail = it },
            placeholder = { Text("Enter Email") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email Icon",
                    tint = Color.White
                )
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = userLocation,
            onValueChange = { userLocation = it },
            placeholder = { Text("Enter Location") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Email Icon",
                    tint = Color.White
                )
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            value = userPassword,
            onValueChange = { userPassword = it },
            placeholder = { Text("Enter Password") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Email Icon",
                    tint = Color.White
                )
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            ),
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            modifier = Modifier
                .clickable {
                    when {
                        userName.isEmpty() -> {
                            Toast.makeText(context, " Please Enter Mail", Toast.LENGTH_SHORT).show()
                        }

                        userEmail.isEmpty() -> {
                            Toast.makeText(context, " Please Enter Password", Toast.LENGTH_SHORT)
                                .show()
                        }
                        userLocation.isEmpty() -> {
                            Toast.makeText(context, " Please Enter Location", Toast.LENGTH_SHORT)
                                .show()
                        }
                        userPassword.isEmpty() -> {
                            Toast.makeText(context, " Please Enter Password", Toast.LENGTH_SHORT).show()
                        }

                        else -> {

                        }

                    }

                }
                .width(200.dp)
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
                .padding(vertical = 12.dp, horizontal = 12.dp)
                .align(Alignment.CenterHorizontally),
            text = "SignUp",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(
                color = colorResource(id = R.color.bg_color),
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    context.finish()
                },
            text = "Or Continue To SignIn",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(
                color = colorResource(id = R.color.fg_color),
            )
        )

        Spacer(modifier = Modifier.weight(1f))


    }
}
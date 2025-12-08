package s3521330manikantareddy.teesproject.geobasedreminderapp


import android.R.attr.password
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase
import kotlin.jvm.java

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnterAppScreen()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterAppScreen() {
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }

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


        Spacer(modifier = Modifier.height(54.dp))


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
                    tint = Color.Black
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
                    tint = Color.Black
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
                        userEmail.isEmpty() -> {
                            Toast.makeText(context, " Please Enter Mail", Toast.LENGTH_SHORT).show()
                        }

                        userPassword.isEmpty() -> {
                            Toast.makeText(context, " Please Enter Password", Toast.LENGTH_SHORT).show()
                        }

                        else -> {




                            val database = FirebaseDatabase.getInstance()
                            val databaseReference = database.reference

                            val sanitizedEmail = userEmail.replace(".", ",")

                            databaseReference.child("UserAccounts").child(sanitizedEmail).get()
                                .addOnSuccessListener { snapshot ->
                                    if (snapshot.exists()) {
                                        val chefData = snapshot.getValue(AccountData::class.java)
                                        chefData?.let {

                                            if (userPassword == it.password) {
                                                Toast.makeText(context, "Login Successfull", Toast.LENGTH_SHORT).show()
                                                UserPrefs.markLoginStatus(context!!, true)
                                                UserPrefs.saveEmail(
                                                    context,
                                                    email = userEmail
                                                )
                                                UserPrefs.saveName(context, snapshot.child("name").value.toString())


                                                context!!.startActivity(Intent(context, ContainerActivity::class.java))
                                                context.finish()
                                            }
                                            else{
                                                Toast.makeText(context,"Incorrect Credentials",Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context,"No User Found",Toast.LENGTH_SHORT).show()
                                    }
                                }.addOnFailureListener { exception ->
                                    println("Error retrieving data: ${exception.message}")
                                }

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
            text = "SignIn",
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
                    context!!.startActivity(Intent(context, ForgotPasswordActivity::class.java))
                    context.finish()
                },
            text = "Or Forgot Password",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(
                color = colorResource(id = R.color.fg_color),
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    context!!.startActivity(Intent(context, RegisterActivity::class.java))
                    context.finish()
                },
            text = "Or SignUp For Account",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(
                color = colorResource(id = R.color.fg_color),
            )
        )

        Spacer(modifier = Modifier.weight(1f))


    }
}

@Preview(showBackground = true)
@Composable
fun EnterAppScreenPreview() {
    EnterAppScreen()
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
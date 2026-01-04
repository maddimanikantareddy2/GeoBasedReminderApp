package s3521330manikantareddy.teesproject.geobasedreminderapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import s3521330manikantareddy.teesproject.geobasedreminderapp.geofence.ReminderViewModel
import s3521330manikantareddy.teesproject.geobasedreminderapp.ui.theme.GeoBasedReminderAppTheme

class ContainerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GeoBasedReminderAppTheme() {
                ContainerScreen()
            }
        }
    }
}

@Composable
fun ContainerScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { CustomBottomBar(navController) }
    ) { innerPadding ->

        Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
            NavigationGraph(navController)
        }
    }

}

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object AddReminder : BottomNavItem("addreminder", "Add Reminder", Icons.Default.Alarm)
    object SavedReminder :
        BottomNavItem("savedreminders", "Saved Reminders", Icons.Default.Save)

    object UpcomingReminder :
        BottomNavItem("upcomingreminder", "Upcoming Reminders", Icons.Default.Upcoming)

    object ProfileScreen :
        BottomNavItem("profilescreen", "Profile Screen", Icons.Default.AccountCircle)
}


@Composable
fun NavigationGraph(navController: NavHostController) {

    val context = LocalContext.current

    val viewModel: ReminderViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(BottomNavItem.AddReminder.route) {
            SetReminderScreen(viewModel = viewModel)
        }
        composable(BottomNavItem.SavedReminder.route) {
            SavedRemindersScreen(viewModel)
        }

        composable(BottomNavItem.UpcomingReminder.route) {
            val reminders = viewModel.reminders

            RemindersHistoryScreen(
                reminders = reminders
            )
        }



        composable(BottomNavItem.ProfileScreen.route) {
            ProfileScreen(
                UserLoginData.getName(context),
                UserLoginData.getEmail(context),
                UserLoginData.getDOB(context),
                UserLoginData.getPlace(context),
                onLogout = {
                    UserLoginData.markLoginStatus(context, false)
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    (context as Activity).finish()
                }
            )
        }

        composable("about") {
            AboutUsScreen { navController.popBackStack() }
        }


        composable(BottomNavItem.Home.route) {
            HomeScreen(
                username = UserLoginData.getName(LocalContext.current),
                reminders = viewModel.reminders,
                onAddReminder = {
                    navController.navigate(BottomNavItem.AddReminder.route) {
                        launchSingleTop = true
                    }
                },
                onViewReminders = {
                    navController.navigate(BottomNavItem.SavedReminder.route)
                },
                onViewHistory = {
                    navController.navigate(BottomNavItem.UpcomingReminder.route)
                },
                onAbout = {
                    navController.navigate("about")
                }
            )

        }

    }
}

@Composable
fun CustomBottomBar(
    navController: NavHostController
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.AddReminder,
        BottomNavItem.SavedReminder,
        BottomNavItem.UpcomingReminder,
        BottomNavItem.ProfileScreen
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Surface(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        shadowElevation = 10.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEach { item ->

                val selected = navBackStackEntry
                    ?.destination
                    ?.hierarchy
                    ?.any { it.route == item.route } == true

                BottomNavItemView(
                    item = item,
                    selected = selected
                ) {
                    if (item.route == BottomNavItem.Home.route) {
                        navController.popBackStack(
                            BottomNavItem.Home.route,
                            false
                        )
                    } else {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }
}




@Composable
fun BottomNavItemView(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) Color(0xFFE75959) else Color.Transparent,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = tween(durationMillis = 250), label = ""
    )

    val horizontalPadding by animateDpAsState(
        targetValue = if (selected) 16.dp else 0.dp,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(42.dp)
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = horizontalPadding)
    ) {

        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = if (selected) Color.White else Color.Gray,
            modifier = Modifier
                .size(22.dp)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                }
        )

        AnimatedVisibility(
            visible = selected,
            enter = fadeIn(tween(200)) + expandHorizontally(tween(300)),
            exit = fadeOut(tween(150)) + shrinkHorizontally(tween(200))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = item.title,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}


package s3521330manikantareddy.teesproject.geobasedreminderapp.notifications


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import s3521330manikantareddy.teesproject.geobasedreminderapp.R

private const val CHANNEL_ID = "geofence_channel"

fun showGeoNotification(context: Context, title: String, body: String) {
    val channelId = "geo_reminder_channel"

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.app_icon_geo)
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    val manager = context.getSystemService(NotificationManager::class.java)
    manager.notify(System.currentTimeMillis().toInt(), notification)
}




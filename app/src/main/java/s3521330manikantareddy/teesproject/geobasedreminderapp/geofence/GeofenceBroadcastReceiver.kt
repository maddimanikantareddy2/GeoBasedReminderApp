package s3521330manikantareddy.teesproject.geobasedreminderapp.geofence


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import s3521330manikantareddy.teesproject.geobasedreminderapp.notifications.showGeoNotification

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        val event = GeofencingEvent.fromIntent(intent!!) ?: return
        if (event.hasError()) return

        val geofence = event.triggeringGeofences?.firstOrNull() ?: return
        val transition = event.geofenceTransition
        val reminderId = geofence.requestId

        Log.e("Test", "Geofence triggered")

        val dao = ReminderDatabase.getDatabase(context).reminderDao()

        CoroutineScope(Dispatchers.IO).launch {
            dao.markAsTriggered(reminderId, System.currentTimeMillis())

            val reminder = dao.getReminderById(reminderId)

            if (reminder != null) {
                val title = reminder.title
                val message = reminder.message

                when (transition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Log.e("Test", "Entered")
                        showGeoNotification(
                            context,
                            title,
                            message.ifEmpty { "You entered the location!" }
                        )
                    }

                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Log.e("Test", "Exited")
                        showGeoNotification(
                            context,
                            title,
                            message.ifEmpty { "You left the location!" }
                        )
                    }
                }
            }
        }
    }
}


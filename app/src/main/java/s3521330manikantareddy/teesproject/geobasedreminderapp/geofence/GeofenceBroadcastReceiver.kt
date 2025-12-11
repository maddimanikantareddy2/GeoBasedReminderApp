package s3521330manikantareddy.teesproject.geobasedreminderapp.geofence


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import s3521330manikantareddy.teesproject.geobasedreminderapp.notifications.showGeoNotification

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        val event = GeofencingEvent.fromIntent(intent!!) ?: return
        if (event.hasError()) return

        val geofence = event.triggeringGeofences?.firstOrNull() ?: return
        val transition = event.geofenceTransition

        val reminderId = geofence.requestId

        Log.e("Test","Geofence triggered")

        when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.e("Test","Entered")
                showGeoNotification(context, reminderId, "Entered the location!")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.e("Test","Left")
                showGeoNotification(context, reminderId, "Left the location!")
            }
        }
    }
}

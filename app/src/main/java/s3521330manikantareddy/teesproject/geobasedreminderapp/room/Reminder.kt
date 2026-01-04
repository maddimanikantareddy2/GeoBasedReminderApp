package s3521330manikantareddy.teesproject.geobasedreminderapp.room

data class Reminder(
    val id: String,
    val title: String,
    val message: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val radius: Float,
    val triggerType: String, // "Arrive" or "Leave"
    val isActive: Boolean = true
)

package s3521330manikantareddy.teesproject.geobasedreminderapp.room


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val triggerType: String,     // "Arrive" or "Leave"
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

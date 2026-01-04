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
    val address: String,
    val radius: Float,
    val triggerType: String,

    val isActive: Boolean = true,

    val isTriggered: Boolean = false,
    val triggeredAt: Long? = null,

    val createdAt: Long = System.currentTimeMillis()
)


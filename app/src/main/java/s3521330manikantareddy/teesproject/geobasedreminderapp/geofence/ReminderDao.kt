package s3521330manikantareddy.teesproject.geobasedreminderapp.geofence

import s3521330manikantareddy.teesproject.geobasedreminderapp.room.ReminderEntity


import androidx.room.*

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity): Int

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity): Int

    @Query("SELECT * FROM reminders ORDER BY createdAt DESC")
    suspend fun getAllReminders(): List<ReminderEntity>

    @Query("UPDATE reminders SET isActive = :active WHERE id = :id")
    suspend fun updateActiveStatus(id: String, active: Boolean): Int

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getReminderById(id: String): ReminderEntity?

    @Query("""
    UPDATE reminders 
    SET isTriggered = 1, 
        triggeredAt = :time 
    WHERE id = :id
""")
    suspend fun markAsTriggered(id: String, time: Long)


}


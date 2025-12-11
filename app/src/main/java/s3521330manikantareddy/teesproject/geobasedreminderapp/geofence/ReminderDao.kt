package s3521330manikantareddy.teesproject.geobasedreminderapp.geofence

import s3521330manikantareddy.teesproject.geobasedreminderapp.room.ReminderEntity


import androidx.room.*

@Dao
interface ReminderDao {

    // INSERT → returns rowId (>0 = success)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    // UPDATE → returns number of rows updated
    @Update
    suspend fun updateReminder(reminder: ReminderEntity): Int

    // DELETE → returns number of rows deleted
    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity): Int

    @Query("SELECT * FROM reminders ORDER BY createdAt DESC")
    suspend fun getAllReminders(): List<ReminderEntity>

    @Query("UPDATE reminders SET isActive = :active WHERE id = :id")
    suspend fun updateActiveStatus(id: String, active: Boolean): Int
}


package s3521330manikantareddy.teesproject.geobasedreminderapp.geofence

import s3521330manikantareddy.teesproject.geobasedreminderapp.room.ReminderEntity


class ReminderRepository(private val dao: ReminderDao) {

    suspend fun addReminder(reminder: ReminderEntity): Boolean {
        return dao.insertReminder(reminder) > 0
    }

    suspend fun updateReminder(reminder: ReminderEntity): Boolean {
        return dao.updateReminder(reminder) > 0
    }

    suspend fun deleteReminder(reminder: ReminderEntity): Boolean {
        return dao.deleteReminder(reminder) > 0
    }

    suspend fun toggleActive(id: String, active: Boolean): Boolean {
        return dao.updateActiveStatus(id, active) > 0
    }

    suspend fun getAllReminders(): List<ReminderEntity> {
        return dao.getAllReminders()
    }
}


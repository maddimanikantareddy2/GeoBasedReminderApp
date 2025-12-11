package s3521330manikantareddy.teesproject.geobasedreminderapp.geofence

import androidx.compose.runtime.mutableStateListOf


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import s3521330manikantareddy.teesproject.geobasedreminderapp.room.ReminderEntity

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ReminderDatabase.getDatabase(application).reminderDao()
    private val repository = ReminderRepository(dao)

    var reminders = mutableStateListOf<ReminderEntity>()
        private set

    init {
        loadReminders()
    }

    fun loadReminders() {
        viewModelScope.launch {
            reminders.clear()
            reminders.addAll(repository.getAllReminders())
        }
    }

    fun addReminder(reminder: ReminderEntity, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.addReminder(reminder)
            if (success) reminders.add(0, reminder)
            onResult(success)
        }
    }

    fun updateReminder(reminder: ReminderEntity, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.updateReminder(reminder)
            if (success) {
                val index = reminders.indexOfFirst { it.id == reminder.id }
                if (index != -1) reminders[index] = reminder
            }
            onResult(success)
        }
    }

    fun deleteReminder(reminder: ReminderEntity, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.deleteReminder(reminder)
            if (success) reminders.remove(reminder)
            onResult(success)
        }
    }

    fun toggleActive(id: String, active: Boolean, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.toggleActive(id, active)
            if (success) loadReminders()
            onResult(success)
        }
    }
}


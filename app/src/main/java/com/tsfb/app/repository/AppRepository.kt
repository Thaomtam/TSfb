import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.tsfb.app.data.AppDatabase
import com.tsfb.app.data.Settings
import com.tsfb.app.data.StatsType
import com.tsfb.app.data.entities.LogEntity
import kotlinx.coroutines.flow.first

class AppRepository(context: Context) {
    private val dataStore: DataStore<Preferences> = context.createDataStore(name = "settings")
    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app_database"
    ).build()

    suspend fun incrementStats(type: StatsType) {
        database.statsDao().increment(type.name)
    }

    suspend fun addLog(action: String, details: String = "") {
        database.logDao().insert(LogEntity(
            timestamp = System.currentTimeMillis(),
            action = action,
            details = details
        ))
    }

    fun getAllLogs() = database.logDao().getAllLogs()

    suspend fun getSettings(): Settings {
        return dataStore.data.first().let { preferences ->
            Settings(
                interactionSpeed = preferences[PreferencesKeys.INTERACTION_SPEED] ?: 5,
                apiKey = preferences[PreferencesKeys.API_KEY] ?: ""
            )
        }
    }

    suspend fun saveSettings(settings: Settings) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.INTERACTION_SPEED] = settings.interactionSpeed
            preferences[PreferencesKeys.API_KEY] = settings.apiKey
        }
    }

    private object PreferencesKeys {
        val INTERACTION_SPEED = intPreferencesKey("interaction_speed")
        val API_KEY = stringPreferencesKey("api_key")
    }
} 
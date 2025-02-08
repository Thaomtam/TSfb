import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stats")
data class StatsEntity(
    @PrimaryKey
    val type: String,
    val count: Int = 0
) 
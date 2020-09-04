package urbandict.api

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity
data class Definition(
    val definition: String,
    val word: String,
    @Json(name = "thumbs_up")
    val thumbsUp: Long,
    @Json(name = "thumbs_down")
    val thumbsDown: Long,
    @Json(name = "defid")
    @PrimaryKey
    val id: Long,
)

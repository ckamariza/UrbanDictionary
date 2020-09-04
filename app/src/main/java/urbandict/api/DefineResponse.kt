package urbandict.api

import com.squareup.moshi.JsonClass
import urbandict.api.Definition

@JsonClass(generateAdapter = true)
data class DefineResponse(
    val list: List<Definition> = emptyList()
)

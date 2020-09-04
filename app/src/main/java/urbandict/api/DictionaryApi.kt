package urbandict.api

import retrofit2.http.GET
import retrofit2.http.Query

interface DictionaryApi {
    @GET("/define")
    suspend fun define(@Query("term") term: String): DefineResponse
}

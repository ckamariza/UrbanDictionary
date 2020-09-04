package urbandict.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import urbandict.api.DictionaryApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideApi(@BaseUrl url: String, @Headers headers: Map<String, String>): DictionaryApi {
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor {
                        it.proceed(it.request().newBuilder()
                            .apply {
                                headers.forEach { entry ->
                                    addHeader(entry.key, entry.value)
                                }
                            }
                            .build())
                    }
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .build())
            .build()
            .create(DictionaryApi::class.java)
    }

    @Singleton
    @Provides
    @Headers
    fun provideHeaders(): Map<String, String> {
        return mapOf(
            "x-rapidapi-host" to "mashape-community-urban-dictionary.p.rapidapi.com",
            "x-rapidapi-key" to "f2ba86279fmsh184468eceeb0123p116c5djsn2d8cd17af2d8"
        )
    }

    @Singleton
    @Provides
    @BaseUrl
    fun provideUrl(): String {
        return "https://mashape-community-urban-dictionary.p.rapidapi.com"
    }
}

@Qualifier
annotation class BaseUrl

@Qualifier
annotation class Headers

package urbandict.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import urbandict.api.DictionaryApi
import urbandict.repository.DefinitionDb
import urbandict.repository.DefinitionsRepository
import urbandict.repository.LocalDataSource
import urbandict.repository.RemoteDataSource
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): DefinitionDb {
        return Room
            .databaseBuilder(context, DefinitionDb::class.java, "definitions.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideLocalDataSource(db: DefinitionDb): LocalDataSource {
        return LocalDataSource(db)
    }

    @Singleton
    @Provides
    fun provideRemoteDataSource(api: DictionaryApi): RemoteDataSource {
        return RemoteDataSource(api)
    }

    @Singleton
    @Provides
    fun provideRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
        coroutineContextProvider: CoroutineContextProvider
    ): DefinitionsRepository {
        return DefinitionsRepository(localDataSource, remoteDataSource, coroutineContextProvider)
    }
}

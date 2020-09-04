package urbandict.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(ApplicationComponent::class)
class CoroutineModule {

    @Singleton
    @Provides
    fun provideCoroutineContext(): CoroutineContextProvider {
        return CoroutineContextProvider()
    }

}

open class CoroutineContextProvider() {
    open val Main: CoroutineContext by lazy { Dispatchers.Main }
    open val IO: CoroutineContext by lazy { Dispatchers.IO }
}

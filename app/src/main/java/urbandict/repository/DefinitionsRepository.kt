package urbandict.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import urbandict.api.Definition
import urbandict.di.CoroutineContextProvider

class DefinitionsRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val coroutineContextProvider: CoroutineContextProvider
) {

    suspend fun define(term: String): Flow<List<Definition>> = flow {
        emit(localDataSource.define(term))
        withContext(coroutineContextProvider.IO) {
            runCatching {
                remoteDataSource.define(term)
            }
                .getOrNull()
                ?.let {
                    localDataSource.insert(it)
                    emit(it)
                }
        }
    }
}

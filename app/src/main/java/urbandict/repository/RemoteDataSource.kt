package urbandict.repository

import urbandict.api.DictionaryApi
import urbandict.repository.DefinitionsDataSource

class RemoteDataSource(private val api: DictionaryApi): DefinitionsDataSource {

    override suspend fun define(term: String) = api.define(term).list
}

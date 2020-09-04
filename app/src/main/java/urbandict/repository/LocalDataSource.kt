package urbandict.repository

import urbandict.api.Definition

class LocalDataSource(private val db: DefinitionDb): DefinitionsDataSource {

    override suspend fun define(term: String) = db.definitionDao().query(term)

    suspend fun insert(list: List<Definition>) = db.definitionDao().insert(list)
}

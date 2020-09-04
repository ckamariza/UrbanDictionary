package urbandict.repository

import urbandict.api.Definition


interface DefinitionsDataSource {

    suspend fun define(term: String): List<Definition>

}

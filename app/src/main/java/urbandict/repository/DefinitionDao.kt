package urbandict.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import urbandict.api.Definition

@Dao
abstract class DefinitionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(list: List<Definition>)

    @Query("SELECT * FROM Definition WHERE word LIKE :term")
    abstract suspend fun query(term: String): List<Definition>
}

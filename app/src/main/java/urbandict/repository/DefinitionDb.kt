package urbandict.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import urbandict.api.Definition

@Database(
    entities = [Definition::class],
    exportSchema = false,
    version = 1,
)
abstract class DefinitionDb: RoomDatabase() {

    abstract fun definitionDao(): DefinitionDao

}

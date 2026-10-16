package cl.oursour.app.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction

/** "Mis ingredientes": la selección del usuario guardada en el teléfono (CU-01, paso 4). */
@Entity(tableName = "my_ingredients")
data class MyIngredientEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val category: String
)

@Dao
abstract class MyIngredientDao {

    @Query("SELECT * FROM my_ingredients ORDER BY name")
    abstract suspend fun getAll(): List<MyIngredientEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(items: List<MyIngredientEntity>)

    @Query("DELETE FROM my_ingredients")
    abstract suspend fun clear()

    /** Reemplaza toda la selección en una sola transacción. */
    @Transaction
    open suspend fun replaceAll(items: List<MyIngredientEntity>) {
        clear()
        insertAll(items)
    }
}

@Database(entities = [MyIngredientEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun myIngredientDao(): MyIngredientDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "oursour.db").build()
    }
}

package com.hanafi.noorezakat

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

@Entity(tableName = "app_storage")
data class AppStateEntity(
    @PrimaryKey val storageKey: String,
    val storageVal: String,
    val updatedAt: Long
)

@Dao
interface AppStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(state: AppStateEntity): Long

    @Query("SELECT storageVal FROM app_storage WHERE storageKey = :key LIMIT 1")
    fun load(key: String): String?
}

@Database(entities = [AppStateEntity::class], version = 1, exportSchema = false)
abstract class NoorDatabase : RoomDatabase() {
    abstract fun stateDao(): AppStateDao

    companion object {
        @Volatile private var INSTANCE: NoorDatabase? = null
        fun getInstance(context: Context): NoorDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, NoorDatabase::class.java, "noor_zakat_room.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build().also { db ->
                    migrateLegacyState(context.applicationContext, db)
                    INSTANCE = db
                }
        }

        private fun migrateLegacyState(context: Context, target: NoorDatabase) {
            val legacy = context.getDatabasePath("noor_zakat_pro.db")
            if (!legacy.exists()) return
            try {
                val old = android.database.sqlite.SQLiteDatabase.openDatabase(legacy.path, null, android.database.sqlite.SQLiteDatabase.OPEN_READONLY)
                old.rawQuery("SELECT storage_key, storage_val FROM app_storage", null).use { c ->
                    val keyIndex = c.getColumnIndex("storage_key")
                    val valueIndex = c.getColumnIndex("storage_val")
                    if (keyIndex >= 0 && valueIndex >= 0) while (c.moveToNext()) {
                        target.stateDao().upsert(AppStateEntity(c.getString(keyIndex), c.getString(valueIndex), System.currentTimeMillis()))
                    }
                }
                old.close()
                legacy.renameTo(context.getDatabasePath("noor_zakat_pro.db.migrated"))
            } catch (_: Exception) {
                // Legacy import is best-effort; a fresh Room store remains usable.
            }
        }
    }

    fun saveState(key: String, value: String): Boolean = try {
        stateDao().upsert(AppStateEntity(key, value, System.currentTimeMillis())); true
    } catch (_: Exception) { false }

    fun loadState(key: String): String? = try { stateDao().load(key) } catch (_: Exception) { null }
}

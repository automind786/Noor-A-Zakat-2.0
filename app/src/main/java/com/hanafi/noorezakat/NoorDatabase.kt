package com.hanafi.noorezakat

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** Simple key-value storage (encrypted values). */
class NoorDatabase(context: Context) : SQLiteOpenHelper(context, "noor_zakat_pro.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS app_storage (
                storage_key TEXT PRIMARY KEY,
                storage_val TEXT NOT NULL,
                updated_at INTEGER
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Purana (ab unused) Shariah-FTS table hata do; user ka saved data (app_storage) safe rehta hai.
        db.execSQL("DROP TABLE IF EXISTS shariah_kb_fts")
        onCreate(db)
    }

    fun saveState(key: String, value: String): Boolean {
        return try {
            val cv = ContentValues().apply {
                put("storage_key", key)
                put("storage_val", value)
                put("updated_at", System.currentTimeMillis())
            }
            writableDatabase.insertWithOnConflict("app_storage", null, cv, SQLiteDatabase.CONFLICT_REPLACE) != -1L
        } catch (e: Exception) {
            false
        }
    }

    fun loadState(key: String): String? {
        return try {
            readableDatabase.rawQuery(
                "SELECT storage_val FROM app_storage WHERE storage_key = ?", arrayOf(key)
            ).use { c -> if (c.moveToFirst()) c.getString(0) else null }
        } catch (e: Exception) {
            null
        }
    }
}

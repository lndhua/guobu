package com.example.photomanager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "PhotoManagerDB"
        private const val TABLE_INFOS = "infos"
        private const val KEY_ID = "_id"
        private const val KEY_INFO = "info"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_INFOS("
                + "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_INFO TEXT)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INFOS")
        onCreate(db)
    }

    fun addInfo(info: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_INFO, info)
        }
        return db.insert(TABLE_INFOS, null, contentValues)
    }

    fun getAllInfos(): List<String> {
        val list = ArrayList<String>()
        val selectQuery = "SELECT * FROM $TABLE_INFOS"
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, null)
        cursor?.let {
            while (it.moveToNext()) {
                val info = it.getString(it.getColumnIndexOrThrow(KEY_INFO))
                list.add(info)
            }
        }
        cursor?.close()
        return list
    }
}
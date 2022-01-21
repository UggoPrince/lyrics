package com.ugo.lyrics

import android.content.Context
import androidx.room.Room

object DataManager {
    var albums = ArrayList<Song>()
    lateinit var db: LyricsDatabase
    lateinit var lyricsDao: LyricsDao

    fun initDb(context: Context) {
        db = Room.databaseBuilder(
            context,
            LyricsDatabase::class.java, DATABASE
        ).build()
        lyricsDao = db.lyricsDao()
    }
}
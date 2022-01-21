package com.ugo.lyrics

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Lyrics::class], version = 1)
@TypeConverters(Converters::class)
abstract class LyricsDatabase: RoomDatabase() {
    abstract fun lyricsDao(): LyricsDao
}
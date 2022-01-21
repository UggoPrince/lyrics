package com.ugo.lyrics

import androidx.room.*
import java.util.*

@Entity(tableName = "saved_lyrics")
data class Lyrics(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "artist") val artist: String?,
    @ColumnInfo(name = "song_title") val title: String?,
    @ColumnInfo(name = "song_lyrics") val lyrics: String?,
    @ColumnInfo(name = "createdAt") val createdAt: Date?,
    @ColumnInfo(name = "updatedAt") val updatedAt: Date?,
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

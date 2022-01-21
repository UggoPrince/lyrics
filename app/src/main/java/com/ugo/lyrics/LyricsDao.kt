package com.ugo.lyrics

import androidx.room.*

@Dao
interface LyricsDao {
    @Query("SELECT * FROM saved_lyrics WHERE artist = :artistName AND " +
            "song_title = :songName LIMIT 1")
    fun findByName(artistName: String, songName: String): Lyrics

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLyrics(vararg lyrics: Lyrics)


    @Query("SELECT * FROM saved_lyrics")
    fun getAll(): List<Lyrics>

    @Query("SELECT * FROM saved_lyrics WHERE id = :lyricId")
    fun getLyric(lyricId: Int): Lyrics

    @Update
    fun updateSavedLyricsById(vararg lyrics: Lyrics)
}
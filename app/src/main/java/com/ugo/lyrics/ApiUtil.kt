package com.ugo.lyrics

import android.net.Uri
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.ArrayList

object ApiUtil {
    val DEEZER_URL: String = "http://api.deezer.com/search"
    val LYRICS_OVH_URL: String = "https://api.lyrics.ovh/v1"
    val QUERY_PARAMETER_KEY: String = "q";
    val LIMIT: String = "limit"

    fun buildSearchArtist(limit: String, text: String): URL {
        val url: URL
        val uri: Uri = Uri.parse(DEEZER_URL).buildUpon()
            .appendQueryParameter(LIMIT, limit)
            .appendQueryParameter(QUERY_PARAMETER_KEY, text)
            .build()
        url = URL(uri.toString())
        return url;
    }

    fun buildSearchLyrics(artist: String, title: String): URL {
        val url: URL
        val uri: Uri = Uri.parse(LYRICS_OVH_URL).buildUpon()
            .appendPath(artist)
            .appendPath(title)
            .build()
        url = URL(uri.toString())
        return url;
    }

    fun getJson(url: URL): String? {
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            val stream: InputStream = connection.inputStream
            val scanner: Scanner = Scanner(stream)
            scanner.useDelimiter("\\A")
            val hasData: Boolean = scanner.hasNext()
            if (hasData) {
                return scanner.next()
            } else {
                return null
            }
        } catch (e: FileNotFoundException) {
            Log.d("Lyrics not found Error", e.toString())
            return "Not found"
        } catch (e: UnknownHostException) {
            Log.d("Network Error", e.toString())
            return "Network Error"
        } catch (e: Exception) {
            Log.d("General Error", e.toString())
            return null
        } finally {
            connection.disconnect()
        }
    }

    fun getAlbumsFromJson(json: String): ArrayList<Song> {
        val TITLE: String = "title"
        val ARTIST: String = "artist"
        val NAME: String = "name"
        val DATA: String = "data"
        val albums: ArrayList<Song> = ArrayList()
        try {
            val jsonObject: JSONObject = JSONObject(json)
            val arrayAlbums: JSONArray = jsonObject.getJSONArray(DATA)
            for (i in 0 until arrayAlbums.length()) {
                val albumJson: JSONObject = arrayAlbums.getJSONObject(i)
                val title = albumJson.getString(TITLE)
                val artistObj: JSONObject = albumJson.getJSONObject(ARTIST)
                val artist = artistObj.getString(NAME)
                val lyrics: Song = Song(title, artist)
                albums.add(lyrics)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return albums
    }

    fun getLyricsFromJson(json: String): String {
        val LYRICS: String = "lyrics"
        var lyrics: String = ""
        try {
            val jsonObject: JSONObject = JSONObject(json)
            lyrics = jsonObject.getString(LYRICS)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return lyrics
    }
}
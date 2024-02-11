package com.ugo.lyrics

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import java.net.URL
import java.util.*
import java.util.Objects.isNull
import java.util.concurrent.Executors

class LyricsActivity : AppCompatActivity() {
    private lateinit var db: LyricsDatabase
    private lateinit var lyricsDao: LyricsDao
    private lateinit var lyricsTextView: TextView
    private lateinit var lyricsTextErrorView: TextView
    private lateinit var spinner: ProgressBar
    private lateinit var artist: String
    private lateinit var songTitle: String
    private lateinit var artistName: TextView
    private lateinit var songName: TextView
    private lateinit var copyButton: MenuItem
    private lateinit var saveButton: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyrics)
        setSupportActionBar(findViewById(R.id.lyricsDetailsToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        DataManager.initDb(applicationContext)
        // db = DataManager.db
        // lyricsDao = DataManager.lyricsDao

        spinner = findViewById(R.id.lyricsProgressBar)
        lyricsTextView = findViewById(R.id.lyricsText)
        lyricsTextErrorView = findViewById(R.id.lyricsTextError)
        artistName = findViewById(R.id.artistName)
        songName = findViewById(R.id.songName)

        artist = intent.extras?.get(ARTIST).toString()
        songTitle = intent.extras?.get(SONG_TITLE).toString()

        getLyrics()
    }

    private fun getLyrics() {
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())
        myExecutor.execute {
            val url: URL = ApiUtil.buildSearchLyrics(artist, songTitle)
            val lyricsJson: String? = ApiUtil.getJson(url)
            when {
                lyricsJson === "Not found" -> {
                    myHandler.post {
                        hideSpinner()
                        displayError("No lyrics found")
                    }
                }
                lyricsJson === "Network Error" -> {
                    hideSpinner()
                    displayError("Your network is down.")
                }
                lyricsJson == null -> {
                    myHandler.post {
                        hideSpinner()
                        displayError("No lyrics found")
                    }
                }
                else -> {
                    val lyrics: String = ApiUtil.getLyricsFromJson(lyricsJson)
                    myHandler.post {
                        hideSpinner()
                        displayLyrics(lyrics)
                        enableButtons()
                    }
                }
            }
        }
        myExecutor.shutdown()
    }

    private fun hideSpinner() {
        spinner.visibility = View.GONE
    }

    private fun displayLyrics(lyrics: String) {
        lyricsTextView.visibility = View.VISIBLE
        artistName.text = artist
        songName.text = songTitle
        lyricsTextView.text = lyrics
    }

    private fun displayError(message: String) {
        // lyricsLayout.visibility = View.VISIBLE
        lyricsTextErrorView.visibility = View.VISIBLE
        lyricsTextErrorView.text = message
    }

    private fun enableButtons() {
        copyButton.isEnabled = true
        saveButton.isEnabled = true
    }

    private fun copyTextToClipboard() {
        val textToCopy = lyricsTextView.text

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()
    }

    private fun savedLyricsExist(): Int {
        val l  = DataManager.db.lyricsDao().findByName(artist, songTitle)
        if (!isNull(l)) return l.id!!
        return 0
    }

    private fun saveLyrics() {
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())
        myExecutor.execute {
            val text: String = lyricsTextView.text.toString()
            val i: Int = savedLyricsExist()
            val t = Date()
            if (i == 0) {
                val l = Lyrics(null, artist, songTitle, text, t, t)
                DataManager.db.lyricsDao().insertLyrics(l)
                myHandler.post {
                    Toast.makeText(applicationContext, "Lyrics saved.", Toast.LENGTH_LONG).show()
                }
            } else {
                val l = Lyrics(i, artist, songTitle, text, null, t)
                DataManager.db.lyricsDao().updateSavedLyricsById(l)
                myHandler.post {
                    Toast.makeText(applicationContext, "Lyrics updated.", Toast.LENGTH_LONG).show()
                }
            }
        }
        myExecutor.shutdown()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_lyrics, menu)
        copyButton = menu.findItem(R.id.copyLyrics)
        saveButton = menu.findItem(R.id.saveLyrics)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.copyLyrics -> {
                copyTextToClipboard()
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.saveLyrics -> {
                saveLyrics()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (DataManager.db.isOpen) DataManager.db.close()
    }
}
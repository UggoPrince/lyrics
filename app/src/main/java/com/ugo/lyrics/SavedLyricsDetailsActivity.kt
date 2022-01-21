package com.ugo.lyrics

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import java.lang.Exception
import java.util.concurrent.Executors

class SavedLyricsDetailsActivity : AppCompatActivity() {
    private lateinit var db: LyricsDatabase
    private var id: Int = 0
    private lateinit var artistName: TextView
    private lateinit var songName: TextView
    private lateinit var lyricsTextView: TextView
    private lateinit var copyButton: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_lyrics_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lyricsTextView = findViewById(R.id.lyricsText)
        artistName = findViewById(R.id.artistName)
        songName = findViewById(R.id.songName)

        DataManager.initDb(applicationContext)
        // db = DataManager.db
        id = intent.extras!!.get(LYRICS_ID).toString().toInt()
    }

    override fun onResume() {
        super.onResume()
        try {
            getLyrics()
        } catch (e: Exception) {
            Log.d("Saved Lyrics Debug", e.toString())
        }
    }

    private fun getLyrics() {
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())
        myExecutor.execute {
            val lyric = DataManager.db.lyricsDao().getLyric(id)
            myHandler.post {
                artistName.text = lyric.artist
                songName.text = lyric.title
                lyricsTextView.text = lyric.lyrics
                // enableButton()
            }
        }
        myExecutor.shutdown()
    }

    private fun copyTextToClipboard() {
        val textToCopy = lyricsTextView.text

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()
    }

    private fun enableButton() {
        copyButton.isEnabled = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_saved_lyrics_details, menu)
        // copyButton = menu.findItem(R.id.copySavedLyrics)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.copySavedLyrics -> {
                copyTextToClipboard()
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DataManager.db.close()
    }
}
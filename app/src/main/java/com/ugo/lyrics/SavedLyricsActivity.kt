package com.ugo.lyrics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

class SavedLyricsActivity : AppCompatActivity() {
    private lateinit var db: LyricsDatabase
    private lateinit var listItems: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_lyrics)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listItems = findViewById(R.id.savedLyricsList)
        listItems.layoutManager = LinearLayoutManager(this)

        DataManager.initDb(applicationContext)
        // db = DataManager.db
        displaySavedLyrics()
    }

    private fun displaySavedLyrics() {
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())
        myExecutor.execute {
            val lyrics = DataManager.db.lyricsDao().getAll()
            myHandler.post {
                if (lyrics.isNotEmpty()) {
                    buildRecyclerView(lyrics)
                }
            }
        }
        myExecutor.shutdown()
    }

    private fun buildRecyclerView(lyrics: List<Lyrics>) {
        listItems.adapter = SavedLyricsRecyclerAdapter(this, lyrics)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
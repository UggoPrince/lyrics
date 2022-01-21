package com.ugo.lyrics

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SavedLyricsRecyclerAdapter
    (private val context: Context, private val lyrics: List<Lyrics>):
    RecyclerView.Adapter<SavedLyricsRecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songTitle: TextView = view.findViewById(R.id.savedSongTitle)
        val artist: TextView = view.findViewById(R.id.savedArtist)
        val lyricId: TextView = view.findViewById(R.id.lyricsId)
        init {
            view.setOnClickListener {
                val intent = Intent(context, SavedLyricsDetailsActivity::class.java)
                intent.putExtra(LYRICS_ID, lyricId.text)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.saved_lyrics_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = lyrics[position]
        holder.artist.text = song.artist
        holder.songTitle.text = song.title
        holder.lyricId.text = song.id.toString()
    }

    override fun getItemCount(): Int {
        return lyrics.size
    }
}
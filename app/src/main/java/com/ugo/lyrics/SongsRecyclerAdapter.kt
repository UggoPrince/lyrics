package com.ugo.lyrics

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*

class SongsRecyclerAdapter(private val context: Context, private val Songs: List<Song>) : Adapter<SongsRecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songTitle: TextView = view.findViewById(R.id.songTitle)
        val artist: TextView = view.findViewById(R.id.artist)
        init {
            view.setOnClickListener {
                val intent = Intent(context, LyricsActivity::class.java)
                intent.putExtra(ARTIST, artist.text)
                intent.putExtra(SONG_TITLE, songTitle.text)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.search_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = Songs[position]
        holder.artist.text = song.artist
        holder.songTitle.text = song.songTitle
    }

    override fun getItemCount(): Int {
        return Songs.size
    }
}
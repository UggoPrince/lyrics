package com.ugo.lyrics

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.net.URL
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    lateinit var listItems: RecyclerView
    lateinit var spinner: ProgressBar
    lateinit var includeRecyclerView: View
    lateinit var sharedPref: SharedPreferences
    var sharedPrefJsonString: String? = null
    var sharedPrefExist: Boolean = false
    var albumsList: List<Song> = DataManager.albums
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        listItems = findViewById(R.id.searchList)
        listItems.layoutManager = LinearLayoutManager(this)

        spinner = findViewById(R.id.loadIndicator)
        includeRecyclerView = findViewById(R.id.searchResultsView)

        val searchView = findViewById<SearchView>(R.id.search)
        val searchIcon: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_search));
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            @SuppressLint("ShowToast")
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    // hideErrorLayout()
                    displaySpinner()
                    getAlbums(query)
                } else {
                    searchView.clearFocus()
                    val message = "No Search Text Submitted"
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), MODE_PRIVATE
        )
        if (sharedPref.contains("albums")) sharedPrefExist = true;
        getSharedPrefJsonString()
        displayLastSearchedData()
    }

    private fun getSharedPrefJsonString() {
        sharedPrefJsonString = sharedPref.getString("albums", null).toString()
    }

    private fun displayLastSearchedData() {
        val gson = Gson()
        val type: Type = object : TypeToken<List<Song?>?>() {}.type
        if (sharedPrefExist) {
            albumsList = gson.fromJson(sharedPrefJsonString, type)
            fillRecyclerView()
        }
    }

    private fun saveLastSearchedData() {
        val gson = Gson()
        val json = gson.toJson(albumsList)
        with(sharedPref.edit()) {
            putString("albums", json)
            apply()
        }

    }

    fun displaySpinner() {
        spinner.setVisibility(View.VISIBLE)
    }

    private fun hideSpinner() {
        spinner.setVisibility(View.GONE)
    }

    private fun getAlbums(searchText: String) {
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())
        myExecutor.execute {
            val url: URL = ApiUtil.buildSearchArtist("30", searchText)
            val albums: String? = ApiUtil.getJson(url);
            if (albums === "Network Error") {
                myHandler.post {
                    hideSpinner()
                    // hideRecyclerView()
                    displayError(albums)
                }
            } else {
                albumsList = ApiUtil.getAlbumsFromJson(albums!!)
                myHandler.post {
                    saveLastSearchedData()
                    hideSpinner()
                    fillRecyclerView()
                }
            }
        }
        myExecutor.shutdown()
    }

    private fun displayError(message: String) {
        // errorLayout.visibility = View.VISIBLE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        // errorTextView.text = message
    }

    fun hideErrorLayout() {
        // errorLayout.visibility = View.GONE
    }

    fun hideRecyclerView() {
        includeRecyclerView.visibility = View.GONE
    }

    private fun fillRecyclerView() {
        includeRecyclerView.visibility = View.VISIBLE
        listItems.adapter = SongsRecyclerAdapter(this, albumsList)
    }

    private fun openSavedLyrics() {
        val intent = Intent(this, SavedLyricsActivity::class.java)
        this.startActivity(intent)
    }

    @SuppressLint("ShowToast")
    override fun onResume() {
        super.onResume()
        if (albumsList.isNotEmpty()) {
            fillRecyclerView()
        } else {
            val message = "No Search Text Submitted"
            Log.d("MainActivity", message);
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.openSavedLyrics).isEnabled = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.openSavedLyrics -> {
                openSavedLyrics()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
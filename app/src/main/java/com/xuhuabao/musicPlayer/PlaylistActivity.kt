package com.xuhuabao.musicPlayer

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.xuhuabao.musicPlayer.databinding.ActivityPlaylistBinding
import com.xuhuabao.musicPlayer.databinding.AddPlaylistDialogBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlaylistViewAdapter
    private var isChange:Boolean = false
    private var numList: Int = 0

    companion object{
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 进入当前页面加载数据
        val editor = getSharedPreferences("favorite_lists", MODE_PRIVATE)
        musicPlaylist = MusicPlaylist()
        val jsonStringPlaylist = editor.getString("MusicPlaylist", null)
        if(jsonStringPlaylist != null){
            val dataPlaylist: MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist, MusicPlaylist::class.java)
                musicPlaylist = dataPlaylist
        }

        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = LinearLayoutManager(this@PlaylistActivity)

        adapter = PlaylistViewAdapter(this, playlistList = musicPlaylist.ref)
        binding.playlistRV.adapter = adapter
        numList = adapter.itemCount

        binding.backBtnPLA.setOnClickListener { finish() }
        binding.addPlaylistBtn.setOnClickListener { customAlertDialog() }

    }
    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this@PlaylistActivity).inflate(R.layout.add_playlist_dialog, binding.root, false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        val dialog = builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("ADD"){ dialog, _ ->
                val playlistName = binder.playlistName.text
                if(playlistName != null)
                    if(playlistName.isNotEmpty())
                    {
                        addPlaylist(playlistName.toString())
                    }
                dialog.dismiss()
            }.create()
        dialog.show()
        setDialogBtnBackground(this, dialog)

    }
    private fun addPlaylist(name: String){
        var playlistExists = false
        for(i in musicPlaylist.ref) {
            if (name == i.name){
                playlistExists = true
                break
            }
        }
        if(playlistExists) Toast.makeText(this, "Playlist Exist!!", Toast.LENGTH_SHORT).show()
        else {
            val tempPlaylist = Playlist()

            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()

            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calendar)

            musicPlaylist.ref.add(tempPlaylist)  // *************
            adapter.refreshPlaylist()  // *************
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isChange || adapter.isChage || numList != adapter.itemCount){
            save_favorite_lists() // 离开当前页面保存数据
        }
    }

    fun save_favorite_lists(){
        Toast.makeText(this, "storing favorite_lists", Toast.LENGTH_SHORT).show()
        //for storing data using shared preferences 保存列表数据
        val editor = getSharedPreferences("favorite_lists", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
    }
}
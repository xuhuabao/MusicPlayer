package com.xuhuabao.musicPlayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.xuhuabao.musicPlayer.databinding.ActivityPlaylistDetailsBinding


class PlaylistDetails : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var adapter: MusicAdapter
    private lateinit var mplaylist: ArrayList<Music>
    private var addChange = false

    companion object{
        var currentPlaylistPos: Int = -1
        var isChange:Boolean = false

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnPD.setOnClickListener { finish() }

        currentPlaylistPos = intent.extras?.get("index") as Int
        try{
            PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist =
            checkPlaylist(playlist = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist)
        }
        catch(e: Exception){}
        mplaylist = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist

        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)

        //构造adapter： 哪个列表内容？musicPlaylist.ref[currentPlaylistPos]， 哪个Activity? playlistDetails
        adapter = MusicAdapter(this, mplaylist, playlistDetails = true)
        binding.playlistDetailsRV.adapter = adapter


        // 实现上下拖动排序，左滑删除
        val itemTouchHelperCallback = RecyclerViewItemTouchHelper(adapter, mplaylist) { position ->
//            Toast.makeText(this, "itemTouchHelperCallback at $position", Toast.LENGTH_SHORT).show()
            refreshInfo()  // 实时刷新，不需要保存
            isChange = true // 1. 离开当前页面时保存歌单歌曲列表
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.playlistDetailsRV)


        binding.addBtnPD.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
            isChange = true // 2. 离开当前页面时保存歌单歌曲列表s
            addChange = true // 添加歌曲返回后执行 adapter.notifyDataSetChanged()
        }
        binding.removeAllPD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
                .setMessage("Do you want to remove all songs from playlist?")
                .setPositiveButton("Yes"){ dialog, _ ->
                    mplaylist.clear()
                    adapter.refreshPlaylist()
                    isChange = true  // 3. 离开当前页面时保存歌单歌曲列表
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            setDialogBtnBackground(this, customDialog)
        }
    }


    override fun onRestart() {
        super.onRestart()

    }

    override fun onStart() {
        super.onStart()

    }

    @SuppressLint("SetTextI18n")
    fun refreshInfo() {  // 实时刷新，不需要保存
        binding.playlistNamePD.text = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].name

        if(adapter.itemCount > 0)
        {
            Glide.with(this)
                .load(PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(binding.playlistImgPD)
        }

        binding.moreInfoPD.text = "Total ${adapter.itemCount} Songs.\n\n" +
                "Created On:\n${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdOn}"
    }

    override fun onResume() {
        super.onResume()
        refreshInfo()  //刚打开页面绑定刷新info，添加歌曲返回后刷新info
        if (addChange) {
            adapter.notifyDataSetChanged() // 添加歌曲返回后刷新歌曲列表
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
//        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show()
        if (isChange) {
            save_favorite_lists() // 离开当前页面保存数据
        }
    }

    fun save_favorite_lists(){
//        Toast.makeText(this, "storing favorite_lists", Toast.LENGTH_SHORT).show()
//        for storing data using shared preferences 保存列表数据
        val editor = getSharedPreferences("favorite_lists", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
    }
}
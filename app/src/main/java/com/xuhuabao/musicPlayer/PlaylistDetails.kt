package com.xuhuabao.musicPlayer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.xuhuabao.musicPlayer.databinding.ActivityPlaylistDetailsBinding
import java.util.Collections

class PlaylistDetails : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var adapter: MusicAdapter
    private lateinit var mplaylist: ArrayList<Music>

    companion object{
        var currentPlaylistPos: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.backBtnPD.setOnClickListener { finish() }

        // ****************************** 拖动排序歌单 start *******************************
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                // 允许拖动到任意位置
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(mplaylist, i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(mplaylist, i, i - 1)
                    }
                }

                // 通知适配器项目已移动
                adapter.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                adapter.notifyDataSetChanged()
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                mplaylist.removeAt(position)
                adapter.notifyItemRemoved(position)
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.playlistDetailsRV)
        // ****************************** 拖动排序歌单 end *******************************


        binding.addBtnPD.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }
        binding.removeAllPD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
                .setMessage("Do you want to remove all songs from playlist?")
                .setPositiveButton("Yes"){ dialog, _ ->
                    mplaylist.clear()
                    adapter.refreshPlaylist()
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
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show()

        binding.playlistNamePD.text = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.moreInfoPD.text = "Total ${adapter.itemCount} Songs.\n\n" +
                "Created On:\n${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n" +
                "  -- ${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdBy}"
        if(adapter.itemCount > 0)
        {
            // 新方法
            val artByteArray = getImgArt(PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].path) // 获取 ByteArray
            val bitmap = artByteArray?.let { BitmapFactory.decodeByteArray(it, 0, it.size) } // 将 ByteArray 转换为 Bitmap
            // 设置 Bitmap 到 ShapeableImageView
            binding.playlistImgPD.setImageBitmap(bitmap)
        }
        adapter.notifyDataSetChanged()
        save_favorite_lists()
    }

    override fun onStop() {
        super.onStop()
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show()
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
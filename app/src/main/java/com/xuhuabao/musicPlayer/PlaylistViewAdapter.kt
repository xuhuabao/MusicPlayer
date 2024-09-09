package com.xuhuabao.musicPlayer

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.xuhuabao.musicPlayer.databinding.AddPlaylistDialogBinding
import com.xuhuabao.musicPlayer.databinding.PlaylistViewBinding


class PlaylistViewAdapter(private val context: Context, private var playlistList: ArrayList<Playlist>) : RecyclerView.Adapter<PlaylistViewAdapter.MyHolder>() {
    public var isChage:Boolean = false

    class MyHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImg
        val name = binding.playlistName
        val numSongs = binding.numSongs
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding = PlaylistViewBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = MyHolder(binding)
        // 注册长按事件
        holder.root.setOnLongClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                showRnOrDelDialog(position)
                true
            } else {
                false
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = playlistList[position].name
        holder.name.isSelected = true
        holder.numSongs.text = "Total songs\n${playlistList[position].playlist.size}"

        holder.root.setOnClickListener {
            val intent = Intent(context, PlaylistDetails::class.java)
            intent.putExtra("index", position)
            ContextCompat.startActivity(context, intent, null)
        }

        if(PlaylistActivity.musicPlaylist.ref[position].playlist.size > 0){

            val artByteArray = getImgArt(PlaylistActivity.musicPlaylist.ref[position].playlist[0].path)
            val bitmap = artByteArray?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            holder.image.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }


    private fun showRnOrDelDialog(position: Int) {
        val item = playlistList[position]
        val builder = AlertDialog.Builder(context)
        builder.setTitle(item.name)
            .setMessage("Do you want to rename or delete?")
            .setNegativeButton("Rename") { dialog, _ ->
                dialog.dismiss()
                showRenameDialog(position)
            }
            .setPositiveButton("Delete") { dialog, _ ->
                PlaylistActivity.musicPlaylist.ref.removeAt(position)
                refreshPlaylist()
                isChage=true
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showRenameDialog(position: Int) {
        val item = playlistList[position]

        val editText = EditText(context)
        editText.imeOptions = EditorInfo.IME_ACTION_DONE

        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 0)
        layout.addView(editText)

        MaterialAlertDialogBuilder(context)
            .setTitle("Enter Playlist Name")
            .setView(layout)
            .setPositiveButton("OK") { dialog, _ ->
                val inputText = editText.text.toString()
                item.name = inputText
                refreshPlaylist()
                isChage = true
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

    private fun removeItem(position: Int) {
        playlistList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun refreshPlaylist(){
        playlistList = ArrayList()
        playlistList.addAll(PlaylistActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }

}
package com.xuhuabao.musicPlayer

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class RecyclerViewItemTouchHelper(
    private val adapter: RecyclerView.Adapter<*>, // 适配器
    private val dataList: MutableList<*>, // 数据列表
    private val onChanged: ((Int) -> Unit)? = null // 可选的移除回调
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN, // 允许上下拖动
    ItemTouchHelper.LEFT // 允许左右侧滑
) {

    // 处理拖动事件
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
                Collections.swap(dataList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(dataList, i, i - 1)
            }
        }

        adapter.notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        dataList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    // 当拖动或侧滑完成时调用
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        adapter.notifyDataSetChanged()
        // 移除歌曲时，歌曲数目变化，实时刷新歌曲条目
        // 上下拖动时即使不涉及第一项，封面图片没变，也会执行回调刷新
        onChanged?.invoke(viewHolder.adapterPosition)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT
        return makeMovementFlags(dragFlags, swipeFlags)
    }
}

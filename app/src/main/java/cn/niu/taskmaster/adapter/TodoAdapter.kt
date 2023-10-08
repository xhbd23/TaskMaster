package cn.niu.taskmaster.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.niu.taskmaster.constant.VALUE_LONG_NOT_INITIALIZED
import cn.niu.taskmaster.databinding.ItemTodoBinding
import cn.niu.taskmaster.room.entity.TodoItem
import cn.niu.taskmaster.ui.MainViewModel
import cn.niu.taskmaster.util.TodoUtils
import com.li.utils.framework.base.recyclerview.BaseBindViewHolder
import com.li.utils.framework.base.recyclerview.BaseRecyclerAdapter
import com.li.utils.framework.base.recyclerview.BaseViewHolder
import com.li.utils.framework.ext.common.asNotNull
import com.li.utils.framework.ext.res.layoutInflater
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.gone
import com.sll.lib_framework.ext.view.visible


/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/06
 */
class TodoAdapter(
    context: Context,
    data: MutableList<TodoItem>,
    private val viewModel: MainViewModel
): BaseRecyclerAdapter<ItemTodoBinding, TodoItem>(context, data) {

    private var mRecyclerView: RecyclerView? = null

    var needCheck = true

    var onItemClickListener: ((item: TodoItem) -> Unit)? = null
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.mRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.mRecyclerView = null
    }

    override fun bindData(holder: BaseViewHolder, item: TodoItem, position: Int) {
        holder.asNotNull<BaseBindViewHolder<ItemTodoBinding>>().binding.apply {
            root.click {
                onItemClickListener?.invoke(item)
            }
            if (needCheck) {
//                checkboxTextview.isChecked = item.completed
                if (item.completed) {
                    checkboxTextview.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG // 设置中划线并加清晰
                } else {
                    checkboxTextview.paint.flags.xor(Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)
                }
//                checkboxTextview.buttonTintList = ColorStateList.valueOf(TodoUtils.getPriorityColor(item.priority))
                checkboxTextview.setTextColor(TodoUtils.getPriorityColor(item.priority))
                checkboxTextview.text = item.title
//                checkboxTextview.setOnCheckedChangeListener { _, isChecked ->
//                    item.completed = isChecked
//                    mRecyclerView?.post { viewModel.swapItemTo(isChecked, position) }
//                }
            }
            TodoUtils.setTodoItemDate(tvDate, item.deadline)
            val hasRepeat = TodoUtils.checkInitialized(item.repeatMode)
            val hasAlert = TodoUtils.checkInitialized(item.alertTime)
            if (hasAlert) ivClock.visible() else ivClock.gone()
            if (hasRepeat) ivRepeat.visible() else ivRepeat.gone()

        }
    }

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemTodoBinding = ItemTodoBinding.inflate(context.layoutInflater, parent, false)


    fun addItem(item: TodoItem) {
        val size = data.size
        var insetPosition = size
        for (i in 0 until size) {
            if (data[i].deadline != VALUE_LONG_NOT_INITIALIZED  && data[i].deadline < item.deadline) continue
            else {
                insetPosition = i
                break
            }
        }
        data.add(insetPosition, item)
        notifyItemInserted(insetPosition)
        notifyItemRangeChanged(insetPosition, size - insetPosition)
    }


    fun updateItem(item: TodoItem) {
        val pos = data.indexOfFirst {
            it.id == item.id
        }
        if (pos != -1) {
            data[pos] = item
            notifyItemChanged(pos)
        }
    }
    fun removeItem(item: TodoItem) {
        val pos = data.indexOfFirst {
            item.id == it.id
        }
        if (pos != -1) {
            data.removeAt(pos)
            notifyItemRemoved(pos)
            notifyItemRangeChanged(pos, data.size - pos + 1)
        }
    }



    inner class TodoViewHolder(
        binding: ItemTodoBinding
    ): BaseBindViewHolder<ItemTodoBinding>(binding)

    interface OnItemClickListener {
        fun onClick()
    }
}


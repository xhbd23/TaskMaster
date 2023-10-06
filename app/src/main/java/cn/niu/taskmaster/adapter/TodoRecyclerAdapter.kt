package cn.niu.taskmaster.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import cn.niu.taskmaster.databinding.ItemTodoBinding
import cn.niu.taskmaster.entity.TodoItem
import com.li.utils.framework.base.recyclerview.BaseBindViewHolder
import com.li.utils.framework.base.recyclerview.BaseRecyclerAdapter
import com.li.utils.framework.base.recyclerview.BaseViewHolder
import com.li.utils.framework.ext.common.asNotNull

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/06
 */
class TodoRecyclerAdapter(
    context: Context,
    data: MutableList<TodoItem>
): BaseRecyclerAdapter<ItemTodoBinding, TodoItem>(context, data) {
    companion object {
        private const val TAG = "TodoRecyclerAdapter"
    }

    override fun bindData(holder: BaseViewHolder, item: TodoItem, position: Int) {
        holder.asNotNull<BaseBindViewHolder<ItemTodoBinding>>().binding.apply {
            checkboxTextview.isChecked = item.completed
            checkboxTextview.text = item.title
            tvDate.text = item.deadline.toString()

        }
    }

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemTodoBinding {
        return ItemTodoBinding.inflate(layoutInflater, parent, false)
    }
}
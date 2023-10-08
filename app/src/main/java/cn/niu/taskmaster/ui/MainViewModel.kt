package cn.niu.taskmaster.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.niu.taskmaster.adapter.TodoAdapter
import cn.niu.taskmaster.constant.VALUE_INT_NOT_INITIALIZED
import cn.niu.taskmaster.constant.VALUE_LONG_NOT_INITIALIZED
import cn.niu.taskmaster.network.ApiManager
import cn.niu.taskmaster.room.entity.TodoItem
import cn.niu.taskmaster.room.RoomManager
import cn.niu.taskmaster.util.TodoUtils
import com.li.utils.LiUtilsApp
import com.li.utils.framework.ext.coroutine.launchIO
import com.li.utils.framework.ext.coroutine.launchMain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/05
 */
class MainViewModel : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    var uncompletedAdapter: TodoAdapter? = null

    var completeAdapter: TodoAdapter? = null


    fun initAdapterData() {
        viewModelScope.launchIO {
            val completed = mutableListOf<TodoItem>()
            val uncompleted = mutableListOf<TodoItem>()
            RoomManager.todoDao.getAllTodoItems().forEach {
                (if (it.completed) completed else uncompleted).add(it)
            }
            val comparator = Comparator<TodoItem> { a, b ->
                val ai = TodoUtils.checkInitialized(a.deadline)
                val bi = TodoUtils.checkInitialized(b.deadline)
                return@Comparator if (ai && bi) (a.deadline - b.deadline).toInt()
                else if (ai) {
                    -1
                } else if (bi) {
                    1
                } else 0
            }

            completed.sortWith(comparator)
            uncompleted.sortWith(comparator)
            launchMain {
                completeAdapter?.swapData(completed)
                uncompletedAdapter?.swapData(uncompleted)
            }
        }
    }


    fun swapItem(item: TodoItem) {
        val moved: TodoItem = item
        completeAdapter?.addItem(item)
        uncompletedAdapter?.removeItem(item)
        updateLocalItem(moved)
    }
    fun swapItemTo(complete: Boolean, position: Int) {
        var moved: TodoItem? = null
        if (complete) {
            val item = uncompletedAdapter?.getItem(position)?.also { moved = it }
            uncompletedAdapter?.removeItem(position)
            completeAdapter?.addItem(item!!)
        } else {
            val item = completeAdapter?.getItem(position)?.also { moved = it }
            completeAdapter?.removeItem(position)
            uncompletedAdapter?.addItem(item!!)
        }
        moved?.let { updateLocalItem(it) }
    }


    fun removeItemAt(complete: Boolean, position: Int) {
        val removed: TodoItem?
        if (complete) {
            removed = completeAdapter?.getItem(position)
            completeAdapter?.removeItem(position)

        } else {
            removed = uncompletedAdapter?.getItem(position)
            uncompletedAdapter?.removeItem(position)
        }
        if (removed != null) {
            removeLocalItem(removed)
            TodoUtils.cancelNotifyWorker(LiUtilsApp.application, removed)
        }

    }

    fun addItem(item: TodoItem) {
        uncompletedAdapter?.addItem(item)
        addLocalItem(item)
        TodoUtils.startNotifyWorker(LiUtilsApp.application, item)
    }


    fun updateItem(item: TodoItem) {
        if (item.completed) {
            completeAdapter?.updateItem(item)
        } else {
            uncompletedAdapter?.updateItem(item)
        }
        updateLocalItem(item)
        TodoUtils.cancelNotifyWorker(LiUtilsApp.application, item)
        TodoUtils.startNotifyWorker(LiUtilsApp.application, item)

    }


    private fun addLocalItem(item: TodoItem) {
        viewModelScope.launchIO {
            RoomManager.todoDao.addTodoItem(item)
        }
    }


    private fun updateLocalItem(item: TodoItem) {
        viewModelScope.launchIO {
            RoomManager.todoDao.updateTodoItem(item)
        }
    }

    private fun removeLocalItem(item: TodoItem) {
        viewModelScope.launchIO {
            RoomManager.todoDao.deleteTodoItem(item)
        }
    }




    // ==================================================================================

    var tmpAddTodoItem: TodoItem? = null


    fun resetTmpAddTodoItem() {
        tmpAddTodoItem = null
    }

    fun setTmpAddTodoItem(
        title: String = "",
        description: String = "",
        deadline: Long = VALUE_LONG_NOT_INITIALIZED,
        priority: Int = VALUE_INT_NOT_INITIALIZED,
        repeatMode: Int = VALUE_INT_NOT_INITIALIZED
    ) {
        tmpAddTodoItem = TodoUtils.getTodoItem(
            title,
            description,
            deadline,
            priority,
            repeatMode
        )

    }

    // ==============================================================================

    var currentItemInfo: TodoItem? = null


    private val _caption = MutableStateFlow("")
    val caption = _caption.asStateFlow()
    // 获取配文
    fun getCaption() {
        viewModelScope.launch {
            launchIO {
                val res = ApiManager.yiyanApi.getSentence("a", 10, 20)
                _caption.value = res
            }
        }
    }


}


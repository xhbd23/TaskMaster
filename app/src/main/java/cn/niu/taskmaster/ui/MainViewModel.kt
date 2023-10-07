package cn.niu.taskmaster.ui

import androidx.lifecycle.ViewModel
import cn.niu.taskmaster.entity.TodoItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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

    // 过期的
    private val _expiredTodo = MutableStateFlow<MutableList<TodoItem>>(mutableListOf())
    val expiredTodo = _expiredTodo.asStateFlow()

    // 今天的
    private val _todayTodo = MutableStateFlow<MutableList<TodoItem>>(mutableListOf())
    val todayIdo = _todayTodo.asStateFlow()

    // 明天的
    private val _tomorrowTodo = MutableStateFlow<MutableList<TodoItem>>(mutableListOf())
    val tomorrowTodo = _expiredTodo.asStateFlow()

    // 没有时间的
    private val _noDateTodo = MutableStateFlow<MutableList<TodoItem>>(mutableListOf())
    val noDateTodo = _noDateTodo.asStateFlow()

    // 已完成的
    private val _completedTodo = MutableStateFlow<MutableList<TodoItem>>(mutableListOf())
    val completedTodo = _completedTodo.asStateFlow()



    fun addTodoItem(items: MutableStateFlow<MutableList<TodoItem>>, item: TodoItem) {
        items.update {
            (it + item) as MutableList<TodoItem>
        }
    }

    fun removeTodoItem(items: MutableStateFlow<MutableList<TodoItem>>, item: TodoItem) {
        items.update {
            (it - item) as MutableList<TodoItem>
        }
    }

    fun updateTodoItem(items: MutableStateFlow<MutableList<TodoItem>>, item: TodoItem) {
        items.update {
            it.find {
                item.id == it.id
            }
            MutableList(it.size) { i ->
                it[i]
            }
        }
    }

    // ==================================================================================

    var tmpAddTodoItem: TodoItem? = null
        private set

    fun setTmpAddTodoItem(
        title: String = "",
        description: String = "",

    ) {

    }

}


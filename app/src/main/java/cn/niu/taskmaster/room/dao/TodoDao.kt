package cn.niu.taskmaster.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.niu.taskmaster.room.entity.TodoItem
import kotlinx.coroutines.flow.Flow

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/06
 */
@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodoItems(items: List<TodoItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodoItem(item: TodoItem)

    @Delete
    suspend fun deleteTodoItem(item: TodoItem)

    @Delete
    suspend fun deleteTodoItems(items: List<TodoItem>)

    @Update
    suspend fun updateTodoItem(item: TodoItem)

    @Update
    suspend fun updateTodoItems(items: List<TodoItem>)

    /**
     * 查询所有实体
     * */
    @Query("SELECT * FROM todo_item")
    suspend fun getAllTodoItems(): List<TodoItem>

    /**
     * 通过 [date] 查找所有的实体
     * */
    @Query("SELECT * FROM todo_item WHERE :date <= deadline AND deadline <= :date + 1000*60*60*24")
     fun getTodoItemsByDate(date: Long): Flow<List<TodoItem>>

    /**
     * 通过 [completed] 查找相关实体
     * */
    @Query("SELECT * FROM todo_item WHERE :completed")
    fun getTodoItemsByCompleted(completed: Boolean):  Flow<List<TodoItem>>

    /**
     * 通过 [[fromDate], [toDate]] 查找相关实体
     * */
    @Query(
        "SELECT * FROM todo_item " +
        "WHERE :fromDate <= create_date AND create_date <= :toDate"
    )
    fun getTodoItemsByCompleted(fromDate: Long, toDate: Long): Flow<List<TodoItem>>


    @Query("SELECT * FROM todo_item WHERE :priority == priority")
    fun getTodoItemsByPriority(priority: Int):  Flow<List<TodoItem>>

}
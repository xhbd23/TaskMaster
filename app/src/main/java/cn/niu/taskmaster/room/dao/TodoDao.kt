package cn.niu.taskmaster.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.niu.taskmaster.entity.TodoItem

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
    suspend fun addTodoItems(items: List<TodoDao>)

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
     * 通过 [date] 查找所有的实体
     * */
    @Query("SELECT * FROM todo_item WHERE :date == create_date")
    suspend fun getTodoItemsByDate(date: Long): List<TodoItem>

    /**
     * 通过 [completed] 查找相关实体
     * */
    @Query("SELECT * FROM todo_item WHERE :completed")
    suspend fun getTodoItemsByCompleted(completed: Boolean): List<TodoItem>

    /**
     * 通过 [[fromDate], [toDate]] 查找相关实体
     * */
    @Query(
        "SELECT * FROM todo_item " +
        "WHERE :fromDate <= create_date AND create_date <= :toDate"
    )
    suspend fun getTodoItemsByCompleted(fromDate: Long, toDate: Long): List<TodoItem>


    @Query("SELECT * FROM todo_item WHERE :priority == priority")
    suspend fun getTodoItemsByPriority(priority: Int): List<TodoItem>

}
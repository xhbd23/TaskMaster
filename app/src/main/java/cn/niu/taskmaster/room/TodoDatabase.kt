package cn.niu.taskmaster.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cn.niu.taskmaster.room.entity.TodoItem
import cn.niu.taskmaster.room.convert.TodoItemConverters
import cn.niu.taskmaster.room.dao.TodoDao

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/06
 */
@Database(
    entities = [TodoItem::class],
    version = 1
)
@TypeConverters(TodoItemConverters::class)
abstract class TodoDatabase: RoomDatabase() {
    companion object {
        private const val TAG = "TodoDatabase"
    }

    abstract fun todoDao(): TodoDao

}
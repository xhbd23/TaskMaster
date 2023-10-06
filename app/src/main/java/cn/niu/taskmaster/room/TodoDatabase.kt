package cn.niu.taskmaster.room

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.niu.taskmaster.entity.TodoItem
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
abstract class TodoDatabase: RoomDatabase() {
    companion object {
        private const val TAG = "TodoDatabase"
    }

    abstract fun todoDao(): TodoDao

}
package cn.niu.taskmaster.room

import android.app.Application
import androidx.room.Room

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/06
 */
object RoomManager {

    private lateinit var application: Application


    /** 数据库实例 */
    private val db by lazy {
        Room.databaseBuilder(
            application,
            TodoDatabase::class.java,
            "todo"
        ).build()
    }

    fun init(application: Application) {
        this.application = application
    }

    /**
     * 获取 todoDao 实例
     * */
    val todoDao by lazy {
        db.todoDao()
    }

}
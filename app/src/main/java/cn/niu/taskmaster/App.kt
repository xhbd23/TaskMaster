package cn.niu.taskmaster

import android.app.Application
import cn.niu.taskmaster.room.RoomManager
import com.li.utils.LiUtilsApp

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/06
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()

        LiUtilsApp.init(this, true)
        RoomManager.init(this)
    }
}
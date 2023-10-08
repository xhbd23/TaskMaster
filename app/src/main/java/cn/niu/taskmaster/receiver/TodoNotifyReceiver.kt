package cn.niu.taskmaster.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import cn.niu.taskmaster.room.entity.TodoItem
import cn.niu.taskmaster.util.TodoUtils

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/08
 */
class TodoNotifyReceiver(
    private val onItemCompleted: (item:TodoItem)->Unit
): BroadcastReceiver() {
    companion object {
        private const val TAG = "TodoNotifyReceiver"

        const val ACTION_COMPLETE = "android.intent.action.ACTION_COMPLETE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val todo: TodoItem? = intent.getParcelableExtra("todo")
            todo?.let { it1 -> onItemCompleted.invoke(it1) }
            TodoUtils.cancelCurrentNotify()
        }
    }
}
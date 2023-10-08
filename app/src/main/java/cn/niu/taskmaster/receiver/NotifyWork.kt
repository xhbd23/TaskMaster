package cn.niu.taskmaster.receiver

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import cn.niu.taskmaster.room.entity.TodoItem
import cn.niu.taskmaster.util.TodoUtils

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/08
 */
class NotifyWork(
    private val context: Context,
    workerParameters: WorkerParameters,
    private val item: TodoItem
): Worker(context, workerParameters) {
    companion object {
        private const val TAG = "NotifyWork"
    }

    override fun doWork(): Result {
        Log.i("TodoUtils", "doWork: $item")
        TodoUtils.showNotify(context, item)
        return  Result.success()
    }
}
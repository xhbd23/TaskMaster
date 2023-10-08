package cn.niu.taskmaster.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import cn.niu.taskmaster.R
import cn.niu.taskmaster.receiver.TodoNotifyReceiver
import cn.niu.taskmaster.constant.ALERT_DAY_1
import cn.niu.taskmaster.constant.ALERT_DEFAULT
import cn.niu.taskmaster.constant.ALERT_HOUR_1
import cn.niu.taskmaster.constant.ALERT_MIN_30
import cn.niu.taskmaster.constant.ALERT_MIN_5
import cn.niu.taskmaster.constant.PRIORITY_HIGH
import cn.niu.taskmaster.constant.PRIORITY_LOW
import cn.niu.taskmaster.constant.PRIORITY_MEDIUM
import cn.niu.taskmaster.constant.REPEAT_MODE_EVERYDAY
import cn.niu.taskmaster.constant.REPEAT_MODE_EVERY_MONTH
import cn.niu.taskmaster.constant.REPEAT_MODE_EVERY_WEEK
import cn.niu.taskmaster.constant.REPEAT_MODE_NONE
import cn.niu.taskmaster.constant.VALUE_INT_NOT_INITIALIZED
import cn.niu.taskmaster.constant.VALUE_LONG_NOT_INITIALIZED
import cn.niu.taskmaster.receiver.NotifyWork
import cn.niu.taskmaster.room.entity.TodoItem
import cn.niu.taskmaster.ui.MainActivity
import com.li.utils.LiUtilsApp
import com.li.utils.framework.ext.common.asNotNull
import com.li.utils.framework.ext.res.color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.LinkedList
import java.util.Locale
import java.util.Queue
import java.util.concurrent.TimeUnit

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/07
 */
object TodoUtils {

    private val red = LiUtilsApp.application.color(R.color.red_500)

    private val orange = LiUtilsApp.application.color(R.color.orange_500)

    private val blue = LiUtilsApp.application.color(R.color.blue_500)

    private val grey = LiUtilsApp.application.color(R.color.grey_400)

    /**
     * 检查是否已初始化
     * @param value
     * */
    fun checkInitialized(value: Any?): Boolean {
        return when (value) {
            is Int -> value != VALUE_INT_NOT_INITIALIZED
            is Long -> value != VALUE_LONG_NOT_INITIALIZED
            else -> value != null
        }

    }

    fun getTodoItem(
        title: String = "",
        description: String = "",
        deadline: Long = VALUE_LONG_NOT_INITIALIZED,
        priority: Int = VALUE_INT_NOT_INITIALIZED,
        repeatMode: Int = VALUE_INT_NOT_INITIALIZED,
        alertTime: Int = VALUE_INT_NOT_INITIALIZED
    ): TodoItem {
        val curTime = System.currentTimeMillis()
        val (createDate, createTime) = splitTimestamp(curTime)
        return TodoItem(
            id = curTime,
            title,
            description,
            createDate = createDate,
            createTime = createTime,
            deadline,
            priority,
            completed = false,
            repeatMode,
            alertTime = alertTime
        )
    }

    fun getTodoItemWithId(
        id: Long,
        title: String = "",
        description: String = "",
        deadline: Long = VALUE_LONG_NOT_INITIALIZED,
        priority: Int = VALUE_INT_NOT_INITIALIZED,
        repeatMode: Int = VALUE_INT_NOT_INITIALIZED,
        alertTime: Int = VALUE_INT_NOT_INITIALIZED
    ): TodoItem {
        val curTime = id
        val (createDate, createTime) = splitTimestamp(curTime)
        return TodoItem(
            id = id,
            title,
            description,
            createDate = createDate,
            createTime = createTime,
            deadline,
            priority,
            completed = false,
            repeatMode,
            alertTime = alertTime
        )
    }

    /**
     * 将时间戳分为 **日期** 和 **时间** 两部分
     * */
    fun splitTimestamp(timestamp: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        // 获取时间部分
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val time: Long = hour * 3600000L + minute * 60000 + second * 1000

        return Pair(
            timestamp - time, // 日期部分的时间戳
            hour * 3600000L + minute * 60000 + second * 1000 // 时间部分的时间戳
        )
    }


    /**
     * 解析时间戳为日期 “xx月xx日”, 如果时间戳的年份和当前年份不同时，解析为 “xxxx年xx月xx日”
     * */
    fun parseTimestampToDateString(timestamp: Long): String {
        if (!checkInitialized(timestamp)) return ""
        val tCalendar = Calendar.getInstance()
        val pCalendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        val py = pCalendar[Calendar.YEAR]
        val ty = tCalendar[Calendar.YEAR]

        val pm = pCalendar[Calendar.MONTH]
        val tm = tCalendar[Calendar.MONTH]

        val pd = pCalendar[Calendar.DAY_OF_MONTH]
        val td = tCalendar[Calendar.DAY_OF_MONTH]


        val yearSdf = SimpleDateFormat("yyyy", Locale.getDefault())

        // 今年的年份
        val curYear = yearSdf.format(Date())
        // 时间戳的年份
        val date = Date(timestamp)
        val timeYear = yearSdf.format(date)

        val dateSdf = if (curYear == timeYear) {
            SimpleDateFormat("MM月dd日", Locale.getDefault())
        } else {
            SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        }
        return dateSdf.format(date)
    }

    fun setTodoItemDate(tv: TextView, timestamp: Long) {
        if (!checkInitialized(timestamp)) {
            tv.text = ""
            return
        }
        val tCalendar = Calendar.getInstance()
        val pCalendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        val context = tv.context

        val py = pCalendar[Calendar.YEAR]
        val ty = tCalendar[Calendar.YEAR]

        val pm = pCalendar[Calendar.MONTH]
        val tm = tCalendar[Calendar.MONTH]

        val pd = pCalendar[Calendar.DAY_OF_MONTH]
        val td = tCalendar[Calendar.DAY_OF_MONTH]

        val ph = pCalendar[Calendar.HOUR_OF_DAY]
        val th = tCalendar[Calendar.HOUR_OF_DAY]

        val pmm = pCalendar[Calendar.MINUTE]
        val tmm = tCalendar[Calendar.MINUTE]

        if (py < ty
            || (py == ty && pm < tm)
            || (py == ty && pm == tm && pd < td)
            || (py == ty && pm == tm && pd == td && ph < th)
            || (py == ty && pm == tm && pd == td && ph == th && pmm < tmm)
        ) {
            tv.setTextColor(context.color(R.color.red_500))
        } else {
            tv.setTextColor(context.color(R.color.blue_500))
        }
        val ymdFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        val mdFormat = SimpleDateFormat("MM月dd日", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = Date(timestamp)
        val text = when {
            // 不同年份
            py < ty -> {
                ymdFormat.format(date)
            }
            // 今天
            py == ty && pm == tm && pd == td -> {
                timeFormat.format(date)
            }

            else -> {
                mdFormat.format(date)
            }
        }
        tv.text = text
    }

    /**
     * 解析时间戳为时间 “hh:mm”
     * */
    fun parseTimestampToTimeString(timestamp: Long): String {
        if (!checkInitialized(timestamp)) return ""
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = sdf.format(Date(timestamp))
        return time
    }


    /**
     * 判断时间戳为今天内
     * */
    fun checkTimestampIsToday(timestamp: Long): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())
        val dateFromTimestamp = sdf.format(Date(timestamp))

        return currentDate == dateFromTimestamp
    }

    fun getAlertString(value: Int): String? {
        if (!checkInitialized(value)) return null
        return when (value) {
            ALERT_DEFAULT -> "准时"
            ALERT_MIN_5 -> "提前5分钟"
            ALERT_MIN_30 -> "提前30分钟"
            ALERT_HOUR_1 -> "提前1小时"
            ALERT_DAY_1 -> "提前1天"
            else -> null
        }
    }

    fun getPriorityString(priority: Int): String? {
        if (!checkInitialized(priority)) return null
        return when (priority) {
            PRIORITY_LOW -> "低优先级"
            PRIORITY_MEDIUM -> "中优先级"
            PRIORITY_HIGH -> "高优先级"
            else -> null
        }
    }

    fun getPriorityColor(priority: Int): Int {
        if (!checkInitialized(priority)) return grey
        return when (priority) {
            PRIORITY_LOW -> blue
            PRIORITY_MEDIUM -> orange
            PRIORITY_HIGH -> red
            else -> grey
        }
    }

    fun getRepeatString(value: Int): String? {
        if (value == -1 || !checkInitialized(value)) return null
        else return when (value) {
            REPEAT_MODE_NONE -> null
            REPEAT_MODE_EVERYDAY -> "每天"
            REPEAT_MODE_EVERY_WEEK -> "每周"
            REPEAT_MODE_EVERY_MONTH -> "每月"
            else -> null
        }
    }

    private var notifyId = 0


    fun showNotify(context: Context, item: TodoItem) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE).asNotNull<NotificationManager>()
        createChannel(manager)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val completeIntent = Intent(TodoNotifyReceiver.ACTION_COMPLETE)
          .apply {
                putExtra("todo", item)
            }
        val completePendingIntent = PendingIntent.getBroadcast(
            context, 0, completeIntent, PendingIntent.FLAG_IMMUTABLE)

        val notifyBuild = NotificationCompat.Builder(context, CHANNEL_ID)
        val notification = notifyBuild.setSmallIcon(R.mipmap.ic_launcher_round)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentTitle(item.title)
            .setContentText(item.description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_tick, "完成", completePendingIntent)
            .setContentIntent(pendingIntent)
            .build()
        notificationQueue.add(notifyId)
        manager.notify(notifyId ++, notification)
    }

    private val notificationQueue: Queue<Int> = LinkedList<Int>()


    private const val CHANNEL_ID = "1"

    private const val CHANNEL_NAME = "todo"

    private const val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
    private fun createChannel(manager: NotificationManager) {
        //创建通知渠道，Android8.0及以上需要
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_IMPORTANCE)
        manager.createNotificationChannel(notificationChannel)
    }

    fun cancelCurrentNotify() {
        val size = notificationQueue.size
        var i = 0
        while (i < size) {
            val notifyId = notificationQueue.poll() ?: 0
            LiUtilsApp.application.getSystemService(Context.NOTIFICATION_SERVICE).asNotNull<NotificationManager>().cancel(notifyId)
            i ++
        }
    }


    fun startNotifyWorker(context: Context, item: TodoItem) {
        val currentTime = System.currentTimeMillis()
        if (!checkInitialized(item.deadline)) return
        val deadline = item.deadline
        Log.i("TodoUtils", "startNotifyWorker: deadline: $deadline current: $currentTime")
        Log.i("TodoUtils", "startNotifyWorker: dis: ${deadline - currentTime}")
        val eTime = when (item.alertTime) {
            ALERT_DEFAULT -> 0
            ALERT_MIN_5 -> 5 * 60000
            ALERT_MIN_30 -> 30 * 60000
            ALERT_HOUR_1 -> 60 * 60000
            ALERT_DAY_1 -> 24 * 60 * 60000
            else -> 0
        }
        Log.i("TodoUtils", "startNotifyWorker: ealier: $eTime")

        val delay = deadline - eTime - currentTime
        if (delay <= 0) return
        Log.i("TodoUtils", "startNotifyWorker: $delay")
        val request = OneTimeWorkRequestBuilder<NotifyWork>()
            .setInitialDelay(delay, TimeUnit.MICROSECONDS)
            .addTag(item.id.toString())
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }


    fun cancelNotifyWorker(context: Context, item: TodoItem) {
        WorkManager.getInstance(context).cancelAllWorkByTag(item.id.toString())
    }






}
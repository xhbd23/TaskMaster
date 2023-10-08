package cn.niu.taskmaster.room.convert

import androidx.room.TypeConverter
import com.li.utils.framework.ext.common.fromJson
import com.li.utils.framework.ext.common.toJson

/**
 * 用于转换数据
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/06
 */
class TodoItemConverters {
    companion object {
        private const val TAG = "TodoItemConverters"
    }


    @TypeConverter
    fun fromLongList(time: List<Long>): String {
        return time.toJson()!!
    }

    @TypeConverter
    fun toLongList(value: String): List<Long> {
         return value.fromJson<List<Long>>() ?: emptyList()
    }
}
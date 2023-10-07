package cn.niu.taskmaster.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

/**
 * 待办实体
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/06
 */
@Entity(tableName = "todo_item", )
@JsonClass(generateAdapter = true)
data class TodoItem(

    /** 主键，为创建时的时间 */
    @PrimaryKey val id: Long,

    /** 标题 */
    var title: String,

    /** 描述 */
    var description: String,

    /** 创建日期，仅为日期 */
    @ColumnInfo(name = "create_date")
    var createDate: Long,

    /** 创建时间，仅为时间 */
    @ColumnInfo(name = "create_time")
    var createTime: Long,

    /** 截止时间 */
    var deadline: Long,

    /** 优先级 */
    var priority: Int,

    /** 是否完成 */
    var completed: Boolean,

    /** 重复 */
    var repeat: Int,

    /** 提醒时间 */
    val alertTimes: List<Long>,

)

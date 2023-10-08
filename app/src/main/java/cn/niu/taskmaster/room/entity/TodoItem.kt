package cn.niu.taskmaster.room.entity

import android.os.Parcel
import android.os.Parcelable
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
    @PrimaryKey var id: Long,

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
    @ColumnInfo(name = "repeat_mode")
    var repeatMode: Int,

    /** 提醒时间 */
    var alertTime: Int

): Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        title = parcel.readString() ?: "",
        description = parcel.readString() ?: "",
        createDate = parcel.readLong(),
        createTime = parcel.readLong(),
        deadline = parcel.readLong(),
        priority = parcel.readInt(),
        completed = parcel.readByte() != 0.toByte(),
        repeatMode = parcel.readInt(),
        alertTime = parcel.readInt()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(title)
        dest.writeString(description)
        dest.writeLong(createDate)
        dest.writeLong(createTime)
        dest.writeLong(deadline)
        dest.writeInt(priority)
        dest.writeByte(if (completed) 1 else 0)
        dest.writeInt(repeatMode)
        dest.writeInt(alertTime)
    }

    companion object CREATOR : Parcelable.Creator<TodoItem> {
        override fun createFromParcel(parcel: Parcel): TodoItem {
            return TodoItem(parcel)
        }

        override fun newArray(size: Int): Array<TodoItem?> {
            return arrayOfNulls(size)
        }
    }
}

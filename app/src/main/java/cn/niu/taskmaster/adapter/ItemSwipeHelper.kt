package cn.niu.taskmaster.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cn.niu.taskmaster.R
import com.li.utils.framework.ext.common.dp
import com.li.utils.framework.ext.res.color
import com.li.utils.framework.ext.res.drawable
import com.li.utils.framework.ext.res.tint
import com.li.utils.glide.ext.toBitmap

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/07
 */
class ItemSwipeHelper(
    private val adapter: TodoAdapter,
    private val context: Context,
    private val type: Int
) : ItemTouchHelper.Callback() {
    companion object {
        private const val TAG = "ItemSwipeHelper"

        const val TYPE_UNCOMPLETED = 1

        const val TYPE_COMPLETE = 2
    }

    interface OnItemUncompletedListener {
        fun onDelete(position: Int)

        fun onCompleted(position: Int)
    }


    interface OnItemCompleteListener {
        fun onReset(position: Int)

        fun onDelete(position: Int)
    }


    private var mOnItemUncompletedListener: OnItemUncompletedListener? = null


    private var mOnItemCompleteListener: OnItemCompleteListener? = null


    private val mRedPaint = Paint().apply {
        color = context.color(R.color.red_500)
        style = Paint.Style.FILL
    }

    private val mBluePaint = Paint().apply {
        color = context.color(R.color.blue_500)
        style = Paint.Style.FILL
    }

    private val mOrangePaint = Paint().apply {
        color = context.color(R.color.orange_500)
        style = Paint.Style.FILL
    }

    private val mBitmapPaint = Paint().apply {
//        this.
    }

    private val iconSize = 36.dp

    private val mDeleteIcon = context.drawable(R.drawable.ic_delete)?.tint(Color.WHITE)?.toBitmap(iconSize, iconSize)!!

    private val mTickIcon = context.drawable(R.drawable.ic_tick)?.tint(Color.WHITE)?.toBitmap(iconSize, iconSize)!!

    private val mResetIcon = context.drawable(R.drawable.ic_reset)?.tint(Color.WHITE)?.toBitmap(iconSize, iconSize)!!


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT))
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 删除
        if (direction == ItemTouchHelper.LEFT) {
            if (type == TYPE_UNCOMPLETED) {
                mOnItemUncompletedListener?.onDelete(viewHolder.adapterPosition)
            } else {
                mOnItemCompleteListener?.onDelete(viewHolder.adapterPosition)
            }

            // 完成
        } else if (direction == ItemTouchHelper.RIGHT) {
            if (type == TYPE_UNCOMPLETED) {
                mOnItemUncompletedListener?.onCompleted(viewHolder.adapterPosition)
            } else {
                mOnItemCompleteListener?.onReset(viewHolder.adapterPosition)
            }
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val x = viewHolder.itemView.x
        val y = viewHolder.itemView.y
        val itemHeight = viewHolder.itemView.height.toFloat()
        val itemWidth = viewHolder.itemView.width.toFloat()
        val iconTop = (itemHeight - 36.dp) / 2f
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                val rect =
                    RectF(x + itemWidth, y, itemWidth, y + itemHeight)
                c.drawRect(rect, mRedPaint)
                c.drawBitmap(mDeleteIcon, rect.left + 8.dp, y + iconTop, mBitmapPaint)
            } else if (dX > 0) {
                if (type == TYPE_COMPLETE) {
                    val rect =
                        RectF(0f, y, x, y + itemHeight)
                    c.drawRect(rect, mBluePaint)
                    c.drawBitmap(mResetIcon, rect.right - 36.dp - 8.dp, y + iconTop, mBitmapPaint)
                } else {
                    val rect =
                        RectF(0f, y, x, y + itemHeight)
                    c.drawRect(rect, mBluePaint)
                    c.drawBitmap(mTickIcon, rect.right - 36.dp - 8.dp, y + iconTop, mBitmapPaint)
                }
            }
        }
    }


    fun setOnItemUncompletedListener(listener: OnItemUncompletedListener) {
        this.mOnItemUncompletedListener = listener
    }


    fun setOnItemCompleteListener(listener: OnItemCompleteListener) {
        this.mOnItemCompleteListener = listener
    }



}
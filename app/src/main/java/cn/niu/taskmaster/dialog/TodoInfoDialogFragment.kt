package cn.niu.taskmaster.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import cn.niu.taskmaster.R
import cn.niu.taskmaster.constant.ALERT_DAY_1
import cn.niu.taskmaster.constant.ALERT_DEFAULT
import cn.niu.taskmaster.constant.ALERT_HOUR_1
import cn.niu.taskmaster.constant.ALERT_MIN_30
import cn.niu.taskmaster.constant.ALERT_MIN_5
import cn.niu.taskmaster.constant.PRIORITY_HIGH
import cn.niu.taskmaster.constant.PRIORITY_LOW
import cn.niu.taskmaster.constant.PRIORITY_MEDIUM
import cn.niu.taskmaster.constant.PRIORITY_NORMAL
import cn.niu.taskmaster.constant.REPEAT_MODE_EVERYDAY
import cn.niu.taskmaster.constant.REPEAT_MODE_EVERY_MONTH
import cn.niu.taskmaster.constant.REPEAT_MODE_EVERY_WEEK
import cn.niu.taskmaster.constant.REPEAT_MODE_NONE
import cn.niu.taskmaster.constant.REPEAT_MODE_TOW_DAYS
import cn.niu.taskmaster.constant.VALUE_INT_NOT_INITIALIZED
import cn.niu.taskmaster.constant.VALUE_LONG_NOT_INITIALIZED
import cn.niu.taskmaster.databinding.DialogTodoInfoBinding
import cn.niu.taskmaster.databinding.LayoutItemTodoInfoBinding
import cn.niu.taskmaster.room.entity.TodoItem
import cn.niu.taskmaster.ui.MainViewModel
import cn.niu.taskmaster.util.TodoUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.li.utils.framework.base.interfaces.IViewBinding
import com.li.utils.framework.base.interfaces.IViewModel
import com.li.utils.framework.ext.common.dp
import com.li.utils.framework.ext.common.lazyNone
import com.li.utils.framework.ext.res.color
import com.li.utils.framework.ext.res.drawable
import com.li.utils.framework.ext.res.tint
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.setClipViewCornerRadius
import java.util.Calendar

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/08
 */
class TodoInfoDialogFragment :
    DialogFragment(), IViewBinding<DialogTodoInfoBinding>, IViewModel<MainViewModel> {
    companion object {
        private const val TAG = "TodoInfoDialog"

        private const val KEY_ITEM = "item"
        fun newInstance(item: TodoItem?): TodoInfoDialogFragment {
            val fragment = TodoInfoDialogFragment().apply {
                item?.let {
                    arguments = Bundle().apply {
                        putParcelable(KEY_ITEM, it)
                    }
                }
            }
            return fragment
        }
    }

    private var mLayoutInflater: LayoutInflater? = null

    private var mContainer: ViewGroup? = null

    private val binding by lazyNone {
        initViewBinding(mLayoutInflater!!, mContainer)
    }

    private val viewModel by lazyNone {
        initViewModel()
    }


    private val alertStrings = arrayOf("无", "准时", "提前5分钟", "提前30分钟", "提前1小时", "提前1天")

    private val priorityStrings = arrayOf("高优先级", "中优先级", "低优先级", "无")

    private val repeatStrings = arrayOf("无", "每天", "每周", "每月")

    private var clearIcon: Drawable? = null

    private var rightIcon: Drawable? = null

    private var setValueColor: Int = 0

    private var noValueColor: Int = 0

    private var item: TodoItem? = null

    var deadlineDate: Long = VALUE_LONG_NOT_INITIALIZED

    var deadlineHour: Int = VALUE_INT_NOT_INITIALIZED

    var deadlineMinute: Int = VALUE_INT_NOT_INITIALIZED

    var repeatMode = VALUE_INT_NOT_INITIALIZED

    var priority = VALUE_INT_NOT_INITIALIZED

    var alertTime = VALUE_INT_NOT_INITIALIZED

    override fun getCreationExtras(): CreationExtras = defaultViewModelCreationExtras

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    override fun getViewModelFactory(): ViewModelProvider.Factory = defaultViewModelProviderFactory

    override fun initViewModel(): MainViewModel = ViewModelProvider(requireActivity(), getViewModelFactory())[getViewModelClass()]

    override fun initViewBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): DialogTodoInfoBinding {
        return DialogTodoInfoBinding.inflate(layoutInflater, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.Dialog)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mLayoutInflater = inflater
        mContainer = container
        binding.root.setClipViewCornerRadius(8.dp)
        item = arguments?.getParcelable(KEY_ITEM)
        Log.i(TAG, "onCreateView: $item")

        setValueColor = color(R.color.blue_500)
        noValueColor = color(R.color.grey_400)
        clearIcon = drawable(R.drawable.ic_x)?.tint(setValueColor)
        rightIcon = drawable(R.drawable.ic_right)?.tint(noValueColor)

        initView(item)
        return binding.root
    }

    private fun initView(item: TodoItem?) {
        val normalColor = Color.GRAY
        binding.apply {
            includeTime.apply {
                ivIcon.setImageDrawable(drawable(R.drawable.ic_time)?.tint(normalColor))
                tvTitle.text = "设置时间"
                setValue(this, null)
            }
            includeAlert.apply {
                ivIcon.setImageDrawable(drawable(R.drawable.ic_clock)?.tint(normalColor))
                tvTitle.text = "提醒时间"
                setValue(this, null)
            }
            includePriority.apply {
                ivIcon.setImageDrawable(drawable(R.drawable.ic_flag)?.tint(normalColor))
                tvTitle.text = "优先级"
                setValue(this, null)
            }
            includeRepeatMode.apply {
                ivIcon.setImageDrawable(drawable(R.drawable.ic_repeat)?.tint(normalColor))
                tvTitle.text = "设置重复"
                setValue(this, null)
            }
        }

        val calendar = if (TodoUtils.checkInitialized(item?.deadline)) {
            Calendar.getInstance().apply {
                timeInMillis = item?.deadline ?: System.currentTimeMillis()
            }
        } else {
            Calendar.getInstance()
        }
        initDatePicker(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        if (item != null) {
            initData(item)
        }

        binding.includeTime.root.click {
            showTimePickerDialog()
        }
        binding.includePriority.root.click {
            showPriorityDialog()
        }
        binding.includeAlert.root.click {
            showAlertTimesDialog()
        }
        binding.includeRepeatMode.root.click {
            showRepeatModeDialog()
        }
        binding.tvConfirmed.click {
            viewModel.currentItemInfo = TodoItem(
                id = item?.id ?: VALUE_LONG_NOT_INITIALIZED,
                title = item?.title ?: "",
                description = item?.description ?: "",
                deadline = deadlineDate
                        + if (TodoUtils.checkInitialized(deadlineHour)) deadlineHour * 3600000 else 0
                        + if (TodoUtils.checkInitialized(deadlineMinute)) deadlineMinute * 60000 else 0,
                createTime = item?.createTime ?: VALUE_LONG_NOT_INITIALIZED,
                createDate = item?.createDate ?: VALUE_LONG_NOT_INITIALIZED,
                priority = priority,
                completed = false,
                repeatMode = repeatMode,
                alertTime = alertTime
            )
            dismiss()
        }
        binding.tvCancel.click {
            dismiss()
        }
    }

    private fun initDatePicker(year: Int, month: Int, day: Int) {
        Log.i(TAG, "initDatePicker: $year, $month, $day")
        binding.datePicker.init(year, month, day) { view, sy, sm, sd ->
            Calendar.getInstance().apply {
                set(sy, sm, sd)
                deadlineDate = this.timeInMillis
            }
        }
        binding.datePicker.minDate = System.currentTimeMillis()
    }

    private fun initData(item: TodoItem) {

        if (TodoUtils.checkInitialized(item.deadline)) {
            setValue(binding.includeTime, TodoUtils.parseTimestampToTimeString(item.deadline))
            val (a, b) = TodoUtils.splitTimestamp(item.deadline)
            deadlineDate = a
            deadlineHour = (b / 3600000).toInt().takeIf { it >= 0  } ?: VALUE_INT_NOT_INITIALIZED
            deadlineMinute = ((b % 3600000) / 60000).toInt().takeIf { it >= 0 } ?: VALUE_INT_NOT_INITIALIZED
        }
        if (TodoUtils.checkInitialized(item.priority)) {
            setValue(binding.includePriority, TodoUtils.getPriorityString(item.priority))
            priority = item.priority
        }
        if (TodoUtils.checkInitialized(item.repeatMode)) {
            setValue(binding.includeRepeatMode, TodoUtils.getRepeatString(item.repeatMode))
            repeatMode = item.repeatMode
        }
        if (TodoUtils.checkInitialized(item.alertTime)) {
            setValue(binding.includeTime, TodoUtils.getAlertString(item.alertTime))
            alertTime = item.alertTime
        }
    }

    private fun setValue(include: LayoutItemTodoInfoBinding, value: String?) {
        include.apply {
            if (value != null) {
                tvValue.text = value
                tvValue.setTextColor(setValueColor)
                ivRight.setImageDrawable(clearIcon)
                ivRight.isClickable = true
                ivRight.click {
                    setValue(include, null)
                    when (include) {
                        binding.includePriority -> priority = PRIORITY_NORMAL
                        binding.includeTime -> {
                            deadlineHour = VALUE_INT_NOT_INITIALIZED
                            deadlineMinute = VALUE_INT_NOT_INITIALIZED
                        }
                        binding.includeAlert -> {
                            alertTime = VALUE_INT_NOT_INITIALIZED
                        }
                        binding.includeRepeatMode -> {
                            repeatMode = VALUE_INT_NOT_INITIALIZED
                        }
                    }
                }

            } else {
                tvValue.text = "无"
                tvValue.setTextColor(noValueColor)
                ivRight.setImageDrawable(rightIcon)
                ivRight.isClickable = false
            }
        }
    }

    private fun showTimePickerDialog() {
        TimePickerDialog(
            requireContext(),
            R.style.DatePickerDialogTheme,
            { _, hourOfDay, minute ->
                setValue(binding.includeTime, timeToString(hourOfDay, minute))
                deadlineHour = hourOfDay
                deadlineMinute = minute
            },
            9,
            0,
            true
        ).apply { show() }
    }



    private fun showPriorityDialog() {
        val priorityItems = arrayOf(
            PRIORITY_HIGH,
            PRIORITY_MEDIUM,
            PRIORITY_LOW,
            PRIORITY_NORMAL
        )
        var checked = if (TodoUtils.checkInitialized(priority)) priority else -1
        Log.i(TAG, "showPriorityDialog: $checked")
        MaterialAlertDialogBuilder(requireContext(), R.style.DatePickerDialogTheme)
            .setTitle("优先级")
            .setSingleChoiceItems(
                priorityStrings, priorityItems.indexOf(checked)
            ) { _, which ->
                checked = priorityItems[which]
            }
            .setPositiveButton("确定") { _, _ ->
                priority = if (checked == -1) VALUE_INT_NOT_INITIALIZED else checked
                setValue(binding.includePriority, TodoUtils.getPriorityString(priority))
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showAlertTimesDialog() {
        val alertItems = arrayOf(
            -1,
            ALERT_DEFAULT,
            ALERT_MIN_5,
            ALERT_MIN_30,
            ALERT_HOUR_1,
            ALERT_DAY_1
        )
        var checked = if (TodoUtils.checkInitialized(alertTime)) alertTime else -1
        MaterialAlertDialogBuilder(requireContext(), R.style.DatePickerDialogTheme)
            .setTitle("提醒")
            .setSingleChoiceItems(
                alertStrings, alertItems.indexOf(checked)
            ) { _, which ->
                checked = alertItems[which]
            }
            .setPositiveButton("确定") { _, _ ->
                alertTime = checked
                setValue(binding.includeAlert, TodoUtils.getAlertString(alertTime))
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showRepeatModeDialog() {
        val values = arrayOf(
            REPEAT_MODE_NONE,
            REPEAT_MODE_EVERYDAY,
            REPEAT_MODE_TOW_DAYS,
            REPEAT_MODE_EVERY_WEEK,
            REPEAT_MODE_EVERY_MONTH
        )
        var checkedValue = if (TodoUtils.checkInitialized(repeatMode)) repeatMode else REPEAT_MODE_NONE
        MaterialAlertDialogBuilder(requireContext(), R.style.DatePickerDialogTheme)
            .setTitle("重复")
            .setSingleChoiceItems(
                repeatStrings, values.indexOf(checkedValue)
            ) { _, which -> 
                checkedValue = values[which]
                Log.i(TAG, "showRepeatModeDialog: ${checkedValue}")
            }
            .setPositiveButton("确定") { _, _ ->
                repeatMode = checkedValue
                setValue(binding.includeRepeatMode, TodoUtils.getRepeatString(repeatMode))
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun timeToString(hour: Int, minute: Int): String {
        val sb = StringBuilder()
        if (hour < 10) sb.append("0")
        sb.append(hour.toString()).append(":")
        if (minute < 10) sb.append("0")
        sb.append(minute)
        return sb.toString()
    }

}
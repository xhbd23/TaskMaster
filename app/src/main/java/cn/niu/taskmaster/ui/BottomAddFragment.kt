package cn.niu.taskmaster.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import cn.niu.taskmaster.R
import cn.niu.taskmaster.constant.VALUE_LONG_NOT_INITIALIZED
import cn.niu.taskmaster.databinding.FragmentBottomAddBinding
import cn.niu.taskmaster.dialog.TodoInfoDialogFragment
import cn.niu.taskmaster.room.entity.TodoItem
import cn.niu.taskmaster.util.TodoUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.li.utils.LiUtilsApp
import com.li.utils.framework.base.interfaces.IViewBinding
import com.li.utils.framework.base.interfaces.IViewModel
import com.li.utils.framework.ext.common.dp
import com.li.utils.framework.ext.common.lazyNone
import com.li.utils.framework.ext.coroutine.launchOnCreated
import com.li.utils.framework.ext.res.color
import com.li.utils.framework.ext.view.textFlow
import com.li.utils.framework.util.bar.ImeUtils
import com.li.utils.framework.util.bar.SystemBarUtils
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.setClipViewCornerRadius
import com.sll.lib_framework.ext.view.setClipViewCornerTopRadius
import kotlinx.coroutines.launch

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/07
 */
class BottomAddFragment :
    BottomSheetDialogFragment(),
    IViewBinding<FragmentBottomAddBinding>, IViewModel<MainViewModel> {
    companion object {
        private const val TAG = "BottomAddFragment"

        private const val KEY_TMP_ITEM = "tmp"
        fun newInstance(item: TodoItem?, update: Boolean): BottomAddFragment {
            val fragment = BottomAddFragment()
            fragment.updated = update
            item?.let {
                fragment.arguments = Bundle().apply {
                    putParcelable(KEY_TMP_ITEM, it)
                }
            }
            return fragment
        }

    }

    private var updated = false

    private var layoutInflater: LayoutInflater? = null

    private var container: ViewGroup? = null

    private val binding by lazyNone {
        initViewBinding(layoutInflater!!, container)
    }

    private val viewModel by lazyNone {
        initViewModel()
    }

    private var mOnActionListener: OnActionListener? = null

    interface OnActionListener {

        fun onSend()

        fun onDismiss(rootView: View)

        fun onStart(rootView: View)

    }

    private var hasSend = false

    // item的标题
    var itemTitle: String? = null

    // item的描述
    var itemDescription: String? = null

    override fun initViewBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBottomAddBinding = FragmentBottomAddBinding.inflate(layoutInflater, container, false)

    override fun getCreationExtras(): CreationExtras = defaultViewModelCreationExtras

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    override fun getViewModelFactory(): ViewModelProvider.Factory = defaultViewModelProviderFactory

    override fun initViewModel(): MainViewModel =
        ViewModelProvider(requireActivity(), getViewModelFactory())[getViewModelClass()]

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetStyle)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.layoutInflater = inflater
        this.container = container
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            root.setClipViewCornerTopRadius(8.dp)
        }
        ImeUtils.heightListenerBuilder().onChange { _, _, offset ->
            dialog?.window?.decorView?.findViewById<FrameLayout>(com.google.android.material.R.id.container)!!.y -= offset
        }.build(dialog?.window!!)
        initViews()
    }

    override fun onStart() {
        super.onStart()
        mOnActionListener?.onStart(binding.root)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mOnActionListener?.onDismiss(binding.root)
        if (!hasSend) {
            viewModel.setTmpAddTodoItem(
                title = itemTitle ?: "",
                description = itemDescription ?: ""
            )
        }

    }

    fun setOnActionListener(listener: OnActionListener) {
        this.mOnActionListener = listener
    }

    private fun initViews() {
        if (updated) {
            viewModel.tmpAddTodoItem = arguments?.getParcelable(KEY_TMP_ITEM)
        }

        launchOnCreated {
            launch {
                binding.etTitle.textFlow.collect {
                    itemTitle = it
                    setBtConfirmEnable(itemTitle?.isNotEmpty() ?: false)
                }
            }
            launch {
                binding.etDescription.textFlow.collect {
                    itemDescription = it
                }
            }
        }
        setBtConfirmEnable(false)
        viewModel.tmpAddTodoItem?.apply {
            binding.etTitle.setText(title)
            binding.etDescription.setText(description)
        }

        binding.btConfirm.setClipViewCornerRadius(16.dp)
        binding.btConfirm.click {
            mOnActionListener?.onSend()
            hasSend = true
            val title = itemTitle ?: ""
            val description = itemDescription ?: ""

            if (updated) {
                val item = viewModel.currentItemInfo?.let {
                    TodoUtils.getTodoItemWithId(
                        viewModel.tmpAddTodoItem?.id ?: System.currentTimeMillis(),
                        viewModel.tmpAddTodoItem?.title ?: "",
                        viewModel.tmpAddTodoItem?.description ?: "",
                        it.deadline,
                        it.priority,
                        it.repeatMode,
                        it.alertTime
                    )
                } ?: TodoUtils.getTodoItemWithId(
                    viewModel.tmpAddTodoItem?.id ?: System.currentTimeMillis(),
                    title,
                    description,
                )
                viewModel.updateItem(item)
            } else {
                val item = viewModel.currentItemInfo?.let {
                    TodoUtils.getTodoItem(
                        title,
                        description,
                        it.deadline,
                        it.priority,
                        it.repeatMode,
                        it.alertTime
                    )
                } ?: TodoUtils.getTodoItem(
                    title,
                    description,
                )
                viewModel.addItem(item)
            }
            viewModel.resetTmpAddTodoItem()
            viewModel.currentItemInfo = null
            dismissNow()
        }

        binding.tvCalendar.click {
            showInfoDialog()
        }
    }


    private fun setBtConfirmEnable(enable: Boolean) {
        binding.btConfirm.isEnabled = enable
        binding.btConfirm.setBackgroundColor(if (enable) color(R.color.blue_500) else color(R.color.blue_100))
    }

    private fun showInfoDialog() {
        SystemBarUtils.hideIme(requireActivity(), binding.root)
        if (updated) {
            viewModel.currentItemInfo = viewModel.tmpAddTodoItem
        }
        TodoInfoDialogFragment.newInstance(viewModel.currentItemInfo).show(parentFragmentManager, "info")
    }


}
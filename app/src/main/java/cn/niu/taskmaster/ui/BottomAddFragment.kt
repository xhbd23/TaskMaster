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
import cn.niu.taskmaster.databinding.FragmentBottomAddBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.li.utils.framework.base.interfaces.IViewBinding
import com.li.utils.framework.base.interfaces.IViewModel
import com.li.utils.framework.ext.common.dp
import com.li.utils.framework.ext.common.lazyNone
import com.li.utils.framework.ext.coroutine.launchOnCreated
import com.li.utils.framework.util.bar.ImeUtils
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
    IViewBinding<FragmentBottomAddBinding>, IViewModel<MainViewModel>
{
    companion object {
        private const val TAG = "BottomAddFragment"
    }

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

    override fun initViewBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBottomAddBinding = FragmentBottomAddBinding.inflate(layoutInflater, container, false)

    override fun getCreationExtras(): CreationExtras = defaultViewModelCreationExtras

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    override fun getViewModelFactory(): ViewModelProvider.Factory = defaultViewModelProviderFactory

    override fun initViewModel(): MainViewModel = ViewModelProvider(requireActivity(), getViewModelFactory())[getViewModelClass()]

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
    }

    fun setOnActionListener(listener: OnActionListener) {
        this.mOnActionListener = listener
    }

    private fun initViews() {
        launchOnCreated {
            launch {
                viewModel
            }
        }
    }


}
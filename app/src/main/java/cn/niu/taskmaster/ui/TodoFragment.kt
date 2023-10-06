package cn.niu.taskmaster.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import cn.niu.taskmaster.databinding.FragmentTodoBinding
import com.li.utils.framework.base.fragment.BaseMvvmFragment

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/05
 */
class TodoFragment: BaseMvvmFragment<FragmentTodoBinding, MainViewModel>() {
    companion object {
        private const val TAG = "TodoFragment"
    }

    override fun onDefCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {

    }

    override fun initViewBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTodoBinding = FragmentTodoBinding.inflate(layoutInflater, container, false)

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    private fun init() {

    }

}
package cn.niu.taskmaster.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import cn.niu.taskmaster.databinding.FragmentCalendarBinding
import com.li.utils.framework.base.fragment.BaseMvvmFragment

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/05
 */
class CalendarFragment: BaseMvvmFragment<FragmentCalendarBinding, MainViewModel>() {
    companion object {
        private const val TAG = "CalendarFragment"
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
    ): FragmentCalendarBinding = FragmentCalendarBinding.inflate(layoutInflater, container, false)

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java
}
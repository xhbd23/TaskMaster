package cn.niu.taskmaster.ui

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import cn.niu.taskmaster.adapter.TodoAdapter
import cn.niu.taskmaster.databinding.FragmentCalendarBinding
import cn.niu.taskmaster.room.RoomManager
import cn.niu.taskmaster.room.entity.TodoItem
import com.li.utils.framework.base.fragment.BaseMvvmFragment
import com.li.utils.framework.ext.common.asNotNull
import com.li.utils.framework.ext.coroutine.launchIO
import com.li.utils.framework.ext.coroutine.launchMain
import com.li.utils.framework.ext.coroutine.launchOnCreated
import com.sll.lib_framework.ext.view.gone
import com.sll.lib_framework.ext.view.throttleClick
import com.sll.lib_framework.ext.view.visible
import kotlinx.coroutines.Job

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/05
 */
class CalendarFragment : BaseMvvmFragment<FragmentCalendarBinding, MainViewModel>() {
    companion object {
        private const val TAG = "CalendarFragment"
    }

    private var preFlow: Job? = null


    override fun onDefCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
        binding.calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            updateRecyclerView(year, month, dayOfMonth - 1)
        }

        launchOnCreated {
            viewModel.caption.collect {
                binding.tvCaption.text = it
            }
        }
        viewModel.getCaption()
        binding.tvCaption.throttleClick {
            viewModel.getCaption()
        }
    }

    override fun initViewBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCalendarBinding = FragmentCalendarBinding.inflate(layoutInflater, container, false)

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    private fun updateRecyclerView(year: Int, month: Int, dayOfMonth: Int) {
        preFlow?.cancel()
        preFlow = launchIO {
            val date = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.timeInMillis
            RoomManager.todoDao.getTodoItemsByDate(date).collect {
                launchMain {
                    if (it.isEmpty()) {
                        binding.tips.visible()
                        binding.tvCount.gone()
                        binding.recyclerView.gone()
                    } else {
                        binding.tvCount.text = "共有${it.size}条待办"
                        binding.tvCount.visible()
                        binding.tips.gone()
                        binding.recyclerView.visible()
                        binding.recyclerView.adapter =
                            TodoAdapter(requireContext(), it.toMutableList(), viewModel).apply {
                                onItemClickListener = {
                                    viewModel.tmpAddTodoItem = it
                                    requireActivity().asNotNull<MainActivity>().showBottomAddFragment(true)
                                }
                            }
                    }
                }
            }
        }
    }
}
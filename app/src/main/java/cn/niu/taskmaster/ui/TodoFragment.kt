package cn.niu.taskmaster.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cn.niu.taskmaster.R
import cn.niu.taskmaster.adapter.TodoRecyclerAdapter
import cn.niu.taskmaster.databinding.FragmentTodoBinding
import cn.niu.taskmaster.entity.TodoItem
import com.li.utils.framework.base.fragment.BaseMvvmFragment
import com.li.utils.framework.ext.coroutine.launchOnCreated
import com.li.utils.framework.ext.coroutine.launchOnResume
import com.li.utils.framework.ext.res.string
import kotlinx.coroutines.launch

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

    private var expiredAdapter: TodoRecyclerAdapter? = null

    private var todayAdapter: TodoRecyclerAdapter? = null

    private var tomorrowAdapter: TodoRecyclerAdapter? = null

    private var noDateAdapter: TodoRecyclerAdapter? = null

    private var completedAdapter: TodoRecyclerAdapter? = null


    override fun onDefCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        init()
    }

    override fun initViewBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTodoBinding = FragmentTodoBinding.inflate(layoutInflater, container, false)

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    private fun init() {
        initViews()
        initData()
    }

    private fun initViews() {
        binding.apply {
            includeExpired.apply {
                tvTitle.text = string(R.string.expired)
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
                }
            }
            includeToday.apply {
                tvTitle.text = string(R.string.today)
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
                }
            }
            includeTomorrow.apply {
                tvTitle.text = string(R.string.tomorrow)
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
                }
            }
            includeNoDate.apply {
                tvTitle.text = string(R.string.no_date)
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
                }
            }
            includeCompleted.apply {
                tvTitle.text = string(R.string.completed)
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
                }
            }
        }
    }


    private fun initData() {
        viewModel.apply {
            launchOnCreated {
                launch {
                    viewModel.expiredTodo.collect {
                        if (it.size == 0) {

                        }
                    }
                }
            }
        }
    }

    private fun updateAdapter(adapter: TodoRecyclerAdapter, items: MutableList<TodoItem>) {
        if (items.size == 0) {

        }
    }

}
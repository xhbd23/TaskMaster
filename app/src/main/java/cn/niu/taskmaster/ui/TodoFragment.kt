package cn.niu.taskmaster.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import cn.niu.taskmaster.R
import cn.niu.taskmaster.adapter.ItemSwipeHelper
import cn.niu.taskmaster.adapter.TodoAdapter
import cn.niu.taskmaster.databinding.FragmentTodoBinding
import cn.niu.taskmaster.databinding.LayoutTodoPartBinding
import com.li.utils.framework.base.fragment.BaseMvvmFragment
import com.li.utils.framework.ext.common.asNotNull
import com.li.utils.framework.ext.res.string
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.gone
import com.sll.lib_framework.ext.view.visible
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


    private lateinit var uncompletedAdapter: TodoAdapter

    private lateinit var completedAdapter: TodoAdapter

    override fun onDefCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        init()
        binding.searchViewTop.gone()
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
        uncompletedAdapter = TodoAdapter(requireContext(), mutableListOf(), viewModel).also { viewModel.uncompletedAdapter = it }
        completedAdapter = TodoAdapter(requireContext(), mutableListOf(), viewModel).also { viewModel.completeAdapter = it}
        binding.apply {
            includeUncompleted.apply {
                tvTitle.text = string(R.string.uncompleted)
                recyclerView.apply {
                    layoutManager = object: LinearLayoutManager(context) {
                        override fun canScrollVertically(): Boolean = false
                    }.apply { orientation = LinearLayoutManager.VERTICAL }
                    adapter = uncompletedAdapter

                    val callback = ItemSwipeHelper(uncompletedAdapter, requireContext(), ItemSwipeHelper.TYPE_UNCOMPLETED).apply {
                        setOnItemUncompletedListener(object : ItemSwipeHelper.OnItemUncompletedListener {
                            override fun onDelete(position: Int) {
                                viewModel.removeItemAt(false, position)
                                Log.i(TAG, "uncompleted onDelete: $position")
                            }

                            override fun onCompleted(position: Int) {
                                Log.i(TAG, "uncompleted onCompleted: $this")
                                uncompletedAdapter.getItem(position)?.apply {
                                    completed = true
                                    viewModel.swapItemTo(true, position)
                                }
                            }
                        })
                    }
                    val touchHelper = ItemTouchHelper(callback)
                    touchHelper.attachToRecyclerView(this)
                    uncompletedAdapter.onItemClickListener = {
                        viewModel.tmpAddTodoItem = it
                        requireActivity().asNotNull<MainActivity>().showBottomAddFragment(true)
                    }
                }


                var expanded = true
                ivPopup.click {
                    expanded = !expanded
                    setTodoListExpanded(expanded, this)
                }

            }


            includeCompleted.apply {
                tvTitle.text = string(R.string.completed)
                recyclerView.apply {
                    layoutManager = object : LinearLayoutManager(context) {
                        override fun canScrollVertically(): Boolean = false
                    }.apply { orientation = LinearLayoutManager.VERTICAL }
                    adapter = completedAdapter

                    val callback = ItemSwipeHelper(completedAdapter, requireContext(), ItemSwipeHelper.TYPE_COMPLETE).apply {
                        setOnItemCompleteListener(object : ItemSwipeHelper.OnItemCompleteListener {
                            override fun onDelete(position: Int) {
                                viewModel.removeItemAt(true, position)
                                Log.i(TAG, "completed onDelete: $position")
                            }

                            override fun onReset(position: Int) {
                                completedAdapter.getItem(position)?.apply {
                                    completed = false
                                    viewModel.swapItemTo(false, position)
                                }
                            }
                        })
                    }
                    val touchHelper = ItemTouchHelper(callback)
                    touchHelper.attachToRecyclerView(this)
                    completedAdapter.onItemClickListener = {
                        viewModel.tmpAddTodoItem = it
                        requireActivity().asNotNull<MainActivity>().showBottomAddFragment(true)
                    }
                }
                var expanded = true
                ivPopup.click {
                    expanded = !expanded
                    setTodoListExpanded(expanded, this)
                }
            }
        }
    }


    private fun initData() {
        viewModel.initAdapterData()
    }


    private fun setTodoListExpanded(expand: Boolean, include: LayoutTodoPartBinding) {
        // 默认为展开的状态
        if (expand) {
            include.ivPopup.rotation = 180f
            include.ivPopup.tag = false
            include.recyclerView.gone()
        } else {
            include.ivPopup.rotation = 0f
            include.ivPopup.tag = true
            include.recyclerView.visible()
        }
    }

}
package cn.niu.taskmaster.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.niu.taskmaster.R
import cn.niu.taskmaster.databinding.ActivityMainBinding
import cn.niu.taskmaster.receiver.TodoNotifyReceiver
import com.li.utils.framework.base.activity.BaseMvvmActivity
import com.li.utils.framework.ext.coroutine.launchIO
import com.li.utils.framework.ext.coroutine.launchMain
import com.li.utils.framework.util.bar.SystemBarUtils
import com.sll.lib_framework.ext.view.click
import com.sll.lib_framework.ext.view.height

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/05
 */
class MainActivity : BaseMvvmActivity<ActivityMainBinding, MainViewModel>() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val fragments = listOf<Fragment>(
        TodoFragment(),
        CalendarFragment()
    )

    private val menuItemIds = listOf<Int>(
        R.id.item_all,
        R.id.item_calendar
    )

    private var broadcastReceiver: TodoNotifyReceiver? = null

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        }

    private var mBottomAddFragment: BottomAddFragment? = null

    override fun initViewBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun getViewModelClass(): Class<MainViewModel> = MainViewModel::class.java

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onDefCreate(savedInstanceState: Bundle?) {
        SystemBarUtils.immersiveStatusBar(this)
        binding.spaceStatusBar.post {
            binding.spaceStatusBar.height(SystemBarUtils.getStatusBarHeight(this, true))
        }
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)


        broadcastReceiver = TodoNotifyReceiver {
            launchIO {
                it.completed = true
                launchMain {
                    viewModel.swapItem(it)
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(TodoNotifyReceiver.ACTION_COMPLETE)
        }
        // 注册广播
        registerReceiver(broadcastReceiver, intentFilter)
        initViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    private fun initViews() {
        // viewPager2 的 adapter
        binding.viewPager2.adapter =
            object : FragmentStateAdapter(supportFragmentManager, lifecycle) {
                override fun getItemCount(): Int = fragments.size

                override fun createFragment(position: Int): Fragment = fragments[position]
            }
        // 监听滑动，下方的底部栏也会同步更改
        // TODO: 可能存在滑动冲突
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigationView.selectedItemId = menuItemIds[position]
            }
        })
        // 底部栏点击标签的时候切换上方的 viewPager2
        binding.bottomNavigationView.setOnItemSelectedListener {
            binding.viewPager2.setCurrentItem(menuItemIds.indexOf(it.itemId), true)
            return@setOnItemSelectedListener true
        }

        binding.fabAdd.click {
            binding.fabAdd.hide()
            showBottomAddFragment(false)
        }

    }

    fun showBottomAddFragment(update: Boolean) {
        mBottomAddFragment?.dismiss()
        mBottomAddFragment = null
        mBottomAddFragment =
            BottomAddFragment.newInstance(viewModel.tmpAddTodoItem, update).apply {
                setOnActionListener(object : BottomAddFragment.OnActionListener {
                    override fun onSend() {

                    }

                    override fun onDismiss(rootView: View) {
                        binding.fabAdd.show()
                        SystemBarUtils.hideIme(this@MainActivity, rootView)
                    }

                    override fun onStart(rootView: View) {
                        SystemBarUtils.showIme(this@MainActivity, rootView)
                    }
                })
                show(supportFragmentManager, "add-todo")

            }
    }


}

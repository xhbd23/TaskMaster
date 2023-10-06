package cn.niu.taskmaster.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.niu.taskmaster.R
import cn.niu.taskmaster.databinding.ActivityMainBinding
import com.li.utils.framework.base.activity.BaseMvvmActivity

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/05
 */
class MainActivity: BaseMvvmActivity<ActivityMainBinding, ViewModel>() {
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

    override fun initViewBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun getViewModelClass(): Class<ViewModel> = ViewModel::class.java

    override fun onDefCreate(savedInstanceState: Bundle?) {
        initViews()
    }

    private fun initViews() {
        binding.viewPager2.adapter = object : FragmentStateAdapter(supportFragmentManager, lifecycle) {
            override fun getItemCount(): Int = fragments.size

            override fun createFragment(position: Int): Fragment = fragments[position]
        }

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigationView.selectedItemId = menuItemIds[position]
            }
        })

        binding.bottomNavigationView.setOnItemSelectedListener {
            binding.viewPager2.setCurrentItem(menuItemIds.indexOf(it.itemId), true)
            return@setOnItemSelectedListener true
        }
    }


}

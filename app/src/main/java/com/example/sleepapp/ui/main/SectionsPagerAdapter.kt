package com.example.sleepapp.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.sleepapp.*

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager, mainActivity: MainActivity) :
    FragmentPagerAdapter(fm) {

    private lateinit var ma : MainActivity

    init {
        ma = mainActivity
    }

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment = Sleep(ma)
        when (position) {
            0 -> fragment = Sleep(ma)
            1 -> fragment = Past()
            2 -> fragment = Settings(ma)
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 3
    }
}
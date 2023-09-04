package com.yyxnb.android.fragment.container

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.BackStackEntry
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.yyxnb.android.fragment.PageManager
import com.yyxnb.android.fragment.R
import com.yyxnb.android.fragment.generateFragmentTag
import com.yyxnb.android.fragment.mode.LaunchMode.NORMAL
import com.yyxnb.android.fragment.mode.LaunchMode.SINGLE_TASK
import com.yyxnb.android.fragment.mode.LaunchMode.SINGLE_TOP
import com.yyxnb.android.fragment.mode.ReEnterFragment

abstract class PageContainerActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_fragment_activity)
        PageManager.onNavigation = this::load
        if (savedInstanceState != null) return
        startFragment()?.let {
            supportFragmentManager.commit {
                add(R.id.page_container_activity_root, PageContainerFragment().apply {
                    attach(it)
                })
            }
        }
    }

    /**
     * 设置入口Fragment，若为null则不设置入口Fragment，后续Fragment通过路由跳转
     */
    protected abstract fun startFragment(): Fragment?

    override fun onDestroy() {
        super.onDestroy()
        PageManager.onNavigation = null
    }

    private fun FragmentManager.findBackStackEntry(block: (BackStackEntry) -> Boolean): BackStackEntry? {
        for (i in 0 until backStackEntryCount) {
            val entry = getBackStackEntryAt(i)
            if (block(entry)) {
                return entry
            }
        }
        return null
    }

    private fun load(fragment: Fragment) {
        supportFragmentManager.commit {
            val customAnimation = (fragment as? PageContainerFragment)?.customAnimation
            if (customAnimation == null) {
                setCustomAnimations(
                    R.anim.enter_from_right, R.anim.fade_out,
                    R.anim.fade_in, R.anim.exit_to_right
                )
            } else {
                setCustomAnimations(
                    customAnimation.enter, customAnimation.exit,
                    customAnimation.popEnter, customAnimation.popExit)
            }
            val fragmentTag = FragmentTag((fragment as? PageContainerFragment)?.fragmentTag
                ?: fragment.generateFragmentTag)
            when (fragmentTag.launchMode) {
                NORMAL -> launchFragment(fragment)
                SINGLE_TOP -> handleSingleTop(fragment)
                SINGLE_TASK -> handleSingleTask(fragment)
            }
        }
    }

    private fun FragmentTransaction.handleSingleTask(fragment: Fragment) {
        val oldFragmentEntry =
            supportFragmentManager.findBackStackEntry {
                val realFragment = (fragment as? PageContainerFragment)?.fragment
                    ?: fragment
                FragmentTag(it.name).className == realFragment::class.java.canonicalName
            }
        if (oldFragmentEntry != null) {
            supportFragmentManager.popBackStackImmediate(oldFragmentEntry.name, 1)
            val reEnteringFragment =
                supportFragmentManager.findFragmentById(R.id.page_container_activity_root)
            (reEnteringFragment as? PageContainerFragment)?.onNewArguments(
                fragment.arguments
            ) ?: (reEnteringFragment as? ReEnterFragment)?.onNewArguments(fragment.arguments)
        } else {
            launchFragment(fragment)
        }
    }

    private fun FragmentTransaction.handleSingleTop(fragment: Fragment) {
        val currentFragment =
            (currentFragment as? PageContainerFragment)?.fragment ?: currentFragment
        if (currentFragment != null) {
            val lastBackStackEntry =
                FragmentTag((currentFragment as? PageContainerFragment)?.fragmentTag ?: currentFragment.generateFragmentTag)
            val currentBackStackEntry = FragmentTag((fragment as? PageContainerFragment)?.fragmentTag ?: fragment.generateFragmentTag)
            if (lastBackStackEntry.className == currentBackStackEntry.className) {
                (this@PageContainerActivity.currentFragment as? PageContainerFragment)?.onNewArguments(fragment.arguments)
            } else {
                launchFragment(fragment)
            }
        } else {
            launchFragment(fragment)
        }
    }

    private fun FragmentTransaction.launchFragment(fragment: Fragment) {
        currentFragment?.let {
            val keepAlive = (fragment as? PageContainerFragment)?.keepAlive
            if (keepAlive?.value != true) {
                remove(it)
            } else {
                hide(it)
            }
            if (it is PageContainerFragment) {
                addToBackStack(it.fragmentTag)
            } else {
                addToBackStack(it.generateFragmentTag)
            }
        }
        add(R.id.page_container_activity_root, fragment, fragment::class.java.canonicalName)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.page_container_activity_root)

}
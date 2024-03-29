package com.yyxnb.android.fragment.container

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.commit
import com.yyxnb.android.fragment.PageManager
import com.yyxnb.android.fragment.R
import com.yyxnb.android.fragment.annotation.Background
import com.yyxnb.android.fragment.annotation.CustomAnimations
import com.yyxnb.android.fragment.annotation.KeepAlive
import com.yyxnb.android.fragment.annotation.Orientation
import com.yyxnb.android.fragment.annotation.SystemUI
import com.yyxnb.android.fragment.exception.FragmentNotFoundException
import com.yyxnb.android.fragment.generateFragmentTag
import com.yyxnb.android.fragment.mode.ReEnterFragment
import com.yyxnb.android.fragment.utils.hideNavigationBar
import com.yyxnb.android.fragment.utils.hideStatusBar
import com.yyxnb.android.fragment.utils.setStatusBarTransparent
import com.yyxnb.android.fragment.utils.showNavigationBar
import com.yyxnb.android.fragment.utils.showStatusBar


@SuppressLint("SourceLockedOrientationActivity")
internal class PageContainerFragment : Fragment() {
    lateinit var fragmentTag: String
    private var orientation: Int? = null
    private var systemUISettings: SystemUI = SystemUI()
    private var backgroundColor: Int? = null
    var customAnimation: CustomAnimations? = null
        private set

    var keepAlive: KeepAlive? = null
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.page_container_fragment, container, false).apply {
        setBackgroundColor(backgroundColor
            ?: kotlin.runCatching { windowBackground }.getOrNull()
            ?: Color.WHITE)
    }!!

    private var pending: Runnable? = null
    private var result: Bundle? = null

    var fragment: Fragment? = null

    fun popResult(result: Bundle? = null) {
        this.result = result
        parentFragmentManager.popBackStack()
    }

    fun popTo(uri: String): Boolean {
        for (i in parentFragmentManager.backStackEntryCount - 1 downTo 0) {
            val name = parentFragmentManager.getBackStackEntryAt(i).name
            val tag = FragmentTag(name)
            if (tag.uri == uri) {
                parentFragmentManager.popBackStack(name, POP_BACK_STACK_INCLUSIVE)
                return true
            }
        }
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pending?.run()
        pending = null
        mayUpdateFragmentSettings()
    }

    private fun mayUpdateFragmentSettings() {
        if (isAdded && !isHidden && userVisibleHint && isResumed) {
            orientation?.let { requireActivity().requestedOrientation = it }
            systemUISettings.run {
                requireActivity().setStatusBarTransparent(brightnessLight)
                if (hideStatusBar) {
                    requireActivity().hideStatusBar()
                } else {
                    requireActivity().showStatusBar()
                }
                if (hideNavigationBar) {
                    requireActivity().hideNavigationBar()
                } else {
                    requireActivity().showNavigationBar()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fragment?.let {
            PageManager.returnResult(it, result)
        }
    }

    internal fun attach(fragment: Fragment) {
        fragment.checkException()
        this.fragment = fragment
        arguments = fragment.arguments
        fragmentTag = fragment.generateFragmentTag
        fragment::class.java.apply {
            getAnnotation(Orientation::class.java)?.let {
                orientation = it.value
            }
            getAnnotation(SystemUI::class.java)?.let { systemUISettings = it }
            getAnnotation(Background::class.java)?.let { backgroundColor = it.value }
            customAnimation = getAnnotation(CustomAnimations::class.java)
            keepAlive = getAnnotation(KeepAlive::class.java)
        }
        if (isAdded) {
            doAttach(fragment)
        } else {
            pending = Runnable {
                doAttach(fragment)
            }
        }
    }

    private fun doAttach(fragment: Fragment) {
        childFragmentManager.commit {
            add(R.id.page_container_fragment_root, fragment)
        }
    }

    private fun Fragment.checkException() {
        if (this is NullFragment) throw FragmentNotFoundException(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mayUpdateFragmentSettings()
    }

    override fun onResume() {
        super.onResume()
        mayUpdateFragmentSettings()
    }

    fun onNewArguments(newArguments: Bundle?) {
        when (val currentFragment =
            childFragmentManager.findFragmentById(R.id.page_container_fragment_root)) {
            is ReEnterFragment -> currentFragment.onNewArguments(newArguments)
        }
    }

    private val windowBackground: Int
        get() {
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(
                android.R.attr.windowBackground,
                typedValue,
                true
            )
            val colorRes = typedValue.resourceId
            return requireContext().resources.getColor(colorRes)
        }
}
package com.yyxnb.yyxarch.utils

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.yyxnb.yyxarch.base.BaseFragment
import com.yyxnb.yyxarch.utils.FragmentUtils.addFragment
import com.yyxnb.yyxarch.utils.FragmentUtils.dispatchBackPress
import com.yyxnb.yyxarch.utils.FragmentUtils.findFragment
import com.yyxnb.yyxarch.utils.FragmentUtils.getAllFragments
import com.yyxnb.yyxarch.utils.FragmentUtils.getAllFragmentsInStack
import com.yyxnb.yyxarch.utils.FragmentUtils.getFragments
import com.yyxnb.yyxarch.utils.FragmentUtils.getFragmentsInStack
import com.yyxnb.yyxarch.utils.FragmentUtils.getTopShowFragment
import com.yyxnb.yyxarch.utils.FragmentUtils.getTopShowFragmentInStack
import com.yyxnb.yyxarch.utils.FragmentUtils.hideFragment
import com.yyxnb.yyxarch.utils.FragmentUtils.hideShowFragment
import com.yyxnb.yyxarch.utils.FragmentUtils.popAllFragments
import com.yyxnb.yyxarch.utils.FragmentUtils.popFragment
import com.yyxnb.yyxarch.utils.FragmentUtils.popToFragment
import com.yyxnb.yyxarch.utils.FragmentUtils.removeAllFragments
import com.yyxnb.yyxarch.utils.FragmentUtils.removeFragment
import com.yyxnb.yyxarch.utils.FragmentUtils.removeToFragment
import com.yyxnb.yyxarch.utils.FragmentUtils.replaceFragment
import com.yyxnb.yyxarch.utils.FragmentUtils.setBackground
import com.yyxnb.yyxarch.utils.FragmentUtils.setBackgroundColor
import com.yyxnb.yyxarch.utils.FragmentUtils.setBackgroundResource
import com.yyxnb.yyxarch.utils.FragmentUtils.showFragment
import com.yyxnb.yyxarch.utils.log.LogUtils
import java.io.Serializable
import java.util.*

/**
 * add                   : [addFragment]新增 fragment
 * show                  : [showFragment]显示 fragment
 * hide                  : [hideFragment]隐藏 fragment
 * showHide              : [hideShowFragment]先显示后隐藏 fragment
 * replace               : [replaceFragment]替换 fragment
 * pop                   : [popFragment]出栈 fragment
 * popTo                 : [popToFragment]出栈到指定 fragment
 * popAll                : [popAllFragments]出栈所有 fragment
 * remove                : [removeFragment]移除 fragment
 * removeTo              : [removeToFragment]移除到指定 fragment
 * removeAll             : [removeAllFragments]移除所有 fragment
 * getTopShow            : [getTopShowFragment]获取顶部可见 fragment
 * getTopShowInStack     : [getTopShowFragmentInStack]获取栈中顶部可见 fragment
 * getFragments          : [getFragments]获取同级别的 fragment
 * getFragmentsInStack   : [getFragmentsInStack]获取同级别栈中的 fragment
 * getAllFragments       : [getAllFragments]获取所有 fragment
 * getAllFragmentsInStack: [getAllFragmentsInStack]获取栈中所有 fragment
 * findFragment          : [findFragment]查找 fragment
 * dispatchBackPress     : [dispatchBackPress]处理 fragment 回退键
 * setBackgroundColor    : [setBackgroundColor]设置背景色
 * setBackgroundResource : [setBackgroundResource]设置背景资源
 * setBackground         : [setBackground]设置背景
 */

object FragmentUtils : Serializable {

    class Args(var id: Int, var isHide: Boolean, var isAddStack: Boolean)

    class SharedElement(internal var sharedElement: View, internal var name: String)

    class FragmentNode(var fragment: BaseFragment, var next: List<FragmentNode>?) {

        override fun toString(): String {
            return fragment.javaClass.simpleName + "->" + if (next == null || next!!.isEmpty()) "no child" else next!!.toString()
        }
    }

    interface OnBackClickListener {
        fun onBackClick(): Boolean
    }


    private const val TYPE_ADD_FRAGMENT = 0x01
    private const val TYPE_REMOVE_FRAGMENT = 0x01 shl 1
    private const val TYPE_REMOVE_TO_FRAGMENT = 0x01 shl 2
    private const val TYPE_REPLACE_FRAGMENT = 0x01 shl 3
    private const val TYPE_POP_ADD_FRAGMENT = 0x01 shl 4
    private const val TYPE_HIDE_FRAGMENT = 0x01 shl 5
    private const val TYPE_SHOW_FRAGMENT = 0x01 shl 6
    private const val TYPE_HIDE_SHOW_FRAGMENT = 0x01 shl 7

    private const val ARGS_ID = "args_id"
    private const val ARGS_IS_HIDE = "args_is_hide"
    private const val ARGS_IS_ADD_STACK = "args_is_add_stack"



    /**
     * 新增fragment
     *
     * @param fragmentManager fragment管理器
     * @param containerId     布局Id
     * @param fragment        fragment
     * @param isHide          是否显示
     * @param isAddStack      是否入回退栈
     * @return fragment
     */
    @JvmOverloads
    fun addFragment(fragmentManager: FragmentManager,
                    fragment: BaseFragment,
                    containerId: Int,
                    isHide: Boolean = false,
                    isAddStack: Boolean = false): BaseFragment? {
        putArgs(fragment, Args(containerId, isHide, isAddStack))
        return operateFragment(fragmentManager, null, fragment, TYPE_ADD_FRAGMENT)
    }

    /**
     * 新增fragment
     *
     * @param fragmentManager fragment管理器
     * @param containerId     布局Id
     * @param fragment        fragment
     * @param isAddStack      是否入回退栈
     * @param sharedElement   共享元素
     * @return fragment
     */
    fun addFragment(fragmentManager: FragmentManager,
                    fragment: BaseFragment,
                    containerId: Int,
                    isAddStack: Boolean,
                    vararg sharedElement: SharedElement): BaseFragment? {
        putArgs(fragment, Args(containerId, false, isAddStack))
        return operateFragment(fragmentManager, null, fragment, TYPE_ADD_FRAGMENT, *sharedElement)
    }

    /**
     * 新增多个fragment
     *
     * @param fragmentManager fragment管理器
     * @param fragments       fragments
     * @param showIndex       要显示的fragment索引
     * @param containerId     布局Id
     * @return fragment
     */
    fun addFragments(fragmentManager: FragmentManager,
                     fragments: List<BaseFragment>,
                     showIndex: Int,
                     containerId: Int): BaseFragment {
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            if (fragment != null) {
                putArgs(fragment, Args(containerId, showIndex != i, false))
                operateFragment(fragmentManager, null, fragment, TYPE_ADD_FRAGMENT)
            }
        }
        return fragments[showIndex]
    }

    /**
     * 移除fragment
     *
     * @param fragment fragment
     */
    fun removeFragment(fragment: BaseFragment) {
        operateFragment(fragment.fragmentManager!!, null, fragment, TYPE_REMOVE_FRAGMENT)
    }

    /**
     * 移除到指定fragment
     *
     * @param fragment      fragment
     * @param isIncludeSelf 是否包括Fragment类自己
     */
    fun removeToFragment(fragment: BaseFragment, isIncludeSelf: Boolean) {
        operateFragment(fragment.fragmentManager!!, if (isIncludeSelf) fragment else null, fragment, TYPE_REMOVE_TO_FRAGMENT)
    }

    /**
     * 移除同级别fragment
     */
    fun removeFragments(fragmentManager: FragmentManager) {
        val fragments = getFragments(fragmentManager)
        if (fragments.isEmpty()) return
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            if (fragment != null) removeFragment(fragment)
        }
    }

    /**
     * 移除所有fragment
     */
    fun removeAllFragments(fragmentManager: FragmentManager) {
        val fragments = getFragments(fragmentManager)
        if (fragments.isEmpty()) return
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            if (fragment != null) {
                removeAllFragments(fragment.childFragmentManager)
                removeFragment(fragment)
            }
        }
    }

    /**
     * 替换fragment
     *
     * @param srcFragment  源fragment
     * @param destFragment 目标fragment
     * @param isAddStack   是否入回退栈
     * @return 目标fragment
     */
    fun replaceFragment(srcFragment: BaseFragment,
                        destFragment: BaseFragment,
                        isAddStack: Boolean): BaseFragment? {
        if (srcFragment.arguments == null) return null
        val containerId = srcFragment.arguments!!.getInt(ARGS_ID)
        return if (containerId == 0) null else replaceFragment(srcFragment.fragmentManager!!, containerId, destFragment, isAddStack)
    }

    /**
     * 替换fragment
     *
     * @param fragmentManager fragment管理器
     * @param containerId     布局Id
     * @param fragment        fragment
     * @param isAddStack      是否入回退栈
     * @return fragment
     */
    fun replaceFragment(fragmentManager: FragmentManager,
                        containerId: Int,
                        fragment: BaseFragment,
                        isAddStack: Boolean): BaseFragment? {
        putArgs(fragment, Args(containerId, false, isAddStack))
        return operateFragment(fragmentManager, null, fragment, TYPE_REPLACE_FRAGMENT)
    }

    /**
     * 出栈fragment
     *
     * @param fragmentManager fragment管理器
     * @return `true`: 出栈成功<br></br>`false`: 出栈失败
     */
    fun popFragment(fragmentManager: FragmentManager): Boolean {
        return fragmentManager.popBackStackImmediate()
    }

    /**
     * 出栈到指定fragment
     *
     * @param fragmentManager fragment管理器
     * @param fragmentClass   Fragment类
     * @param isIncludeSelf   是否包括Fragment类自己
     * @return `true`: 出栈成功<br></br>`false`: 出栈失败
     */
    fun popToFragment(fragmentManager: FragmentManager,
                      fragmentClass: Class<out BaseFragment>,
                      isIncludeSelf: Boolean): Boolean {
        return fragmentManager.popBackStackImmediate(fragmentClass.name, if (isIncludeSelf) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0)
    }

    /**
     * 出栈同级别fragment
     *
     * @param fragmentManager fragment管理器
     */
    fun popFragments(fragmentManager: FragmentManager) {
        while (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStackImmediate()
        }
    }

    /**
     * 出栈所有fragment
     *
     * @param fragmentManager fragment管理器
     */
    fun popAllFragments(fragmentManager: FragmentManager) {
        val fragments = getFragments(fragmentManager)
        if (fragments.isEmpty()) return
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            if (fragment != null) popAllFragments(fragment.childFragmentManager)
        }
        while (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStackImmediate()
        }
    }

    /**
     * 先出栈后新增fragment
     *
     * @param fragmentManager fragment管理器
     * @param containerId     布局Id
     * @param fragment        fragment
     * @param isAddStack      是否入回退栈
     * @return fragment
     */
    fun popAddFragment(fragmentManager: FragmentManager,
                       containerId: Int,
                       fragment: BaseFragment,
                       isAddStack: Boolean,
                       vararg sharedElement: SharedElement): BaseFragment? {
        putArgs(fragment, Args(containerId, false, isAddStack))
        return operateFragment(fragmentManager, null, fragment, TYPE_POP_ADD_FRAGMENT, *sharedElement)
    }

    /**
     * 先出栈后新增fragment
     *
     * @param fragmentManager fragment管理器
     * @param containerId     布局Id
     * @param fragment        fragment
     * @param isAddStack      是否入回退栈
     * @return fragment
     */
    fun popAddFragment(fragmentManager: FragmentManager,
                       containerId: Int,
                       fragment: BaseFragment,
                       isAddStack: Boolean): BaseFragment? {
        putArgs(fragment, Args(containerId, false, isAddStack))
        return operateFragment(fragmentManager, null, fragment, TYPE_POP_ADD_FRAGMENT)
    }

    /**
     * 隐藏fragment
     *
     * @param fragment fragment
     * @return 隐藏的Fragment
     */
    fun hideFragment(fragment: BaseFragment): BaseFragment? {
        val args = getArgs(fragment)
        if (args != null) {
            putArgs(fragment, Args(args.id, true, args.isAddStack))
        }
        return operateFragment(fragment.fragmentManager!!, null, fragment, TYPE_HIDE_FRAGMENT)
    }

    /**
     * 隐藏同级别fragment
     *
     * @param fragmentManager fragment管理器
     */
    fun hideFragments(fragmentManager: FragmentManager) {
        val fragments = getFragments(fragmentManager)
        if (fragments.isEmpty()) return
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            if (fragment != null) hideFragment(fragment)
        }
    }

    /**
     * 显示fragment
     *
     * @param fragment fragment
     * @return show的Fragment
     */
    fun showFragment(fragment: BaseFragment): BaseFragment? {
        val args = getArgs(fragment)
        if (args != null) {
            putArgs(fragment, Args(args.id, false, args.isAddStack))
        }
        return operateFragment(fragment.fragmentManager!!, null, fragment, TYPE_SHOW_FRAGMENT)
    }

    /**
     * 先隐藏后显示fragment
     *
     * @param hideFragment 需要隐藏的Fragment
     * @param showFragment 需要显示的Fragment
     * @return 显示的Fragment
     */
    fun hideShowFragment(hideFragment: BaseFragment,
                         showFragment: BaseFragment): BaseFragment? {
        var args = getArgs(hideFragment)
        if (args != null) {
            putArgs(hideFragment, Args(args.id, true, args.isAddStack))
        }
        args = getArgs(showFragment)
        if (args != null) {
            putArgs(showFragment, Args(args.id, false, args.isAddStack))
        }
        return operateFragment(showFragment.fragmentManager!!, hideFragment, showFragment, TYPE_HIDE_SHOW_FRAGMENT)
    }

    /**
     * 传参
     *
     * @param fragment fragment
     * @param args     参数
     */
    private fun putArgs(fragment: BaseFragment, args: Args) {
        var bundle = fragment.arguments
        if (bundle == null) {
            bundle = Bundle()
            fragment.arguments = bundle
        }
        bundle.putInt(ARGS_ID, args.id)
        bundle.putBoolean(ARGS_IS_HIDE, args.isHide)
        bundle.putBoolean(ARGS_IS_ADD_STACK, args.isAddStack)
    }

    /**
     * 获取参数
     *
     * @param fragment fragment
     */
    fun getArgs(fragment: BaseFragment): Args? {
        val bundle = fragment.arguments
        return if (bundle == null || bundle.getInt(ARGS_ID) == 0) null else Args(bundle.getInt(ARGS_ID), bundle.getBoolean(ARGS_IS_HIDE), bundle.getBoolean(ARGS_IS_ADD_STACK))
    }

    /**
     * 操作fragment
     *
     * @param fragmentManager fragment管理器
     * @param srcFragment     源fragment
     * @param destFragment    目标fragment
     * @param type            操作类型
     * @param sharedElements  共享元素
     * @return destFragment
     */
    private fun operateFragment(fragmentManager: FragmentManager,
                                srcFragment: BaseFragment?,
                                destFragment: BaseFragment,
                                type: Int,
                                vararg sharedElements: SharedElement): BaseFragment? {
        if (srcFragment === destFragment) return null
        if (srcFragment != null && srcFragment.isRemoving) {
            LogUtils.e(srcFragment.javaClass.name + " is isRemoving")
            return null
        }
//        val name = destFragment.javaClass.name
        val name = UUID.randomUUID().toString()
        val args = destFragment.arguments

        val ft = fragmentManager.beginTransaction()
        ft.setReorderingAllowed(true)
        if (sharedElements == null || sharedElements.size == 0) {
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        } else {
            for (element in sharedElements) {// 添加共享元素动画
                ft.addSharedElement(element.sharedElement, element.name)
            }
        }
        when (type) {
            TYPE_ADD_FRAGMENT -> {
                ft.add(args!!.getInt(ARGS_ID), destFragment, name)
                if (args.getBoolean(ARGS_IS_HIDE)) ft.hide(destFragment)
                if (args.getBoolean(ARGS_IS_ADD_STACK)) ft.addToBackStack(name)
            }
            TYPE_REMOVE_FRAGMENT -> ft.remove(destFragment)
            TYPE_REMOVE_TO_FRAGMENT -> {
                val fragments = getFragments(fragmentManager)
                for (i in fragments.indices.reversed()) {
                    val fragment = fragments[i]
                    if (fragment === destFragment) {
                        if (srcFragment != null) ft.remove(fragment)
                        break
                    }
                    ft.remove(fragment)
                }
            }
            TYPE_REPLACE_FRAGMENT -> {
                ft.replace(args!!.getInt(ARGS_ID), destFragment, name)
                if (args.getBoolean(ARGS_IS_ADD_STACK)) ft.addToBackStack(name)
            }
            TYPE_POP_ADD_FRAGMENT -> {
                popFragment(fragmentManager)
                ft.add(args!!.getInt(ARGS_ID), destFragment, name)
                if (args.getBoolean(ARGS_IS_ADD_STACK)) ft.addToBackStack(name)
            }
            TYPE_HIDE_FRAGMENT -> ft.hide(destFragment)
            TYPE_SHOW_FRAGMENT -> ft.show(destFragment)
            TYPE_HIDE_SHOW_FRAGMENT -> ft.hide(srcFragment!!).show(destFragment)
        }
        ft.commit()
        return destFragment
    }

    /**
     * 获取同级别最后加入的fragment
     *
     * @param fragmentManager fragment管理器
     * @return 最后加入的fragment
     */
    fun getLastAddFragment(fragmentManager: FragmentManager): BaseFragment? {
        return getLastAddFragmentIsInStack(fragmentManager, false)
    }

    /**
     * 获取栈中同级别最后加入的fragment
     *
     * @param fragmentManager fragment管理器
     * @return 最后加入的fragment
     */
    fun getLastAddFragmentInStack(fragmentManager: FragmentManager): BaseFragment? {
        return getLastAddFragmentIsInStack(fragmentManager, true)
    }

    /**
     * 根据栈参数获取同级别最后加入的fragment
     *
     * @param fragmentManager fragment管理器
     * @param isInStack       是否是栈中的
     * @return 栈中最后加入的fragment
     */
    private fun getLastAddFragmentIsInStack(fragmentManager: FragmentManager,
                                            isInStack: Boolean): BaseFragment? {
        val fragments = getFragments(fragmentManager)
        if (fragments.isEmpty()) return null
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            if (fragment != null) {
                if (isInStack) {
                    if (fragment.arguments!!.getBoolean(ARGS_IS_ADD_STACK)) {
                        return fragment
                    }
                } else {
                    return fragment
                }
            }
        }
        return null
    }

    /**
     * 获取顶层可见fragment
     *
     * @param fragmentManager fragment管理器
     * @return 顶层可见fragment
     */
    fun getTopShowFragment(fragmentManager: FragmentManager): BaseFragment? {
        return getTopShowFragmentIsInStack(fragmentManager, null, false)
    }

    /**
     * 获取栈中顶层可见fragment
     *
     * @param fragmentManager fragment管理器
     * @return 栈中顶层可见fragment
     */
    fun getTopShowFragmentInStack(fragmentManager: FragmentManager): BaseFragment? {
        return getTopShowFragmentIsInStack(fragmentManager, null, true)
    }

    /**
     * 根据栈参数获取顶层可见fragment
     *
     * @param fragmentManager fragment管理器
     * @param parentFragment  父fragment
     * @param isInStack       是否是栈中的
     * @return 栈中顶层可见fragment
     */
    private fun getTopShowFragmentIsInStack(fragmentManager: FragmentManager,
                                            parentFragment: BaseFragment?,
                                            isInStack: Boolean): BaseFragment? {
        val fragments = getFragments(fragmentManager)
        if (fragments.isEmpty()) return parentFragment
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            if (fragment != null && fragment.isResumed && fragment.isVisible && fragment.userVisibleHint) {
                if (isInStack) {
                    if (fragment.arguments!!.getBoolean(ARGS_IS_ADD_STACK)) {
                        return getTopShowFragmentIsInStack(fragment.childFragmentManager, fragment, true)
                    }
                } else {
                    return getTopShowFragmentIsInStack(fragment.childFragmentManager, fragment, false)
                }
            }
        }
        return parentFragment
    }

    /**
     * 获取同级别fragment
     *
     * @param fragmentManager fragment管理器
     * @return 同级别的fragments
     */
    fun getFragments(fragmentManager: FragmentManager): List<BaseFragment> {
        return getFragmentsIsInStack(fragmentManager, false)
    }

    /**
     * 获取栈中同级别fragment
     *
     * @param fragmentManager fragment管理器
     * @return 栈中同级别fragment
     */
    fun getFragmentsInStack(fragmentManager: FragmentManager): List<BaseFragment> {
        return getFragmentsIsInStack(fragmentManager, true)
    }

    /**
     * 根据栈参数获取同级别fragment
     *
     * @param fragmentManager fragment管理器
     * @param isInStack       是否是栈中的
     * @return 栈中同级别fragment
     */
    private fun getFragmentsIsInStack(fragmentManager: FragmentManager, isInStack: Boolean): List<BaseFragment> {
        val fragments = fragmentManager.fragments
        if (fragments == null || fragments.isEmpty()) return emptyList()
        val result = ArrayList<BaseFragment>()
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            if (fragment != null) {
                if (isInStack) {
                    if (fragment.arguments!!.getBoolean(ARGS_IS_ADD_STACK)) {
                        result.add(fragment as BaseFragment)
                    }
                } else {
                    result.add(fragment as BaseFragment)
                }
            }
        }
        return result
    }

    /**
     * 获取所有fragment
     *
     * @param fragmentManager fragment管理器
     * @return 所有fragment
     */
    fun getAllFragments(fragmentManager: FragmentManager): List<FragmentNode> {
        return getAllFragmentsIsInStack(fragmentManager, ArrayList(), false)
    }

    /**
     * 获取栈中所有fragment
     *
     * @param fragmentManager fragment管理器
     * @return 所有fragment
     */
    fun getAllFragmentsInStack(fragmentManager: FragmentManager): List<FragmentNode> {
        return getAllFragmentsIsInStack(fragmentManager, ArrayList(), true)
    }

    /**
     * 根据栈参数获取所有fragment
     *
     * 需之前对fragment的操作都借助该工具类
     *
     * @param fragmentManager fragment管理器
     * @param result          结果
     * @param isInStack       是否是栈中的
     * @return 栈中所有fragment
     */
    private fun getAllFragmentsIsInStack(fragmentManager: FragmentManager,
                                         result: MutableList<FragmentNode>,
                                         isInStack: Boolean): List<FragmentNode> {
        val fragments = fragmentManager.fragments
        if (fragments == null || fragments.isEmpty()) return emptyList()
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            if (fragment != null) {
                if (isInStack) {
                    if (fragment.arguments!!.getBoolean(ARGS_IS_ADD_STACK)) {
                        result.add(FragmentNode(fragment as BaseFragment, getAllFragmentsIsInStack(fragment.childFragmentManager, ArrayList(), true)))
                    }
                } else {
                    result.add(FragmentNode(fragment as BaseFragment, getAllFragmentsIsInStack(fragment.childFragmentManager, ArrayList(), false)))
                }
            }
        }
        return result
    }

    /**
     * 获取目标fragment的前一个fragment
     *
     * @param destFragment 目标fragment
     * @return 目标fragment的前一个fragment
     */
    fun getPreFragment(destFragment: Fragment): BaseFragment? {
        val fragmentManager = destFragment.fragmentManager ?: return null
        val fragments = getFragments(fragmentManager)
        var flag = false
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            if (flag && fragment != null) {
                return fragment
            }
            if (fragment === destFragment) {
                flag = true
            }
        }
        return null
    }

    /**
     * 查找fragment
     *
     * @param fragmentManager fragment管理器
     * @param fragmentClass   fragment类
     * @return 查找到的fragment
     */
    fun findFragment(fragmentManager: FragmentManager, fragmentClass: Class<out BaseFragment>): BaseFragment? {
        val fragments = getFragments(fragmentManager)
        return if (fragments.isEmpty()) null else fragmentManager.findFragmentByTag(fragmentClass.name) as BaseFragment?
    }

    /**
     * 处理fragment回退键
     *
     * 如果fragment实现了OnBackClickListener接口，返回`true`: 表示已消费回退键事件，反之则没消费
     *
     * 具体示例见FragmentActivity
     *
     * @param fragment fragment
     * @return 是否消费回退事件
     */

    fun dispatchBackPress(fragment: Fragment): Boolean {
        return dispatchBackPress(fragment.fragmentManager!!)
    }

    /**
     * 处理fragment回退键
     *
     * 如果fragment实现了OnBackClickListener接口，返回`true`: 表示已消费回退键事件，反之则没消费
     *
     * 具体示例见FragmentActivity
     *
     * @param fragmentManager fragment管理器
     * @return 是否消费回退事件
     */
    fun dispatchBackPress(fragmentManager: FragmentManager): Boolean {
        val fragments = fragmentManager.fragments
        if (fragments == null || fragments.isEmpty()) return false
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            if (fragment != null
                    && fragment.isResumed
                    && fragment.isVisible
                    && fragment.userVisibleHint
                    && fragment is OnBackClickListener
                    && (fragment as OnBackClickListener).onBackClick()) {
                return true
            }
        }
        return false
    }

    /**
     * 设置背景色
     *
     * @param fragment fragment
     * @param color    背景色
     */
    fun setBackgroundColor(fragment: BaseFragment, @ColorInt color: Int) {
        val view = fragment.view
        view?.setBackgroundColor(color)
    }

    /**
     * 设置背景资源
     *
     * @param fragment fragment
     * @param resId    资源Id
     */
    fun setBackgroundResource(fragment: BaseFragment, @DrawableRes resId: Int) {
        val view = fragment.view
        view?.setBackgroundResource(resId)
    }

    /**
     * 设置背景
     *
     * @param fragment   fragment
     * @param background 背景
     */
    fun setBackground(fragment: BaseFragment, background: Drawable) {
        val view = fragment.view
        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.background = background
            } else {
                view.setBackgroundDrawable(background)
            }
        }
    }

}

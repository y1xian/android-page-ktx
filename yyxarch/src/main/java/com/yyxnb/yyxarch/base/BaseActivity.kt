package com.yyxnb.yyxarch.base

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import com.yyxnb.yyxarch.utils.ActivityStack
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


/**
 * Description: BaseActivity
 *
 * @author : yyx
 * @date : 2018/6/10
 */
abstract class BaseActivity : AppCompatActivity() {

    protected val TAG = javaClass.canonicalName

    private val generateViewId = android.view.View.generateViewId()

    protected var fragmentContainer: FrameLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFManager = supportFragmentManager

        lifecycle.addObserver(Java8Observer)

        if (initLayoutResID() == 0) {
            fragmentContainer = FrameLayout(this)
            fragmentContainer!!.id = generateViewId
            setContentView(fragmentContainer)
        } else {
            setContentView(initLayoutResID())
        }

        initView(savedInstanceState)

        initViewObservable()

        ActivityStack.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityStack.finishActivity(this)
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    abstract fun initLayoutResID(): Int

    /**
     * 初始化
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化界面观察者的监听
     * 接收数据结果
     */
    open fun initViewObservable() {}


    //*************************跳转*************

    private lateinit var mFManager: FragmentManager
    private val mAtomicInteger = AtomicInteger()
    private val mFragmentStack = ArrayList<BaseFragment>()
    private val mFragmentEntityMap = HashMap<BaseFragment, FragmentStackEntity>()

    companion object {

        val REQUEST_CODE_INVALID = -1

    }

    open class FragmentStackEntity constructor() {
        var isSticky = false
        var requestCode = REQUEST_CODE_INVALID
        var resultCode = Activity.RESULT_CANCELED
        var result: Bundle? = null

    }

    fun <T : BaseFragment> fragment(fragmentClass: Class<T>): Fragment? {

        return Fragment.instantiate(this, fragmentClass.canonicalName)
    }

    fun <T : BaseFragment> fragment(fragmentClass: Class<T>, bundle: Bundle): Fragment? {

        return Fragment.instantiate(this, fragmentClass.canonicalName, bundle)
    }


    /**
     * 跳转 fragment.
     *
     * @param clazz 目标fragment class.
     */
    fun <T : BaseFragment> startFragment(clazz: Class<T>) {
        try {
            val targetFragment = clazz.newInstance()
            startFragment<BaseFragment>(null, targetFragment, true, REQUEST_CODE_INVALID)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 跳转 fragment.
     *
     * @param clazz       目标fragment class.
     * @param stickyStack 是否加入堆栈.
     */
    fun <T : BaseFragment> startFragment(clazz: Class<T>, stickyStack: Boolean) {
        try {
            val targetFragment = clazz.newInstance()
            startFragment<BaseFragment>(null, targetFragment, stickyStack, REQUEST_CODE_INVALID)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 跳转 fragment.
     *
     * @param targetFragment 目标fragment.
     * @param T            [BaseFragment].
     */
    fun <T : BaseFragment> startFragment(targetFragment: T) {
        startFragment(null, targetFragment, true, REQUEST_CODE_INVALID)
    }

    /**
     * 跳转 fragment.
     *
     * @param targetFragment 目标fragment.
     * @param stickyStack    是否加入堆栈.
     * @param T            [BaseFragment].
     */
    fun <T : BaseFragment> startFragment(targetFragment: T, stickyStack: Boolean) {
        startFragment(null, targetFragment, stickyStack, REQUEST_CODE_INVALID)
    }

    /**
     * 跳转 fragment 返回结果.
     *
     * @param clazz       目标fragment.
     * @param requestCode 请求码.
     * @param T        [BaseFragment].
     */
    fun <T : BaseFragment> startFragmentForResult(clazz: Class<T>, requestCode: Int) {
        if (requestCode == REQUEST_CODE_INVALID)
            throw IllegalArgumentException("The requestCode must be positive integer.")
        try {
            val targetFragment = clazz.newInstance()
            startFragment<BaseFragment>(null, targetFragment, true, requestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 跳转 fragment 返回结果.
     *
     * @param targetFragment 目标fragment.
     * @param requestCode    请求码.
     * @param T            [BaseFragment].
     */
    fun <T : BaseFragment> startFragmentForResult(targetFragment: T, requestCode: Int) {
        if (requestCode == REQUEST_CODE_INVALID)
            throw IllegalArgumentException("The requestCode must be positive integer.")
        startFragment(null, targetFragment, true, requestCode)
    }

    /**
     * 跳转 fragment.
     *
     * @param thisFragment 当前fragment.
     * @param thatFragment 目标fragment.
     * @param stickyStack  是否加入堆栈.
     * @param requestCode  请求码.
     * @param T          [BaseFragment].
     */
    fun <T : BaseFragment> startFragment(thisFragment: T?, thatFragment: T,
                                         stickyStack: Boolean, requestCode: Int) {
        var fragmentTransaction = mFManager.beginTransaction()
        if (thisFragment != null) {
            val thisStackEntity = mFragmentEntityMap[thisFragment]
            if (thisStackEntity != null) {
                if (thisStackEntity.isSticky) {
                    thisFragment.onPause()
                    thisFragment.onStop()
                    fragmentTransaction.hide(thisFragment)
                } else {
                    fragmentTransaction.remove(thisFragment).commit()
                    fragmentTransaction.commitNow()
                    fragmentTransaction = mFManager.beginTransaction()

                    mFragmentEntityMap.remove(thisFragment)
                    mFragmentStack.remove(thisFragment)
                }
            }
        }

        val fragmentTag = thatFragment.javaClass.canonicalName!! + mAtomicInteger.incrementAndGet()

        if (initLayoutResID() == 0) {
            fragmentTransaction.add(fragmentContainer!!.id, thatFragment, fragmentTag)
        } else {
            fragmentTransaction.add(initLayoutResID(), thatFragment, fragmentTag)
        }

        fragmentTransaction.addToBackStack(fragmentTag)
        fragmentTransaction.commit()

        val fragmentStackEntity = FragmentStackEntity()
        fragmentStackEntity.isSticky = stickyStack
        fragmentStackEntity.requestCode = requestCode
        thatFragment.setStackEntity(fragmentStackEntity)
        mFragmentEntityMap[thatFragment] = fragmentStackEntity

        mFragmentStack.add(thatFragment)
    }

    /**
     * 回退
     */
    private fun onBackStackFragment(): Boolean {
        if (mFragmentStack.size > 1) {
            mFManager.popBackStack()
            val inFragment = mFragmentStack[mFragmentStack.size - 2]

            val fragmentTransaction = mFManager.beginTransaction()
            fragmentTransaction.show(inFragment)
            fragmentTransaction.commit()

            val outFragment = mFragmentStack[mFragmentStack.size - 1]
            inFragment.onResume()

            val stackEntity = mFragmentEntityMap[outFragment]
            mFragmentStack.remove(outFragment)
            mFragmentEntityMap.remove(outFragment)

            if (stackEntity!!.requestCode != REQUEST_CODE_INVALID && stackEntity.resultCode != Activity.RESULT_CANCELED) {
                inFragment.onFragmentResult(stackEntity.requestCode, stackEntity.resultCode, stackEntity.result)
            }
            return true
        }
        return false
    }

    override fun onBackPressed() {
        if (!onBackStackFragment()) {
            finish()
        }
    }
}

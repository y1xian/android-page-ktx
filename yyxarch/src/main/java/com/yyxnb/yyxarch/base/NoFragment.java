package com.yyxnb.yyxarch.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yyxnb.yyxarch.annotation.ResultCode;

public abstract class NoFragment extends BaseFragment {

    public static final int RESULT_OK = Activity.RESULT_OK;
    public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;

    private static final int REQUEST_CODE_INVALID = CompatActivity.REQUEST_CODE_INVALID;

    /**
     * Create a new instance of a Fragment with the given class name.  This is the same as calling its empty constructor.
     *
     * @param context       context.
     * @param fragmentClass class of fragment.
     * @param <T>           subclass of {@link NoFragment}.
     * @return new instance.
     * @deprecated In {@code Activity} with {@link CompatActivity#fragment(Class)} instead;
     * in the {@code Fragment} width {@link #fragment(Class)} instead.
     */
    @Deprecated
    public static <T extends NoFragment> T instantiate(Context context, Class<T> fragmentClass) {
        //noinspection unchecked
        return (T) instantiate(context, fragmentClass.getName(), null);
    }

    /**
     * Create a new instance of a Fragment with the given class name.  This is the same as calling its empty constructor.
     *
     * @param context       context.
     * @param fragmentClass class of fragment.
     * @param bundle        argument.
     * @param <T>           subclass of {@link NoFragment}.
     * @return new instance.
     * @deprecated In {@code Activity} with {@link CompatActivity#fragment(Class, Bundle)} instead;
     * in the {@code Fragment} width {@link #fragment(Class, Bundle)} instead.
     */
    @Deprecated
    public static <T extends NoFragment> T instantiate(Context context, Class<T> fragmentClass, Bundle bundle) {
        //noinspection unchecked
        return (T) instantiate(context, fragmentClass.getName(), bundle);
    }

    /**
     * Create a new instance of a Fragment with the given class name.  This is the same as calling its empty constructor.
     *
     * @param fragmentClass class of fragment.
     * @param <T>           subclass of {@link NoFragment}.
     * @return new instance.
     */
    public final <T extends NoFragment> T fragment(Class<T> fragmentClass) {
        //noinspection unchecked
        return (T) instantiate(getContext(), fragmentClass.getName(), null);
    }

    /**
     * Create a new instance of a Fragment with the given class name.  This is the same as calling its empty constructor.
     *
     * @param fragmentClass class of fragment.
     * @param bundle        argument.
     * @param <T>           subclass of {@link NoFragment}.
     * @return new instance.
     */
    public final <T extends NoFragment> T fragment(Class<T> fragmentClass, Bundle bundle) {
        //noinspection unchecked
        return (T) instantiate(getContext(), fragmentClass.getName(), bundle);
    }

    /**
     * CompatActivity.
     */
    private CompatActivity mActivity;

    /**
     * Get BaseActivity.
     *
     * @return {@link CompatActivity}.
     */
    protected final CompatActivity getCompatActivity() {
        return mActivity;
    }

    /**
     * Start activity.
     *
     * @param clazz class for activity.
     * @param <T>   {@link Activity}.
     */
//    protected final <T extends Activity> void startActivity(Class<T> clazz) {
//        startActivity(new Intent(mActivity, clazz));
//    }

    /**
     * Start activity and finish my parent.
     *
     * @param clazz class for activity.
     * @param <T>   {@link Activity}.
     */
    protected final <T extends Activity> void startActivityFinish(Class<T> clazz) {
        startActivity(new Intent(mActivity, clazz));
        mActivity.finish();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (CompatActivity) context;
    }

    /**
     * Destroy me.
     */
    public void finish() {
        mActivity.onBackPressed();
    }


    /**
     * Override this method, intercept backPressed of ToolBar.
     *
     * @return true, other wise false.
     */
    public boolean onInterceptToolbarBack() {
        return false;
    }

    // ------------------------- Stack ------------------------- //

    /**
     * Stack info.
     */
    private CompatActivity.FragmentStackEntity mStackEntity;

    /**
     * Set result.
     *
     * @param resultCode result code, one of {@link NoFragment#RESULT_OK}, {@link NoFragment#RESULT_CANCELED}.
     */
    protected final void setResult(@ResultCode int resultCode) {
        mStackEntity.resultCode = resultCode;
    }

    /**
     * Set result.
     *
     * @param resultCode resultCode, use {@link }.
     * @param result     {@link Bundle}.
     */
    protected final void setResult(@ResultCode int resultCode, @NonNull Bundle result) {
        mStackEntity.resultCode = resultCode;
        mStackEntity.result = result;
    }

    /**
     * Get the resultCode for requestCode.
     */
    final void setStackEntity(@NonNull CompatActivity.FragmentStackEntity stackEntity) {
        this.mStackEntity = stackEntity;
    }

    /**
     * You should override it.
     *
     * @param resultCode resultCode.
     * @param result     {@link Bundle}.
     */
    public void onFragmentResult(int requestCode, @ResultCode int resultCode, @Nullable Bundle result) {
    }

    /**
     * Show a fragment.
     *
     * @param clazz fragment class.
     * @param <T>   {@link NoFragment}.
     */
    public final <T extends NoFragment> void startFragment(Class<T> clazz) {
        try {
            NoFragment targetFragment = clazz.newInstance();
            startFragment(targetFragment, true, REQUEST_CODE_INVALID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show a fragment.
     *
     * @param clazz       fragment class.
     * @param stickyStack sticky to back stack.
     * @param <T>         {@link NoFragment}.
     */
    public final <T extends NoFragment> void startFragment(Class<T> clazz, boolean stickyStack) {
        try {
            NoFragment targetFragment = clazz.newInstance();
            startFragment(targetFragment, stickyStack, REQUEST_CODE_INVALID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show a fragment.
     *
     * @param targetFragment fragment to display.
     * @param <T>            {@link NoFragment}.
     */
    public final <T extends NoFragment> void startFragment(T targetFragment) {
        startFragment(targetFragment, true, REQUEST_CODE_INVALID);
    }

    /**
     * Show a fragment.
     *
     * @param targetFragment fragment to display.
     * @param stickyStack    sticky back stack.
     * @param <T>            {@link NoFragment}.
     */
    public final <T extends NoFragment> void startFragment(T targetFragment, boolean stickyStack) {
        startFragment(targetFragment, stickyStack, REQUEST_CODE_INVALID);
    }

    /**
     * Show a fragment for result.
     *
     * @param clazz       fragment to display.
     * @param requestCode requestCode.
     * @param <T>         {@link NoFragment}.
     * @deprecated use {@link #startFragmentForResult(Class, int)} instead.
     */
    @Deprecated
    public final <T extends NoFragment> void startFragmentForResquest(Class<T> clazz, int requestCode) {
        startFragmentForResult(clazz, requestCode);
    }

    /**
     * Show a fragment for result.
     *
     * @param targetFragment fragment to display.
     * @param requestCode    requestCode.
     * @param <T>            {@link NoFragment}.
     * @deprecated use {@link #startFragmentForResult(Class, int)} instead.
     */
    @Deprecated
    public final <T extends NoFragment> void startFragmentForResquest(T targetFragment, int requestCode) {
        startFragmentForResult(targetFragment, requestCode);
    }

    /**
     * Show a fragment for result.
     *
     * @param clazz       fragment to display.
     * @param requestCode requestCode.
     * @param <T>         {@link NoFragment}.
     */
    public final <T extends NoFragment> void startFragmentForResult(Class<T> clazz, int requestCode) {
        try {
            NoFragment targetFragment = clazz.newInstance();
            startFragment(targetFragment, true, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show a fragment for result.
     *
     * @param targetFragment fragment to display.
     * @param requestCode    requestCode.
     * @param <T>            {@link NoFragment}.
     */
    public final <T extends NoFragment> void startFragmentForResult(T targetFragment, int requestCode) {
        startFragment(targetFragment, true, requestCode);
    }

    /**
     * Show a fragment.
     *
     * @param targetFragment fragment to display.
     * @param stickyStack    sticky back stack.
     * @param requestCode    requestCode.
     * @param <T>            {@link NoFragment}.
     */
    private <T extends NoFragment> void startFragment(T targetFragment, boolean stickyStack, int requestCode) {
        mActivity.startFragment(this, targetFragment, stickyStack, requestCode);
    }

}
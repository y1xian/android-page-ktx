package com.yyxnb.yyxarch.ext

import androidx.fragment.app.FragmentActivity
import kotlin.LazyThreadSafetyMode.NONE
import androidx.lifecycle.*
import com.yyxnb.yyxarch.base.mvvm.BaseState
import com.yyxnb.yyxarch.base.mvvm.BaseViewModel

inline fun <S : BaseState> withState(
        viewModel: BaseViewModel<S, *>,
        crossinline block: (S) -> Unit
) {
    block(viewModel.currentState)
}

inline fun <T : Any> LiveData<T>.observeWith(
        lifecycleOwner: LifecycleOwner,
        crossinline onChanged: (T) -> Unit
) {
    observe(lifecycleOwner, Observer {
        it ?: return@Observer
        onChanged.invoke(it)
    })
}

inline fun <reified T : ViewModel> ViewModelProvider.Factory.get(fragmentActivity: FragmentActivity): T =
        ViewModelProviders.of(fragmentActivity, this)[T::class.java]

fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(NONE, initializer)
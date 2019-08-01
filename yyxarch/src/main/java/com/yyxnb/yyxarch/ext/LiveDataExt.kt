package com.yyxnb.yyxarch.ext

import android.arch.lifecycle.*
import kotlin.LazyThreadSafetyMode.NONE
import android.support.v4.app.FragmentActivity
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.android.MainThreadDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> LiveData<T>.toReactiveStream(): Flowable<T> = Flowable
        .create({ emitter: FlowableEmitter<T> ->
            val observer = Observer<T> { data ->
                data?.let {
                    emitter.onNext(it)
                }
            }
            observeForever(observer)

            emitter.setCancellable {
                object : MainThreadDisposable() {
                    override fun onDispose() {
                        removeObserver(observer)
                    }
                }
            }
        }, BackpressureStrategy.LATEST)
//        .subscribeOn(RxSchedulers.ui)
//        .observeOn(observerScheduler)
        .unsubscribeOn(Schedulers.io())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

fun <X, Y> LiveData<X>.map(function: (X) -> Y): LiveData<Y> =
        Transformations.map(this, function)

fun <X, Y> LiveData<X>.switchMap(function: (X) -> LiveData<Y>): LiveData<Y> =
        Transformations.switchMap(this, function)

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
package com.yyxnb.yyxarch.livedatabus;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;


import com.yyxnb.yyxarch.livedatabus.liveevent.LiveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public final class LiveDataBus {

    private final Map<String, BusLiveEvent<Object>> bus;

    private LiveDataBus() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final LiveDataBus DEFAULT_BUS = new LiveDataBus();
    }

    public static LiveDataBus get() {
        return SingletonHolder.DEFAULT_BUS;
    }

    public synchronized <T> Observable<T> with(String key, Class<T> type) {
        if (!bus.containsKey(key)) {
            bus.put(key, new BusLiveEvent<>(key));
        }
        return (Observable<T>) bus.get(key);
    }

    public Observable<Object> with(String key) {
        return with(key, Object.class);
    }

    public interface Observable<T> {
        void setValue(T value);

        void postValue(T value);

        void postValueDelay(T value, long delay, TimeUnit unit);

        void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

        void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

        void observeForever(@NonNull Observer<T> observer);

        void observeStickyForever(@NonNull Observer<T> observer);

        void removeObserver(@NonNull Observer<T> observer);
    }

    private static class BusLiveEvent<T> extends LiveEvent<T> implements Observable<T> {

        private class PostValueTask implements Runnable {
            private Object newValue;

            public PostValueTask(@NonNull Object newValue) {
                this.newValue = newValue;
            }

            @Override
            public void run() {
                setValue((T) newValue);
            }
        }

        @NonNull
        private final String key;
        private Handler mainHandler = new Handler(Looper.getMainLooper());

        private BusLiveEvent(String key) {
            this.key = key;
        }

        @Override
        protected Lifecycle.State observerActiveLevel() {
            return super.observerActiveLevel();
//            return Lifecycle.State.STARTED;
        }

        @Override
        public void postValueDelay(T value, long delay, TimeUnit unit) {
            mainHandler.postDelayed(new PostValueTask(value), unit.convert(delay, unit));
        }

        @Override
        public void removeObserver(@NonNull Observer<T> observer) {
            super.removeObserver(observer);
            if (!hasObservers()) {
                LiveDataBus.get().bus.remove(key);
            }
        }
    }
}

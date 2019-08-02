object Libs {

    //https://github.com/Kotlin/kotlinx.coroutines
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${ProjectProperties.kotlinVersion}"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${ProjectProperties.kotlinVersion}"

    const val koinCore = "org.koin:koin-core:${Versions.koin}"
    const val koinTest = "org.koin:koin-test:${Versions.koin}"
    const val koinAndroid = "org.koin:koin-android:${Versions.koin}"
    const val koinScope = "org.koin:koin-androidx-scope:${Versions.koin}"
    const val koinViewModel = "org.koin:koin-androidx-viewmodel:${Versions.koin}"

    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val design = "com.google.android.material:material:1.1.0-alpha07"
    const val annotation = "androidx.annotation:annotation:${Versions.appCompat}"

    const val lifecycle = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    const val lifecycleJava8 = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle}"
    const val lifecycleKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"

    const val ktxCore = "androidx.core:core-ktx:${Versions.ktxCore}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val materialComponents = "com.google.android.material:material:${Versions.materialComponents}"
    const val navigation = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val safeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
    const val paging = "androidx.paging:paging-runtime-ktx:${Versions.paging}"
    const val preference = "androidx.preference:preference-ktx:${Versions.preference}"
    const val workManager = "androidx.work:work-runtime-ktx:${Versions.workManager}"
    const val workManagerTestHelpers = "androidx.work:work-testing:${Versions.workManager}"

    const val vector = "com.github.haroldadmin:Vector:${Versions.vector}"

    const val epoxy = "com.airbnb.android:epoxy:${Versions.epoxy}"
    const val epoxyProcessor = "com.airbnb.android:epoxy-processor:${Versions.epoxy}"
    const val epoxyDatabinding = "com.airbnb.android:epoxy-databinding:${Versions.epoxy}"
    //glide     https://github.com/bumptech/glide
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    const val lemniscate = "com.github.VladimirWrites:Lemniscate:${Versions.lemniscate}"
    //rxjava2       https://github.com/ReactiveX/RxJava
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val rxBinding = "com.jakewharton.rxbinding2:rxbinding:${Versions.rxBinding}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"

    const val room = "androidx.room:room-runtime:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    //retrofit      https://github.com/square/retrofit
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val retrofitRxjava = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    const val retrofitCoroutines = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
    //https://github.com/square/okhttp
    const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
    const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.loggingInterceptor}"

    const val junit4 = "junit:junit:${Versions.junit4}"
    const val androidxJunitExt = "androidx.test.ext:junit:${Versions.androidxJunitExt}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"
    const val kotlinTest = "io.kotlintest:kotlintest-runner-junit5:${Versions.kotlinTest}"
    const val androidxTestCore = "androidx.arch.core:core-testing:${Versions.androidxTestCore}"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"
    const val mockkAndroid = "io.mockk:mockk-android:${Versions.mockk}"
    //https://github.com/google/gson
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    //内存泄漏    https://github.com/square/leakcanary
    const val leak_canary_debug = "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"
    const val leak_canary_debug_support = "com.squareup.leakcanary:leakcanary-support-fragment:${Versions.leakcanary}"
    const val leak_canary_release = "com.squareup.leakcanary:leakcanary-android-no-op:${Versions.leakcanary}"

    const val multidex = "com.android.support:multidex:${Versions.multidex}"
    //https://github.com/Tencent/MMKV
    const val mmkv = "com.tencent:mmkv:1.0.22"
    //https://github.com/JeremyLiao/LiveEventBus
    const val live_event_bus = "com.jeremyliao:live-event-bus-x:1.4.4"
    //https://github.com/anzewei/ParallaxBackLayout
    const val parallaxbacklayout = "com.github.anzewei:parallaxbacklayout:1.1.9"
}
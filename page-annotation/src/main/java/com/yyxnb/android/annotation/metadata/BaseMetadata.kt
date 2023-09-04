package com.yyxnb.android.annotation.metadata

abstract class BaseMetadata {
    fun inject() {
        val ktx = Class.forName("com.yyxnb.android.Initializer_${this::class.java.canonicalName.replace('.', '_')}Kt")
        ktx.getMethod("_inject", this::class.java).invoke(ktx, this)
    }
}
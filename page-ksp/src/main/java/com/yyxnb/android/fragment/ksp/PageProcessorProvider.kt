package com.yyxnb.android.fragment.ksp

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class PageProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) = PageUriProcessor(environment)
}
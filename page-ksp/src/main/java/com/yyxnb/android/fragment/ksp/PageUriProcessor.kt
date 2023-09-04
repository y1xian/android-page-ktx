package com.yyxnb.android.fragment.ksp

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.yyxnb.android.annotation.PageMetadata
import com.yyxnb.android.annotation.PageUri

private const val FRAGMENT_ROUTE_MAP_CLASS = "com.yyxnb.android.fragment.RouteMap"
private const val ACTIVITY_ROUTE_MAP_CLASS = "com.yyxnb.android.RouteMap"

class PageUriProcessor(
    private val processingEnv: SymbolProcessorEnvironment
) : SymbolProcessor {
    private var round = 0
    private val logger = processingEnv.logger
    private val codeGenerator = processingEnv.codeGenerator

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (round != 0) return emptyList()

        val metadataSymbols = resolver.getSymbolsWithAnnotation(PageMetadata::class.qualifiedName!!)
        val metadataElements = metadataSymbols.filterIsInstance<KSClassDeclaration>()
        val metadataList = mutableListOf<PageMetadataEntry>()
        metadataElements.forEach {
            val packageName = it.packageName.asString()
            val type = it.asType(emptyList())
            metadataList.add(PageMetadataEntry(packageName, type.toString()))
        }
        logger.warn("metadataList = $metadataList")
        val activityClass = resolver.getClassDeclarationByName("android.app.Activity")?.asType(emptyList()) ?: return emptyList()
        val fragmentClass = resolver.getClassDeclarationByName("androidx.fragment.app.Fragment")?.asType(emptyList())
        val fragmentRouteMap = resolver.getClassDeclarationByName(FRAGMENT_ROUTE_MAP_CLASS)
        val activityRouteMap = resolver.getClassDeclarationByName(ACTIVITY_ROUTE_MAP_CLASS)

        val symbols = resolver.getSymbolsWithAnnotation(PageUri::class.qualifiedName!!)
        val elements = symbols.filterIsInstance<KSClassDeclaration>()
        val fragmentEntries = mutableListOf<PageUriEntry>()
        val activityEntries = mutableListOf<PageUriEntry>()
        elements.forEach {
            val packageName = it.packageName.asString()
            val type = it.asType(emptyList())

            if (fragmentClass?.isAssignableFrom(type) == true && fragmentRouteMap != null) {
                it.annotations.forEach { ks ->
                    if (ks.shortName.asString() == PageUri::class.java.simpleName) {
                        ks.arguments.forEach { arg ->
                            if (arg.name?.asString() == "value") {
                                logger.warn("bind ${arg.value} to $packageName.$type")
                                val uris = if (arg.value is String) {
                                    listOf(arg.value as String)
                                } else arg.value as List<String>
                                fragmentEntries.add(
                                    PageUriEntry(
                                        packageName, "$type", uris
                                    )
                                )
                            }
                        }
                    }
                }
            }
            if (activityClass.isAssignableFrom(type) && activityRouteMap != null) {
                it.annotations.forEach { ks ->
                    if (ks.shortName.asString() == PageUri::class.java.simpleName) {
                        ks.arguments.forEach { arg ->
                            if (arg.name?.asString() == "value") {
                                logger.warn("bind ${arg.value} to $packageName.$type")
                                val uris = if (arg.value is String) {
                                    listOf(arg.value as String)
                                } else arg.value as List<String>
                                activityEntries.add(
                                    PageUriEntry(
                                        packageName, "$type", uris
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        metadataList.firstOrNull()?.let {
            val metadataClassName = "${it.packageName}.${it.className}"
            val metadata = ClassName(it.packageName, it.className)
            val fileSpec = FileSpec.builder("com.yyxnb.android", "Initializer\$${metadataClassName.replace('.', '$')}")
                .addFunction(FunSpec.builder("_inject")
                    .receiver(metadata)
                    .apply {
                        fragmentEntries.forEach {
                            it.uris.forEach { uri ->
                                addStatement("$FRAGMENT_ROUTE_MAP_CLASS.register(%S, %T::class.java)", uri, ClassName(it.packageName, it.className))
                            }
                        }
                        activityEntries.forEach {
                            it.uris.forEach { uri ->
                                addStatement("$ACTIVITY_ROUTE_MAP_CLASS.register(%S, %T::class.java)", uri, ClassName(it.packageName, it.className))
                            }
                        }
                    }
                    .build())
                .build()

            codeGenerator.createNewFile(Dependencies.ALL_FILES, fileSpec.packageName, fileSpec.name).use {
                it.write(fileSpec.toString().toByteArray())
            }
        }

        round++
        return emptyList()
    }
}
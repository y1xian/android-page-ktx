package com.yyxnb.android.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.yyxnb.android.fragment.container.NullFragment
import com.yyxnb.android.utils.baseUri
import java.util.concurrent.ConcurrentHashMap

/**
 * 路由表，可手动注册，也可通过ksp动态注册
 */
object RouteMap {
    const val KEY_URI = "uri"
    private val routeMetadataMap = ConcurrentHashMap<String, Class<out Fragment>>()

    fun register(baseUri: String, fragment: Class<out Fragment>) {
        val confirmedBaseUri = baseUri.baseUri
        routeMetadataMap[confirmedBaseUri] = fragment
    }

    fun get(uri: String): Fragment = get(Uri.parse(uri))

    fun get(uri: Uri): Fragment = (routeMetadataMap[uri.baseUri]?.newInstance() ?: NullFragment()).apply {
        arguments = (arguments ?: Bundle()).apply {
            putParcelable(KEY_URI, uri)
        }
    }
}

private val String.baseUri: String
    get() = split("?").first()

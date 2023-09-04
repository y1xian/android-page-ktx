package com.yyxnb.android.fragment.exception

import android.net.Uri
import com.yyxnb.android.fragment.RouteMap
import com.yyxnb.android.fragment.container.NullFragment
import com.yyxnb.android.fragment.uriOrNull

class FragmentNotFoundException(fragment: NullFragment) : RuntimeException(
    "Cannot find fragment with uri: ${
        fragment.arguments?.getParcelable<Uri>(
            RouteMap.KEY_URI
        )
    }"
) {
    val uri = fragment.arguments?.uriOrNull
}
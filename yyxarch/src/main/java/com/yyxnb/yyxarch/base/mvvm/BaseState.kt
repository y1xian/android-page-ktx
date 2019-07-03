package com.yyxnb.yyxarch.base.mvvm

import java.io.Serializable

/**
 * A state object contains all the necessary information to
 * render the view.
 *
 * Can be used with Kotlin data classes to allow for easy mutation
 * using the generated `copy` function.
 */
interface BaseState : Serializable
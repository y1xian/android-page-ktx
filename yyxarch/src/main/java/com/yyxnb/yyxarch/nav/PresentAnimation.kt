package com.yyxnb.yyxarch.nav

import androidx.annotation.AnimRes

import com.yyxnb.yyxarch.R

/**
 * PresentAnimation
 */
enum class PresentAnimation private constructor(@field:AnimRes
                                                internal var enter: Int, @field:AnimRes
                                                internal var exit: Int, @field:AnimRes
                                                internal var popEnter: Int, @field:AnimRes
                                                internal var popExit: Int) {

    Push(R.anim.nav_slide_in_right, R.anim.nav_slide_out_left, R.anim.nav_slide_in_left, R.anim.nav_slide_out_right),
    Modal(R.anim.nav_slide_up, R.anim.nav_delay, R.anim.nav_delay, R.anim.nav_slide_down),
    Delay(R.anim.nav_delay, R.anim.nav_delay, R.anim.nav_delay, R.anim.nav_delay),
    Fade(R.anim.nav_fade_in, R.anim.nav_fade_out, R.anim.nav_fade_in, R.anim.nav_fade_out),
    None(R.anim.nav_none, R.anim.nav_none, R.anim.nav_none, R.anim.nav_none)

}

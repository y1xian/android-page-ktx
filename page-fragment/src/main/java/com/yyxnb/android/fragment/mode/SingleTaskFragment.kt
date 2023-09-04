package com.yyxnb.android.fragment.mode

import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class SingleTaskFragment : Fragment(), ReEnterFragment {
    override fun onNewArguments(arguments: Bundle?) = Unit
}
package com.paperatus.swipe.data

import ktx.collections.GdxArray
import ktx.collections.lastIndex

fun <T> GdxArray<T>.lastItem(offset: Int = 0): T = this[lastIndex - offset]

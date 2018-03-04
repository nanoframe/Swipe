package com.paperatus.swipe.core

import com.badlogic.gdx.physics.box2d.Body
import ktx.collections.GdxArray

data class Size(var width: Float = 0.0f, var height: Float = 0.0f) {
    fun set(w: Float, h: Float) {
        width = w
        height = h
    }
}

private val temp = GdxArray<Any>()
fun <T> GdxArray<T>.filterBy(condition: (it: T) -> Boolean): GdxArray<T> {
    temp.clear()

    forEach {
        if (condition(it)) {
            temp.add(it)
        }
    }
    return temp as GdxArray<T>
}

inline fun <reified T> GdxArray<*>.filterByType(): GdxArray<T> {
    val arr = filterBy { it is T }
    return arr as GdxArray<T>
}

fun Body.getGameObject(): GameObject? {
    return userData as? GameObject
}

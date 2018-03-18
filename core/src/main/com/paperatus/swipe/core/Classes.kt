package com.paperatus.swipe.core

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import ktx.collections.GdxArray

data class Size(var width: Float = 0.0f, var height: Float = 0.0f) {
    fun set(w: Float, h: Float) {
        width = w
        height = h
    }
}

class Scale(x: Float = 1.0f, y: Float = 1.0f) : Vector2(x, y) {
    constructor(scl: Float) : this(scl, scl)

    fun set(scale: Float) = set(scale, scale)
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

fun <T> GdxArray<T>.getOrNull(index: Int) : T? =
        if (index >= size) null else this[index]

fun Body.getGameObject(): GameObject? {
    return userData as? GameObject
}

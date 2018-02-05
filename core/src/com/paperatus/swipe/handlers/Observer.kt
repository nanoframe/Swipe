package com.paperatus.swipe.handlers

import com.paperatus.swipe.Game
import ktx.collections.GdxArray

abstract class Observer(var game: Game) {
    abstract fun receive(what: Int)
}

open class Subject {
    private var observers = GdxArray<Observer>()

    fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    fun removeObserver(observer: Observer) {
        observers.removeValue(observer, true)
    }

    protected fun post(what: Int) {
        observers.forEach { it.receive(what) }
    }
}

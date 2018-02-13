package com.paperatus.swipe.core

import ktx.collections.GdxArray

/**
 * Allows a class to receive events without coupling code together.
 */
interface Observer {
    fun receive(what: Int)
}

/**
 * Provides a way to post notifications to handle events without coupling classes together.
 *
 * @property observers listeners that will observe for events.
 */
open class Subject {
    private val observers = GdxArray<Observer>()

    /**
     * Adds an observer to the object.
     *
     * @param observer the observer to attach to this object
     */
    fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    /**
     * Removes the observer from the object.
     *
     * @param observer the observer to detach from this object.
     */
    fun removeObserver(observer: Observer) {
        observers.removeValue(observer, true)
    }

    /**
     * Notifies all observers of an event.
     *
     * @param what the message to notify.
     */
    protected fun post(what: Int) {
        observers.forEach { it.receive(what) }
    }
}

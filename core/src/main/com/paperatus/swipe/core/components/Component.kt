package com.paperatus.swipe.core.components

import com.paperatus.swipe.core.scene.GameObject

typealias ComponentMessage = Int

/**
 * Components that can be attached to a GameObject instance to extend functionality.
 */
interface Component {

    /**
     * Updates the given GameObject.
     *
     * @param gameObject current GameObject.
     */
    fun update(delta: Float, gameObject: GameObject)

    /**
     * Receives a message sent by other components
     */
    fun receive(what: ComponentMessage, payload: Any? = null)
}

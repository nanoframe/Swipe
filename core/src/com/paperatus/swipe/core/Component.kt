package com.paperatus.swipe.core

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import ktx.collections.GdxArray

/**
 * Components that can be attached to a GameObject instance to extend functionality.
 *
 * @property order update order in which the component should be updated.
 */
interface Component {
    enum class Message {
        MOVEMENT
    }

    enum class Order {
        PRE_UPDATE, UPDATE, POST_UPDATE,
        PRE_RENDER, RENDER, POST_RENDER,
        MANUAL
    }

    val order: Order

    /**
     * Updates the given GameObject.
     *
     * @param gameObject current GameObject.
     */
    fun update(gameObject: GameObject)

    /**
     * Receives a message sent by other components
     */
    fun receive(what: Message, payload: Any? = null)
}

/**
 * Handles user input.
 */
abstract class InputComponent : Component {
    override val order = Component.Order.PRE_UPDATE
}

/**
 * Provides a Physics system to a GameObject.
 */
abstract class PhysicsComponent : Component {
    override val order = Component.Order.POST_UPDATE
    private val contactListeners = GdxArray<ContactListener>()
    var onInit: ((Body) -> Unit)? = null

    /**
     * Called upon initialization to create Body instances.
     *
     * @param world Box2D game world.
     */
    abstract fun init(world: World)

    abstract fun destroy(world: World)

    abstract fun getBody(): Body

    fun addContactListener(contactListener: ContactListener) {
        contactListeners.add(contactListener)
    }

    fun removeContactListener(contactListener: ContactListener) {
        contactListeners.removeValue(contactListener, true)
    }

    fun postCollisionStart(other: GameObject) {
        contactListeners.forEach { it.onContactBegin(other) }
    }

    fun postCollisionEnd(other: GameObject) {
        contactListeners.forEach { it.onContactEnd(other) }
    }

    interface ContactListener {
        fun onContactBegin(other: GameObject)
        fun onContactEnd(other: GameObject)
    }
}

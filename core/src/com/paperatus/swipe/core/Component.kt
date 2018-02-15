package com.paperatus.swipe.core

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World

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

    /**
     * Called upon initialization to create Body instances.
     *
     * @param world Box2D game world.
     */
    abstract fun init(world: World)

    abstract fun getBody() : Body
}

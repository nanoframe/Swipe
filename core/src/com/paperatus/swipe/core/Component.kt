package com.paperatus.swipe.core

import com.badlogic.gdx.physics.box2d.World

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

    fun update(character: GameObject)

    fun receive(what: Message, payload: Any? = null)
}

/**
 * The Input component of the Player class.
 */
abstract class InputComponent : Component {
    override val order = Component.Order.PRE_UPDATE
}

abstract class PhysicsComponent : Component {
    override val order = Component.Order.POST_UPDATE

    abstract fun init(world: World)
}

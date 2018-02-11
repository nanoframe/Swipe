package com.paperatus.swipe.handlers

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.paperatus.swipe.objects.GameObject

interface Component {
    enum class Update {
        PRE_UPDATE, UPDATE, POST_UPDATE,
        PRE_RENDER, RENDER, POST_RENDER,
        MANUAL
    }

    val update: Update

    fun update(character: GameObject)
}

/**
 * The Input component of the Player class.
 */
abstract class InputComponent : Component {
    override val update = Component.Update.PRE_UPDATE
}

/**
 * Provides touch-event based input to control the Player
 */
class PlayerTouchInput : InputComponent() {
    override fun update(character: GameObject) {

    }

}

abstract class PhysicsComponent : Component {
    override val update = Component.Update.PRE_UPDATE

    abstract fun initBody(world: World)
}

class PlayerPhysicsComponent : PhysicsComponent() {
    override fun initBody(world: World) {

    }

    override fun update(character: GameObject) {
    }

}
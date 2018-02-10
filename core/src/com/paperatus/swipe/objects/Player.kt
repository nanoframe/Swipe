package com.paperatus.swipe.objects

import com.badlogic.gdx.physics.box2d.BodyDef
import com.paperatus.swipe.handlers.InputComponent

/**
 * The Player of the game.
 *
 * @param inputComponent component that handles the input and controls the Player.
 */
class Player(private val inputComponent: InputComponent) : PhysicsObject() {
    override fun getBodyDef(): BodyDef {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.fixedRotation = false
        bodyDef.linearDamping = 1.0f
        return bodyDef
    }

    init {
        bounds.set(0.0f, 0.0f, 1.5f, 1.5f)
    }

    override fun update(delta: Float) {
        inputComponent.updateInput(this)

    }
}
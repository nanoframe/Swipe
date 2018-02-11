package com.paperatus.swipe.scene

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.paperatus.swipe.Game

abstract class PhysicsScene(game: Game,
                            gravity: Vector2,
                            doSleep: Boolean = true) : Scene(game) {
    private val physicsWorld = World(gravity, doSleep)

    override fun update(delta: Float) {
        physicsWorld.step(delta, 6, 2)
    }


}
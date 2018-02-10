package com.paperatus.swipe.objects

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef


abstract class PhysicsObject : GameObject() {
    var body: Body? = null

    abstract fun getBodyDef() : BodyDef
}

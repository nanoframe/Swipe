package com.paperatus.swipe.core

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.physics.box2d.World
import com.paperatus.swipe.Game

abstract class PhysicsScene(game: Game,
                            gravity: Vector2,
                            doSleep: Boolean = true) : ObjectScene(game) {
    protected val physicsWorld = World(gravity, doSleep)

    init {
        physicsWorld.setContactListener(object: ContactListener {
            override fun beginContact(contact: Contact?) {
                contact?.let {
                    val body1 = it.fixtureA.body
                    val body2 = it.fixtureB.body
                    val object1 = body1.getGameObject()
                    val object2 = body2.getGameObject()

                    if (object1 != null && object2 != null) {
                        object1.getComponent<PhysicsComponent>()?.postCollisionStart(object2)
                        object2.getComponent<PhysicsComponent>()?.postCollisionStart(object1)
                    }
                }
            }

            override fun endContact(contact: Contact?) {
                contact?.let {
                    val body1 = it.fixtureA.body
                    val body2 = it.fixtureB.body
                    val object1 = body1.getGameObject()
                    val object2 = body2.getGameObject()

                    if (object1 != null && object2 != null) {
                        object1.getComponent<PhysicsComponent>()?.postCollisionStart(object2)
                        object2.getComponent<PhysicsComponent>()?.postCollisionStart(object1)
                    }
                }
            }

            override fun preSolve(contact: Contact?, oldManifold: Manifold?) = Unit
            override fun postSolve(contact: Contact?, impulse: ContactImpulse?) = Unit
        })
    }

    private val debugRenderer: Box2DDebugRenderer by lazy {
        Box2DDebugRenderer()
    }

    override fun update(delta: Float) {
        super.update(delta)

        physicsWorld.step(delta, 6, 2)
    }

    protected fun debugRender(camera: Camera) {
        debugRenderer.render(physicsWorld, camera.combined)
    }

    override fun addObject(gameObject: GameObject) {
        super.addObject(gameObject)

        val component = gameObject.getComponent<PhysicsComponent>()
        if (component != null) {
            component.init(physicsWorld)
            component.onInit?.invoke(component.getBody())
            component.getBody().userData = gameObject
        }
    }

    override fun removeObject(gameObject: GameObject, identity: Boolean): Boolean {
        val status = super.removeObject(gameObject, identity)

        val component = gameObject.getComponent<PhysicsComponent>()
        if (component != null) {
            component.destroy(physicsWorld)
        }

        return status
    }


}

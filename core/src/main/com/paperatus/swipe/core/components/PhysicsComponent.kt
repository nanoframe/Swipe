package com.paperatus.swipe.core.components

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.physics.box2d.World
import com.paperatus.swipe.core.physics.PhysicsBodyData
import com.paperatus.swipe.core.physics.RectangleShape
import com.paperatus.swipe.core.scene.GameObject
import ktx.collections.GdxArray

/**
 * Provides a Physics system to a GameObject.
 */
open class PhysicsComponent(private val data: PhysicsBodyData) : Component {

    enum class Positioning {
        OBJECT_TO_BODY, BODY_TO_OBJECT
    }

    private var _physicsBody: Body? = null
    val physicsBody get() = _physicsBody!!
    var initialized = false
        private set

    var onInit: ((Body) -> Unit)? = null
    var positioning = Positioning.OBJECT_TO_BODY

    private val contactListeners = GdxArray<ContactListener>()

    override fun update(delta: Float, gameObject: GameObject) {
        val transform = gameObject.transform
        when (positioning) {
            Positioning.OBJECT_TO_BODY -> {
                gameObject.transform.position.set(
                        physicsBody.position.x + transform.worldSize.width *
                                (transform.anchor.x - 0.5f),
                        physicsBody.position.y + transform.worldSize.height *
                                (transform.anchor.y - 0.5f)
                )
            }
            Positioning.BODY_TO_OBJECT -> {
                physicsBody.setTransform(
                        transform.position.x + transform.worldSize.width *
                                (0.5f - transform.anchor.x),
                        transform.position.y + transform.worldSize.height *
                                (0.5f - transform.anchor.y),
                        0.0f)
            }
        }
    }

    /**
     * Called upon initialization to create Body instances.
     *
     * @param world Box2D game world.
     */
    fun init(world: World) {
        if (initialized) throw RuntimeException()

        val bodyDef = BodyDef()
        bodyDef.type = when (data.bodyType) {
            PhysicsBodyData.Type.STATIC -> BodyDef.BodyType.StaticBody
            PhysicsBodyData.Type.DYNAMIC -> BodyDef.BodyType.DynamicBody
            PhysicsBodyData.Type.KINEMATIC -> BodyDef.BodyType.KinematicBody
        }
        bodyDef.linearDamping = data.linearDampening
        bodyDef.angularDamping = data.angularDampening
        bodyDef.fixedRotation = data.fixedRotation

        val dataShape = data.shape

        val body = world.createBody(bodyDef)
        val shape: Shape
        val area: Float

        when (dataShape) {
            is com.paperatus.swipe.core.physics.CircleShape -> {
                val circle = CircleShape()
                circle.radius = dataShape.radius
                shape = circle
                area = MathUtils.PI * circle.radius * circle.radius
            }
            is RectangleShape -> {
                val square = PolygonShape()
                square.setAsBox(dataShape.width / 2.0f, dataShape.height / 2.0f)
                shape = square
                area = dataShape.width * dataShape.height
            }
            else -> throw RuntimeException()
        }

        val fixture = body.createFixture(shape, data.mass / area)
        fixture.isSensor = data.isSensor
        fixture.restitution = data.restitution
        shape.dispose()

        body.position.set(data.position)

        _physicsBody = body
        onInit?.invoke(physicsBody)
        initialized = true
    }

    fun destroy(world: World) {
        world.destroyBody(physicsBody)
    }

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

    override fun receive(what: ComponentMessage, payload: Any?) = Unit

    interface ContactListener {
        fun onContactBegin(other: GameObject)
        fun onContactEnd(other: GameObject)
    }


}

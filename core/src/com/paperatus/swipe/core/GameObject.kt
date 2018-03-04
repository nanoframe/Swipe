package com.paperatus.swipe.core

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ObjectMap
import kotlin.reflect.KClass

/**
 * Interface for objects that can be rendered onto the scene.
 *
 * @property spriteName the filename of the image of the object.
 * An empty string or a nonexistent file will throw an exception.
 * @property position position of the GameObject.
 * @property size size of the GameObject to render
 * @property rotation rotation of the GameObject, in degrees CCW.
 * @property bounds the boundaries of the GameObject.
 * Provides the GameObject's size.
 * @property anchor position where all transforms are relative to.
 * @property components Components that are attached to the GameObject
 */
open class GameObject() : Subject() {
    var spriteName: String = ""

    val position = Vector2()
    val size = Size()
    var rotation: Float = 0.0f
    val bounds = Rectangle()
        get() {
            field.set(position.x, position.y, size.width, size.height)
            return field
        }
    val anchor = Vector2()

    private val components = ObjectMap<KClass<out Component>, Component>()

    constructor(sprite: String) : this() {
        spriteName = sprite
    }

    /**
     * Updates the GameObject.
     *
     * @param [delta] the time since the last frame; capped at [SceneController.maxDeltaTime]
     */
    open fun update(delta: Float) {
    }

    /**
     * Attaches a Component onto the GameObject instance.
     *
     * The type of the Component should be specified to prevent weird problems.
     *
     * @param component the component to attach to the GameObject.
     */
    inline fun <reified T : Component> attachComponent(component: T) =
            attachComponent(component, T::class)

    /**
     * Attaches a Component onto the GameObject instance.
     *
     * This method serves as a helper method to [attachComponent]
     *
     * The type of the Component should be specified to prevent weird problems.
     *
     * @param component the component to attach to the GameObject.
     */
    fun attachComponent(component: Component, type: KClass<out Component>) {
        assert(!components.containsKey(type)) {
            "A component of type ${type.java.simpleName} is currently attached to this instance!"
        }
        components.put(type, component)
    }

    /**
     * Detatches a Component from the GameObject instance.
     */
    inline fun <reified T : Component> detachComponent() = detachComponent(T::class)

    /**
     * Detatches a Component from the GameObject instance.
     *
     * @param type class type of the Component. [detachComponent] should be
     * called instead for a cleaner code.
     */
    fun detachComponent(type: KClass<out Component>) = components.remove(type)

    inline fun <reified T : Component> getComponent() = getComponent(T::class)

    fun <T : Component> getComponent(type: KClass<T>): T? {
        return components[type, null] as T?
    }

    fun getComponents() = components

    /**
     * Sends a message to other components with an optional payload
     *
     * @param what message type
     * @param payload the data of the message; defaults to null
     */
    fun messageComponent(what: ComponentMessage, payload: Any? = null) {
        components.keys().forEach {
            components[it].receive(what, payload)
        }
    }
}

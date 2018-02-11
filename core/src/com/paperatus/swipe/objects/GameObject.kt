package com.paperatus.swipe.objects

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ObjectMap
import com.paperatus.swipe.handlers.Component
import com.paperatus.swipe.handlers.Subject
import kotlin.reflect.KClass

/**
 * Interface for objects that can be rendered onto the scene.
 *
 * @property spriteName the filename of the image of the object.
 * An empty string or a nonexistent file will throw an exception.
 */
abstract class GameObject : Subject() {
    var spriteName: String = ""

    val position = Vector2()
    var rotation: Float = 0.0f
    val bounds = Rectangle()

    private val components = ObjectMap<KClass<out Component>, Component>()

    abstract fun update(delta: Float)

    inline fun<reified T : Component> attachComponent(component: T) {
        attachComponent(component, T::class)
    }

    fun attachComponent(component: Component, type: KClass<out Component>) {
        components.put(type, component)
    }

    inline fun<reified T : Component> detachComponent() {
        detachComponent(T::class)
    }

    fun detachComponent(type: KClass<out Component>) {
        components.remove(type)
    }

}
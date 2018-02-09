package com.paperatus.swipe.objects

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.handlers.Subject

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

    abstract fun update(delta: Float)
}
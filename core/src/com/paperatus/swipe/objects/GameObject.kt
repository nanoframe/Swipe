package com.paperatus.swipe.objects

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

/**
 * Interface for objects that can be rendered onto the scene.
 *
 * @property spriteName the filename of the image of the object.
 * An empty string or a nonexistent file will throw an exception.
 */
interface GameObject {
    val spriteName: String

    val position: Vector2
    val velocity: Vector2
    val acceleration: Vector2
    val rotation: Float
    val bounds: Rectangle

    fun update(delta: Float)
}
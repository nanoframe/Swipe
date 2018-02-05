package com.paperatus.swipe.objects

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

interface GameObject {
    val spriteName: String

    val position: Vector2
    val velocity: Vector2
    val acceleration: Vector2
    val rotation: Float
    val bounds: Rectangle

    fun update(delta: Float)
}
package com.paperatus.swipe.core

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2

/**
 * Provides a repeated texture in the specified direction
 */
class TiledTexture(val texture: Texture) {

    enum class Direction {
        X, Y
    }

    init {
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    }

    val position = Vector2()

    var width = 0.0f
    var height = 0.0f

    var repeatCount = 0.0f
    var direction = Direction.X

    fun draw(batch: Batch) {
        val u: Float = when (direction) {
            Direction.X -> repeatCount
            Direction.Y -> (width / height) * repeatCount
        }

        val v: Float = when (direction) {
            Direction.X -> (height / width) * repeatCount
            Direction.Y -> repeatCount
        }

        batch.draw(
                texture,
                position.x, position.y,
                width, height,
                0.0f, 0.0f, u, v)
    }
}

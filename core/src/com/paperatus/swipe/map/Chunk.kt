package com.paperatus.swipe.map

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool
import ktx.collections.GdxArray

// TODO: Implement separate array class
class Chunk private constructor() : GdxArray<Vector2>(), Pool.Poolable {

    companion object : Pool<Chunk>() {
        override fun newObject(): Chunk {
            return Chunk()
        }
    }

    var body: Body? = null

    fun addPoint(x: Float, y: Float) {
        add(Vector2Pool.obtain().also {
            it.x = x
            it.y = y
        })
    }

    override fun reset() {
        forEach {
            Vector2Pool.free(it)
        }

        clear()
    }
}

private object Vector2Pool : Pool<Vector2>() {
    override fun newObject(): Vector2 {
        return Vector2()
    }
}

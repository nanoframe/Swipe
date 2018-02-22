package com.paperatus.swipe.map

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool
import ktx.collections.GdxArray

// TODO: Implement separate array class
class Chunk private constructor() : GdxArray<ChunkPoint>(), Pool.Poolable {

    companion object : Pool<Chunk>() {
        override fun newObject(): Chunk {
            return Chunk()
        }
    }

    var body: Body? = null

    fun addPoint(x: Float, y: Float) {
        add(ChunkPoint.obtain().also {
            it.x = x
            it.y = y
        })
    }

    override fun reset() {
        forEach {
            ChunkPoint.free(it)
        }

        clear()
    }
}

class ChunkPoint private constructor(var x: Float = 0.0f,
                                     var y: Float = 0.0f) : Pool.Poolable {

    companion object : Pool<ChunkPoint>() {
        override fun newObject(): ChunkPoint {
            return ChunkPoint()
        }
    }

    override fun reset() {
        x = 0.0f
        y = 0.0f
    }
}

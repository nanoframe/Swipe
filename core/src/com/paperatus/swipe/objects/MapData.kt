package com.paperatus.swipe.objects

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Pool
import ktx.collections.GdxArray

interface MapData {
    fun create()
    fun update(world: World, camera: Camera)
}

class Chunk private constructor() : GdxArray<ChunkPoint>(), Pool.Poolable {

    companion object : Pool<Chunk>() {
        override fun newObject(): Chunk {
            return Chunk()
        }
    }

    fun addPoint(x: Float, y: Float) {
        add(ChunkPoint.obtain().also {
            it.x = x
            it.y = y
        })
    }

    override fun reset() {

    }
}

class ChunkPoint private constructor (var x: Float = 0.0f,
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

package com.paperatus.swipe.map

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.physics.box2d.World
import ktx.collections.GdxArray

abstract class MapData {
    val leftChunks = GdxArray<Chunk>()
    val rightChunks = GdxArray<Chunk>()

    abstract var pathColor: Color

    private val renderer = MapRenderer()

    abstract fun create()
    abstract fun update(world: World, camera: Camera)

    open fun render(camera: Camera) {
        assert(leftChunks.size == rightChunks.size)
        if (leftChunks.size == 0) return

        renderer.projectionMatrix = camera.combined
        renderer.pathColor = pathColor

        for (i in 0 until leftChunks.size) {
            val leftChunk = leftChunks[i]
            val rightChunk = rightChunks[i]

            renderer.drawPath(leftChunk, rightChunk)
        }

        renderer.flush()
    }
}

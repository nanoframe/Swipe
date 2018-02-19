package com.paperatus.swipe.objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Pool
import ktx.collections.GdxArray

abstract class MapData {
    val leftChunks = GdxArray<Chunk>()
    val rightChunks = GdxArray<Chunk>()

    abstract var pathColor: Color

    private val renderer = MapRenderer()

    abstract fun create()
    abstract fun update(world: World, camera: Camera)

    fun render(camera: Camera) {
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

    private class MapRenderer (maxVertices: Int = 24) {

        var pathColor: Color = Color.BLACK
        var projectionMatrix: Matrix4? = null

        private val shader = ShaderProgram(
                Gdx.files.internal("shaders/map_shader.vert"),
                Gdx.files.internal("shaders/map_shader.frag")
        )
        private val mesh = Mesh(false, maxVertices, 0,
                        VertexAttribute(
                                VertexAttributes.Usage.Position,
                                2,
                                "a_position"))

        // TODO: Utilize indices instead of vertices
        private val verts = FloatArray(maxVertices)
        private var size = 0

        init {
            assert(maxVertices % 3 == 0)
            assert(shader.isCompiled) {
                shader.log
            }
        }

        fun drawPath(leftChunk: Chunk, rightChunk: Chunk) {
            for (i in 0 until leftChunk.size - 1) {
                if (size + 6 > verts.size) {
                    flush()
                }
                var p1 = leftChunk[i]
                var p2 = rightChunk[i]
                var p3 = leftChunk[i+1]

                verts[size++] = p1.x
                verts[size++] = p1.y
                verts[size++] = p2.x
                verts[size++] = p2.y
                verts[size++] = p3.x
                verts[size++] = p3.y

                p1 = rightChunk[i]
                p2 = leftChunk[i+1]
                p3 = rightChunk[i+1]

                verts[size++] = p1.x
                verts[size++] = p1.y
                verts[size++] = p2.x
                verts[size++] = p2.y
                verts[size++] = p3.x
                verts[size++] = p3.y
            }
        }

        fun flush() {
            mesh.setVertices(verts)
            val vertexCount = size / 2

            shader.begin()
            shader.setUniformMatrix("u_projTrans", projectionMatrix)
            shader.setUniformf(
                    "u_pathColor",
                    pathColor.r,
                    pathColor.g,
                    pathColor.b)
            mesh.render(shader, GL20.GL_TRIANGLES, 0, vertexCount)
            shader.end()

            size = 0
        }
    }
}

// TODO: Implement separate array class
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
        forEach {
            ChunkPoint.free(it)
        }

        clear()
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

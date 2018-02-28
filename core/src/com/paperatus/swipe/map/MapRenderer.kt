package com.paperatus.swipe.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.data.Solver
import kotlin.math.sqrt

class MapRenderer(var pathColor: Color,
                  var edgeTexture: Texture) {

    private val edgeRenderer = EdgeRenderer(this, 120)
    private val pathRenderer = PathRenderer(this, 120)

    var projectionMatrix: Matrix4? = null

    fun drawEdge(chunk: Chunk) {
        edgeRenderer.projectionMatrix = projectionMatrix
        edgeRenderer.draw(chunk)
    }

    fun flushEdge() = edgeRenderer.flush()

    fun drawPath(leftChunk: Chunk, rightChunk: Chunk) {
        pathRenderer.projectionMatrix = projectionMatrix
        pathRenderer.draw(leftChunk, rightChunk)
    }

    fun flushPath() = pathRenderer.flush()
}

private class EdgeRenderer(val mapRenderer: MapRenderer,
                           maxVertices: Int = 60) {

    var projectionMatrix: Matrix4? = null

    private val shader = ShaderProgram(
            Gdx.files.internal("shaders/edge_shader.vert"),
            Gdx.files.internal("shaders/edge_shader.frag")
    )
    private val mesh = Mesh(false, maxVertices, 0,
            VertexAttribute(
                    VertexAttributes.Usage.Position,
                    2,
                    "a_position"),
            VertexAttribute(
                    VertexAttributes.Usage.TextureCoordinates,
                    2,
                    "a_texCoords"
            ))

    // TODO: Utilize indices instead of vertices
    private val verts = FloatArray(maxVertices)
    private var size = 0

    init {
        assert(maxVertices % 3 == 0)
        assert(shader.isCompiled) {
            shader.log
        }

        mapRenderer.edgeTexture.setWrap(
                Texture.TextureWrap.Repeat,
                Texture.TextureWrap.Repeat)
    }

    private val temp = Vector2()
    fun draw(chunk: Chunk) {
        for (i in 0 until chunk.size - 1) {
            if (size + 24 > verts.size) {
                flush()
            }

            val p1 = chunk[i]
            val p2 = chunk[i + 1]
            Solver.getPerpendicularDelta(p1, p2, 3.0f, temp)

            val x1 = p1.x - temp.x
            val y1 = p1.y - temp.y
            val x2 = p2.x - temp.x
            val y2 = p2.y - temp.y
            val dx1 = p1.x + temp.x
            val dy1 = p1.y + temp.y
            val dx2 = p2.x + temp.x
            val dy2 = p2.y + temp.y
            val u = 0.0f
            val v = sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) / 3.0f
            val u2 = 1.0f
            val v2 = 0.0f

            verts[size++] = x1
            verts[size++] = y1
            verts[size++] = u
            verts[size++] = v

            verts[size++] = x2
            verts[size++] = y2
            verts[size++] = u
            verts[size++] = v2

            verts[size++] = dx1
            verts[size++] = dy1
            verts[size++] = u2
            verts[size++] = v

            verts[size++] = dx1
            verts[size++] = dy1
            verts[size++] = u2
            verts[size++] = v

            verts[size++] = x2
            verts[size++] = y2
            verts[size++] = u
            verts[size++] = v2

            verts[size++] = dx2
            verts[size++] = dy2
            verts[size++] = u2
            verts[size++] = v2
        }
    }

    fun flush() {
        mesh.setVertices(verts)
        val vertexCount = size / 2

        mapRenderer.edgeTexture.bind()
        shader.begin()
        shader.setUniformMatrix("u_projTrans", projectionMatrix)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        mesh.render(shader, GL20.GL_TRIANGLES, 0, vertexCount)
        shader.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)

        size = 0
    }
}

private class PathRenderer(val mapRenderer: MapRenderer,
                           maxVertices: Int = 24) {

    var projectionMatrix: Matrix4? = null

    private val shader = ShaderProgram(
            Gdx.files.internal("shaders/color_shader.vert"),
            Gdx.files.internal("shaders/color_shader.frag")
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

    fun flush() {
        mesh.setVertices(verts)
        val vertexCount = size / 2

        shader.begin()
        shader.setUniformMatrix("u_projTrans", projectionMatrix)
        shader.setUniformf(
                "u_pathColor",
                mapRenderer.pathColor.r,
                mapRenderer.pathColor.g,
                mapRenderer.pathColor.b)
        mesh.render(shader, GL20.GL_TRIANGLES, 0, vertexCount)
        shader.end()

        size = 0
    }

    fun draw(leftChunk: Chunk, rightChunk: Chunk) {
        for (i in 0 until leftChunk.size - 1) {
            if (size + 6 > verts.size) {
                flush()
            }
            var p1 = leftChunk[i]
            var p2 = rightChunk[i]
            var p3 = leftChunk[i + 1]

            verts[size++] = p1.x
            verts[size++] = p1.y
            verts[size++] = p2.x
            verts[size++] = p2.y
            verts[size++] = p3.x
            verts[size++] = p3.y

            p1 = rightChunk[i]
            p2 = leftChunk[i + 1]
            p3 = rightChunk[i + 1]

            verts[size++] = p1.x
            verts[size++] = p1.y
            verts[size++] = p2.x
            verts[size++] = p2.y
            verts[size++] = p3.x
            verts[size++] = p3.y
        }
    }
}

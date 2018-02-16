package com.paperatus.swipe.objects

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.physics.box2d.World
import ktx.collections.GdxArray

private const val GENERATE_GAP = 30.0f

class ProceduralMapData : MapData {
    private val generatedY: Float = 0.0f

    val points = GdxArray<Point>()

    override fun create() {
    }

    override fun update(world: World, camera: Camera) {
        val cameraTop = camera.position.y + camera.viewportHeight / 2.0f

        // Create more paths if the top of the screen is near the
        // end of the path
        if (generatedY - cameraTop < GENERATE_GAP) {
            val point = PointFactory.obtain()
        }

    }
}

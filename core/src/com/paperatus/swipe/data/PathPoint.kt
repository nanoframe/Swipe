package com.paperatus.swipe.data

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

class PathPoint private constructor(): Vector2() {
    companion object : Pool<PathPoint>() {
        override fun newObject(): PathPoint = PathPoint()
    }

    var width = 0.0f
}

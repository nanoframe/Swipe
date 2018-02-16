package com.paperatus.swipe.objects

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.physics.box2d.World

interface MapData {
    fun create()
    fun update(world: World, camera: Camera)
}

package com.paperatus.swipe.objects

import Notification
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.components.RenderComponent
import com.paperatus.swipe.core.patterns.Observer
import com.paperatus.swipe.core.scene.GameObject

class PathObjectGenerator : GameObject(), Observer {
    override fun receive(what: Int, payload: Any?) {
        val pathObject: GameObject = when (what) {
            Notification.BLOCKADE_SPAWN -> {
                val d = Destructible()
                d.attachComponent<RenderComponent>(
                        RenderComponent(sprite = "blockade.png"))
                d
            }

            Notification.DESTRUCTIBLE_SPAWN -> {
                val r = RoadBlock()
                r.attachComponent<RenderComponent>(
                        RenderComponent(sprite = "blockade.png"))
                r
            }
            else -> return
        }

        addChild(pathObject.apply {
            transform.position.set(payload as Vector2)
        })
    }
}

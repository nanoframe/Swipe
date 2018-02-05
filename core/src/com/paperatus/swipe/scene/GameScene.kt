package com.paperatus.swipe.scene

import com.paperatus.swipe.handlers.PlayerInput
import com.paperatus.swipe.objects.Player

class GameScene : Scene() {

    var player: Player = Player(PlayerInput())

    init {
        gameObjects.operate {
            add(player)
        }
    }

    override fun update(delta: Float) {
        player.update(delta)
    }

    override fun reset() {
    }

    override fun dispose() {
    }
}
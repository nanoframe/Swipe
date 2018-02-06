package com.paperatus.swipe

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.assets.AssetManager
import com.paperatus.swipe.scene.GameScene
import com.paperatus.swipe.scene.SceneController

class Game : ApplicationAdapter() {

    val sceneController = SceneController()
    val assets = AssetManager()

    override fun create() {
        sceneController.init()

        sceneController.addScene(GameScene(this))
        sceneController.setScene<GameScene>()
    }

    override fun render() {
        sceneController.step()
    }

    override fun dispose() {
        sceneController.dispose()
    }
}

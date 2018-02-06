package com.paperatus.swipe

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.assets.AssetManager
import com.paperatus.swipe.scene.GameScene
import com.paperatus.swipe.scene.SceneController
import com.paperatus.swipe.scene.SplashScene

const val VIEWPORT_HEIGHT: Float = 15.0f

class Game : ApplicationAdapter() {

    val sceneController = SceneController()
    val assets = AssetManager()

    override fun create() {
        // Initialize the Scene
        sceneController.init()
        sceneController.addScene(GameScene(this))

        sceneController.showSceneOnce(SplashScene(this))
    }

    override fun render() {
        sceneController.step()
    }

    override fun dispose() {
        sceneController.dispose()
    }
}

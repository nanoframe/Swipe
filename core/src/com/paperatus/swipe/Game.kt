package com.paperatus.swipe

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.paperatus.swipe.core.SceneController
import com.paperatus.swipe.scenes.GameScene
import com.paperatus.swipe.scenes.SplashScene

class Game : ApplicationListener {

    val sceneController = SceneController()
    val assets = AssetManager()

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG

        // Initialize the Scene
        sceneController.init()
        sceneController.addScene(GameScene(this))

        sceneController.showSceneOnce(SplashScene(this))
    }

    override fun render() {
        sceneController.step()
    }

    override fun resume() = Unit

    override fun pause() = Unit

    override fun resize(width: Int, height: Int) = sceneController.resize(width, height)

    override fun dispose() {
        sceneController.dispose()
    }
}

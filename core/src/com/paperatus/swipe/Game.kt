package com.paperatus.swipe

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.utils.ObjectMap
import com.paperatus.swipe.scene.GameScene
import com.paperatus.swipe.scene.SceneController

class Game : ApplicationAdapter() {

    val sceneController = SceneController()
    val assets = Assets()

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

class Assets {
    private val assetMap = ObjectMap<String, Any>()

    private val manager = AssetManager()

    fun <T : Any> get(asset: String) : T? {
        @Suppress("UNCHECKED_CAST")
        return assetMap[asset] as T?
    }

    fun isLoaded(asset: String) : Boolean = assetMap[asset] != null

}

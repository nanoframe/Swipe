package com.paperatus.swipe

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.utils.ObjectMap
import com.paperatus.swipe.scene.GameScene
import com.paperatus.swipe.scene.SceneController

class Game : ApplicationAdapter() {

    private lateinit var sceneController: SceneController
    private lateinit var assets: Assets

    override fun create() {
        assets = Assets()

        sceneController = SceneController(assets)
        sceneController.addScene(GameScene())
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

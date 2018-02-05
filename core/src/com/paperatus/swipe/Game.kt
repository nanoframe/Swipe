package com.paperatus.swipe

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.utils.ObjectMap

class Game : ApplicationAdapter() {

    override fun create() {
    }

    override fun render() {
    }

    override fun dispose() {
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

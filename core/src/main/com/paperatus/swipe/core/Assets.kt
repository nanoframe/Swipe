package com.paperatus.swipe.core

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Disposable
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import ktx.collections.set

// TODO: Implement async loading
class Assets {
    private val files = GdxMap<String, Any>()
    private val disposables = GdxArray<Disposable>()

    operator fun <T : Any> get(filename: String): T {
        if (!isLoaded(filename)) throw AssetNotLoadedException("Asset $filename isn't loaded!")
        val obj = files[filename]
        return obj as T
    }

    fun loadTexture(filename: String) {
        val texture = Texture(filename)
        disposables.add(texture)
        files.put(filename, texture)
    }

    fun loadTextureAtlas(filename: String) {
        val atlas = TextureAtlas(filename)
        val regions = atlas.regions
        val directory = filename.substringBefore('/')
        regions.forEach {
            files["$directory/${it.name}"] = it
        }
        disposables.add(atlas)
    }

    fun isLoaded(filename: String) = files.containsKey(filename)

    fun dispose() {
        disposables.forEach {
            it.dispose()
        }
    }
}

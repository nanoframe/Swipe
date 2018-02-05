package com.paperatus.swipe.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ObjectMap
import com.paperatus.swipe.Assets
import kotlin.math.max
import kotlin.reflect.KClass


/**
 * Created by vianney on 2018-01-29.
 */

class SceneController(var assets: Assets) : Disposable {

    var paused = false

    // Store and reuse scenes instead of creating a new one each time
    private var scenes = ObjectMap<KClass<out Scene>, Scene>()
    private var activeScene: Scene? = null

    var maxDeltaTime = 1.0f / 30.0f
    private var batch: SpriteBatch = SpriteBatch()

    fun addScene(scene: Scene, type: KClass<out Scene>) {
        scene.sceneController = this
        scenes.put(type, scene)
    }

    inline fun <reified T : Scene> addScene(scene: T) {
        addScene(scene, T::class)
    }

    fun setScene(type: KClass<out Scene>) {
        activeScene = scenes[type]
        activeScene!!.reset()
    }

    inline fun <reified T : Scene> setScene() {
        setScene(T::class)
    }

    /**
     * Performs an operate + render step on the SceneController
     */
    internal fun step() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (!paused) activeScene?.update(max(Gdx.graphics.deltaTime, maxDeltaTime))

        activeScene?.let {
            batch.begin()
            it.render(batch)
            batch.end()
        }
    }

    override fun dispose() {
        batch.dispose()
        activeScene?.dispose()
    }
}

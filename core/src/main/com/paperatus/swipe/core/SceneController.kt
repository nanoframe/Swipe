package com.paperatus.swipe.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ObjectMap
import kotlin.math.min
import kotlin.reflect.KClass

/**
 * Provides a way to manage, order, and render Scenes.
 *
 * @property paused determines if [Scene.render] should be called.
 */

class SceneController : Disposable {

    var paused = false

    // Store and reuse scenes instead of creating a new one each time
    private val scenes = ObjectMap<KClass<out Scene>, Scene>()
    private var activeScene: Scene? = null

    var maxDeltaTime = 1.0f / 30.0f
    private lateinit var batch: SpriteBatch

    private var isInitialized = false
    private var isSceneCreated = false

    /**
     * Initializes the SceneController
     */
    fun init() = if (!isInitialized) {
        isInitialized = true
        batch = SpriteBatch()
    } else {
        throw RuntimeException("SceneController is already initialized!")
    }

    /**
     * Adds a Scene to the SceneController.
     *
     * @param scene the Scene to add.
     * @param type the class of the Scene to refer to when setting the Scene.
     */
    fun addScene(scene: Scene, type: KClass<out Scene>) {
        scenes.put(type, scene)
        scene.resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    /**
     * Adds a Scene to the SceneController.
     *
     * @param scene the Scene to add.
     */
    inline fun <reified T : Scene> addScene(scene: T) {
        addScene(scene, T::class)
    }

    /**
     * Sets the active Scene to the Scene of class [type].
     *
     * @param type the class of the Scene to display.
     */
    fun setScene(type: KClass<out Scene>) {
        activeScene = scenes[type]
        activeScene!!.reset()
    }

    /**
     * Displays the Scene without adding the Scene to the SceneController.
     *
     * @param scene the Scene to display.
     */
    fun showSceneOnce(scene: Scene) {
        activeScene = scene
        scene.create()
        scene.reset()
        scene.resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    /**
     * Sets the active Scene to the Scene [T]
     */
    inline fun <reified T : Scene> setScene() {
        setScene(T::class)
    }

    fun createScenes() = if (!isSceneCreated) {
        scenes.forEach { it.value.create() }
    } else throw RuntimeException("createScenes() has already been called!")

    /**
     * Performs a [Scene.update]* and [Scene.render] step on the Scene.
     *
     * The order method will not be called if [paused] is set to true.
     */
    internal fun step() {
        if (!isInitialized)
            throw RuntimeException("SceneController not initialized! Did you forget to call init()?")

        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (!paused) activeScene?.let {
            val delta = min(Gdx.graphics.deltaTime, maxDeltaTime)
            it.preUpdate(delta)
            it.update(delta)
            it.postUpdate(delta)
        }

        activeScene?.let {
            it.preRender(batch)
            batch.begin()
            it.render(batch)
            batch.end()
            it.postRender(batch)
        }
    }

    /**
     * Called when the game resolution has changed.
     *
     * @param width new screen width.
     * @param height new screen height.
     */
    fun resize(width: Int, height: Int) {
        activeScene?.resize(width, height)
    }

    override fun dispose() {
        batch.dispose()
        scenes.values().forEach { it.dispose() }
    }
}

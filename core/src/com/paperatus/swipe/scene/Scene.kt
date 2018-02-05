package com.paperatus.swipe.scene

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable
import com.paperatus.swipe.objects.GameObject
import ktx.collections.GdxArray

abstract class Scene : Disposable {
    lateinit var sceneController: SceneController
    var gameObjects = GdxArray<GameObject>()

    abstract fun update(delta: Float)
    open fun render(batch: SpriteBatch) {
        gameObjects.forEach {
            assert(it.spriteName != "")
            assert(sceneController.assets.isLoaded(it.spriteName))

            val image: Any = sceneController.assets.get(it.spriteName)!!

            when (image) {
                is Texture -> batch.draw(image,
                        it.position.x, it.position.y,
                        0.5f, 0.5f,
                        it.bounds.width, it.bounds.height,
                        1.0f, 1.0f,
                        it.rotation,
                        0, 0, image.width, image.height,
                        false, false
                )
                is TextureRegion -> batch.draw(image,
                        it.position.x, it.position.y,
                        0.5f, 0.5f,
                        it.bounds.width, it.bounds.height,
                        1.0f, 1.0f,
                        it.rotation
                )
                else -> ktx.log.error(RuntimeException()) {
                    "Asset '$it.spriteName' is of type ${image::class}!"
                }
            }
        }
    }

    abstract fun reset()

    inline fun<T : Any> GdxArray<T>.operate(action: GdxArray<T>.() -> Unit) = action()
}

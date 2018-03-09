import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.GameObject
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

class ActionTest : Spek({
    given("a GameObject") {
        val gameObject = GameObject()

        on("movement") {
            val movement = Actions.moveTo(5.0f, 2.0f, 3.0f)
            act(gameObject, movement)

            it("should move to (5, 2)") {
                assert(gameObject.position == Vector2(5.0f, 2.0f))
            }
        }
    }

})

private fun act(gameObject: GameObject, action: Action) {
    gameObject.runAction(action)
    while (gameObject.isActionActive()) {
        gameObject.update(1.0f/60.0f)
    }
}

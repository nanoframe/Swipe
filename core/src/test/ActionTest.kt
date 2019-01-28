import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.scene.GameObject
import com.paperatus.swipe.core.InvalidActionException
import com.paperatus.swipe.core.Scale
import io.kotlintest.TestCaseContext
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.BehaviorSpec
import org.junit.Assert.assertEquals

class ActionTest : BehaviorSpec() {
    private val gameObject: GameObject = GameObject()

    init {
        given("a base TimeAction class") {
            `when`("the duration is negative") {
                then ("it should throw an error") {
                    shouldThrow<InvalidActionException> {
                        Actions.moveTo(0.0f, 0.0f, -1.0f)
                    }
                }
            }
        }

        given("a moveTo action") {
            `when`("applied") {
                val movement = Actions.moveTo(5.0f, 2.0f, 1.0f)

                then("it should move to (5, 2)") {
                    act(gameObject, movement)
                    assertEquals(Vector2(5.0f, 2.0f), gameObject.transform.position)
                }
            }

            `when`("the duration is 0") {
                val movement = Actions.moveTo(5.0f, 1.0f, 0.0f)

                then("it should be at (5, 2)") {
                    act(gameObject, movement)
                    assertEquals(Vector2(5.0f, 1.0f), gameObject.transform.position)
                }
            }
        }

        given("a Sequence") {
            `when`("a Sequence of moveTo and scaleTo is applied") {
                val sequence = Actions.sequence {
                    moveTo(7.0f, 5.0f, 0.5f)
                    scaleTo(5.0f, 3.0f, 0.5f)
                }

                act(gameObject, sequence)

                then("the position will be (7, 5)") {
                    assertEquals(Vector2(7.0f, 5.0f), gameObject.transform.position)
                }
                then("the scale will be (5, 3)") {
                    assertEquals(Vector2(5.0f, 3.0f), gameObject.transform.scale)
                }
            }
        }

        given("a Spawn") {
            `when`("a Spawn of sizeTo and moveTo is applied") {
                val spawn = Actions.spawn {
                    moveTo(12.0f, 1.0f, 1.0f)
                    scaleTo(2.0f, 5.0f, 1.0f)
                }
                act(gameObject, spawn)

                then("the position should be (12, 1)") {
                    assertEquals(Vector2(12.0f, 1.0f), gameObject.transform.position)
                }

                then("the scale should be (2, 5)") {
                    assertEquals(Scale(2.0f, 5.0f), gameObject.transform.scale)
                }
            }
        }
    }

    override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
        gameObject.transform.position.set(POSITION_X, POSITION_Y)
        gameObject.transform.worldSize.set(WIDTH, HEIGHT)
        gameObject.transform.scale.set(1.0f, 1.0f)

        test()
    }
}

private fun act(gameObject: GameObject, action: Action) {
    gameObject.runAction(action)
    while (gameObject.isActionActive()) {
        gameObject.update(1.0f/60.0f)
    }
}

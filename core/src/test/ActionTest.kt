import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.InvalidActionException
import com.paperatus.swipe.core.Size
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
                    assertEquals(gameObject.position, Vector2(5.0f, 2.0f))
                }
            }

            `when`("the duration is 0") {
                val movement = Actions.moveTo(5.0f, 1.0f, 0.0f)

                then("it should be at (5, 2)") {
                    act(gameObject, movement)
                    assertEquals(gameObject.position, Vector2(5.0f, 1.0f))
                }
            }
        }

        given("a Sequence") {
            `when`("a Sequence of sizeTo and scaleTo is applied") {
                val sequence = Actions.sequence {
                    sizeTo(7.0f, 5.0f, 0.5f)
                    scaleTo(3.0f, 0.5f)
                }

                then("the size will be (21, 15)") {
                    act(gameObject, sequence)
                    assertEquals(gameObject.size, Size(21.0f, 15.0f))
                }
            }
        }

        given("a Spawn") {
            `when`("a Spawn of sizeTo and moveTo is applied") {
                val spawn = Actions.spawn {
                    sizeTo(25.0f, 12.0f, 1.0f)
                    moveTo(12.0f, 1.0f, 1.0f)
                }
                act(gameObject, spawn)

                then("the position should be (12, 1)") {
                    assertEquals(gameObject.position, Vector2(12.0f, 1.0f))
                }

                then("the size should be (25, 12)") {
                    assertEquals(gameObject.size, Size(25.0f, 12.0f))
                }
            }
        }
    }

    override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
        gameObject.position.set(POSITION_X, POSITION_Y)
        gameObject.size.set(WIDTH, HEIGHT)

        test()
    }
}

private fun act(gameObject: GameObject, action: Action) {
    gameObject.runAction(action)
    while (gameObject.isActionActive()) {
        gameObject.update(1.0f/60.0f)
    }
}

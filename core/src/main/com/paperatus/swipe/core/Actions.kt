import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.InvalidActionException
import com.paperatus.swipe.core.RenderComponent
import ktx.collections.GdxArray

object Actions {
    fun sequence(init: Sequence.() -> Unit): ActionGroup {
        val sequence = Sequence()
        sequence.init()
        return sequence
    }

    fun spawn(init: Spawn.() -> Unit): ActionGroup {
        val spawn = Spawn()
        spawn.init()
        return spawn
    }

    fun moveTo(x: Float, y: Float, duration: Float) = MoveTo(x, y, duration)

    fun moveTo(position: Vector2, duration: Float) =
            moveTo(position.x, position.y, duration)

    fun scaleTo(x: Float, y: Float, duration: Float) = ScaleTo(x, y, duration)

    fun scaleTo(scl: Float, duration: Float) = scaleTo(scl, scl, duration)

    fun sizeTo(width: Float, height: Float, duration: Float) = SizeTo(width, height, duration)

    fun fade(duration: Float) = FadeAction(duration)
}

abstract class Action {
    private var rawGameObject: GameObject? = null
    val gameObject get() = rawGameObject!!

    abstract fun start()
    abstract fun update(delta: Float)
    abstract fun end()
    abstract fun isFinished(): Boolean

    fun setGameObject(gameObject: GameObject?) {
        rawGameObject = gameObject
    }
}

abstract class ActionGroup : Action() {
    protected val actionList = GdxArray<Action>()

    override fun start() {
        actionList.forEach {
            it.setGameObject(gameObject)
        }
    }

    // Action groups

    fun sequence(actions: Sequence.() -> Unit) = add(Sequence().also{ it.actions() })

    fun spawn(actions: Spawn.() -> Unit) = add(Spawn().also{ it.actions() })

    // State actions

    fun moveTo(x: Float, y: Float, duration: Float) =
        add(Actions.moveTo(x, y, duration))

    fun moveTo(position: Vector2, duration: Float) =
            moveTo(position.x, position.y, duration)

    fun scaleTo(x: Float, y: Float, duration: Float) =
            add(Actions.scaleTo(x, y, duration))

    fun scaleTo(scl: Float, duration: Float) = scaleTo(scl, scl, duration)

    fun sizeTo(width: Float, height: Float, duration: Float) =
            add(Actions.sizeTo(width, height, duration))

    fun fade(duration: Float) = add(Actions.fade(duration))

    // Others

    fun execute(func: GameObject.() -> Unit) = add(ExecuteAction(func))

    fun delay(duration: Float) = add(DelayAction(duration))

    private fun add(action: Action) = actionList.add(action)
}

abstract class TimeAction(internal val duration: Float) : Action() {
    internal var currentDuration = 0.0f
        private set

    init {
        if (duration < 0.0f) throw InvalidActionException("Duration $duration is < 0!")
    }

    override fun start() {
    }

    override fun update(delta: Float) {
        currentDuration += delta
        step((currentDuration / duration).coerceAtMost(1.0f))
    }

    override fun end() {
    }

    override fun isFinished() = (currentDuration / duration) >= 1.0f

    abstract fun step(alpha: Float)
}

// Action groups

class Sequence internal constructor() : ActionGroup() {

    private var index = 0
    private var activeAction: Action? = null

    override fun update(delta: Float) {
        // Calculate the amount of time the action has went over
        // if the action has been completed
        val timeOffset = activeAction?.let {
            if (!it.isFinished()) return@let 0.0f

            it.end()
            activeAction = null
            index++

            return@let if (it is TimeAction) it.currentDuration - it.duration
            else 0.0f
        } ?: 0.0f

        if (isFinished()) return

        activeAction = activeAction ?: actionList[index].apply {
            start()
        }

        val action = activeAction!!
        action.update(delta + timeOffset)
    }

    override fun end() = Unit

    override fun isFinished() = index >= actionList.size
}

class Spawn internal constructor() : ActionGroup() {
    private var finished: Boolean = false

    override fun start() {
        super.start()
        actionList.forEach {
            it.start()
        }
    }

    override fun update(delta: Float) {
        // Calculate the amount of time the action has went over
        // if the action has been completed

        if (isFinished()) return

        finished = true
        actionList.forEach {
            it.update(delta)
            finished = (it.isFinished() and finished)
        }
    }

    override fun end() = Unit

    override fun isFinished() = finished
}

// Actions that can modify the state of the GameObject

class MoveTo internal constructor(val x: Float, val y: Float, duration: Float) : TimeAction(duration) {
    private var startX: Float = 0.0f
    private var startY: Float = 0.0f

    override fun start() {
        startX = gameObject.position.x
        startY = gameObject.position.y
    }

    override fun step(alpha: Float) {
        val newX = MathUtils.lerp(startX, x, alpha)
        val newY = MathUtils.lerp(startY, y, alpha)
        gameObject.position.set(newX, newY)
    }

    override fun end() {
        gameObject.position.set(x, y)
    }
}

class ScaleTo internal constructor(val x: Float,
                                   val y: Float,
                                   duration: Float) : TimeAction(duration) {
    private var startX = 0.0f
    private var startY = 0.0f

    override fun start() {
        startX = gameObject.size.width
        startY = gameObject.size.height
    }

    override fun step(alpha: Float) {
        val newX = MathUtils.lerp(startX, startX * x, alpha)
        val newY = MathUtils.lerp(startY, startY * y, alpha)
        gameObject.size.set(newX, newY)
    }

    override fun end() {
        gameObject.size.set(startX * x, startY * y)
    }
}

class SizeTo internal constructor(val width: Float,
                                  val height: Float,
                                  duration: Float) : TimeAction(duration) {
    private var startWidth = 0.0f
    private var startHeight = 0.0f

    override fun start() {
        startWidth = gameObject.size.width
        startHeight = gameObject.size.height
    }

    override fun step(alpha: Float) {
        val newWidth = MathUtils.lerp(startWidth, width, alpha)
        val newHeight = MathUtils.lerp(startHeight, height, alpha)
        gameObject.size.set(newWidth, newHeight)
    }

    override fun end() {
        gameObject.size.set(width, height)
    }
}

class FadeAction internal constructor(duration: Float) : TimeAction(duration) {
    private var startAlpha: Float = 0.0f
    private var renderComponent: RenderComponent? = null

    override fun start() {
        renderComponent = gameObject.getComponent<RenderComponent>() ?: throw InvalidActionException("The given component does not have a RenderComponent!")
        startAlpha = renderComponent!!.alpha
    }

    override fun step(alpha: Float) {
        renderComponent!!.alpha = MathUtils.lerp(startAlpha, 0.0f, alpha)
    }

    override fun end() {
        renderComponent!!.alpha = 0.0f
        renderComponent = null
    }
}

// Others

class ExecuteAction internal constructor(private val func: GameObject.() -> Unit) : Action() {
    override fun start() {
        gameObject.func()
    }

    override fun update(delta: Float) = Unit

    override fun end() = Unit

    override fun isFinished() = true
}

class DelayAction internal constructor(duration: Float) : TimeAction(duration) {
    override fun step(alpha: Float) = Unit
}

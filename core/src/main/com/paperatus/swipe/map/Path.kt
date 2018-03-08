import com.badlogic.gdx.math.MathUtils
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import ktx.collections.set

class Path {

    private val types: Array<Type>
    private val availableTypes = GdxArray<Type>(5)

    private val pathMap = GdxMap<Type, Int>()

    private var pathType = PathType(Type.Uninitialized, Direction.None)

    init {
        types = Type.values()
    }

    enum class Type {
        Uninitialized,
        SoftCurve,
        HardCurve,
        //BackLeft, BackRight,
        Up;
    }

    enum class Direction {
        Left, Right, None;
    }

    fun setTypeCount(t: Type, count: Int) {
        pathMap[t] = count
    }

    fun random(): PathType {
        if (availableTypes.size <= 0) {
            resetTypes()
        }

        val type = availableTypes.removeIndex(
                MathUtils.random(0, availableTypes.size - 1))
        pathType.type = type
        pathType.direction =
                if (MathUtils.randomBoolean()) { Direction.Left } else { Direction.Right }

        return pathType
    }

    private fun resetTypes() {
        // Skip the uninitialized type
        for (i in 1..types.lastIndex) {
            val type = types[i]
            val count = pathMap[type, 1]

            for (j in 1..count) availableTypes.add(type)
        }
    }

    data class PathType(
        var type: Type = Type.Uninitialized,
        var direction: Direction = Direction.None
    )
}

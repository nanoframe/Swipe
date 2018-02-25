import com.badlogic.gdx.math.MathUtils
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import ktx.collections.set

object Path {

    // TODO: Add probability

    private val types: Array<Type>
    private val availableTypes = GdxArray<Type>(5)

    private val pathMap = GdxMap<Type, Int>()

    init {
        types = Type.values()
    }

    enum class Type {
        CurveLeft, CurveRight,
        //BackLeft, BackRight,
        CurveUp;
    }

    fun setTypeCount(t: Type, count: Int) {
        pathMap[t] = count
    }

    fun random(): Type {
        if (availableTypes.size <= 0) {
            resetTypes()
        }

        return availableTypes.removeIndex(
                            MathUtils.random(0,
                                             availableTypes.size - 1))
    }

    private fun resetTypes() {
        for (i in 0..types.lastIndex) {
            val type = types[i]
            val count = pathMap[type, 1]

            for (j in 1..count) availableTypes.add(type)
        }
    }
}

import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.NodeRemover
import com.paperatus.swipe.core.NodeTraversal
import io.kotlintest.TestCaseContext
import io.kotlintest.specs.FunSpec
import org.junit.Assert

class NodeTraversalTest : FunSpec() {
    private val traversal = NodeTraversal()
    private val remover = NodeRemover()

    init {
        test("Removing node root-0-0 changes the size to 2") {
            val root = getRoot()
            val node = root.children[0].children[0]
            node.requestRemove()

            traversal.traverse(remover, root)

            Assert.assertEquals(2, root.children[0].children.size)
        }

        test("Removing node root-0 changes the size to 1") {
            val root = getRoot()
            val node = root.children[0]
            node.requestRemove()

            traversal.traverse(remover, root)

            Assert.assertEquals(1, root.children.size)
        }
    }

    fun getRoot() : GameObject {
        val root = GameObject()

        val n0 = GameObject()

        val n0n0 = GameObject()
        val n0n0n0 = GameObject()
        val n0n0n1 = GameObject()

        n0n0.addChild(n0n0n0)
        n0n0.addChild(n0n0n1)

        val n0n1 = GameObject()
        val n0n2 = GameObject()

        n0.addChild(n0n0)
        n0.addChild(n0n1)
        n0.addChild(n0n2)

        root.addChild(n0)

        val n1 = GameObject()
        root.addChild(n1)

        return root
    }

    override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
        println("-----${context.testCase.name}-----")
        test()
        println()
    }
}

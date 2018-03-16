import com.paperatus.swipe.core.LinkedList
import io.kotlintest.TestCaseContext
import io.kotlintest.specs.FunSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class LinkedListTest : FunSpec() {
    private val list = LinkedList<Int>()

    init {
        test("LinkedList.add() should add the value to the list") {
            list.add(2)
            list.add(5)

            println(list)
            assertEquals(2, list.size)
            assertEquals(2, list.start!!.value)
            assertEquals(5, list.start!!.next!!.value)
        }

        test("Removing the middle element should work") {
            list.add(2)
            list.add(5)
            list.add(8)
            list.add(7)

            println(list)

            assertTrue(list.remove(5))
            assertEquals(8, list.start!!.next!!.value)

            println(list)
        }

        test("Removing the first element should shift the elements") {
            list.add(2)
            list.add(3)
            list.add(4)

            println(list)

            assertTrue(list.remove(2))
            assertEquals(3, list.start!!.value)

            println(list)
        }

        test("Remove a non-existent element") {
            list.add(1)
            list.add(2)

            assertFalse(list.remove(5))
        }
    }

    override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
        list.clear()
        println("-----${context.testCase.name}-----")
        test()
        println()
    }
}

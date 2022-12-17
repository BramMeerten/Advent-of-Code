package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import sun.security.provider.SHA
import kotlin.math.max

class Day17Test {

    val SHAPES: Array<Shape> = arrayOf(Shape.MIN, Shape.PLUS, Shape.J, Shape.I, Shape.SQUARE)

    @Test
    fun `part 1a`() {
        val pattern = readJetPattern(readSingleLine("day17/exampleInput.txt"))
        Assertions.assertEquals(3068, simulate(2022, 7, pattern))
    }

    @Test
    fun `part 1a real`() {
        val pattern = readJetPattern(readSingleLine("day17/input.txt"))
        Assertions.assertEquals(1514285714288, simulate(2022, 7, pattern))
    }

    fun readJetPattern(pattern: String): List<C> {
        return pattern.toCharList().map { c -> if (c == '<') C.LEFT else C.RIGHT }
    }

    fun simulate(rounds: Long, chamberWidth: Int, jetPattern: List<C>): Int {
        val chamber = Chamber(chamberWidth)

        var i =0
        var shapeI = 0
        for (n in 0 until rounds) {
            i = simulateFallingBlock(chamber, SHAPES[shapeI], jetPattern, i)
            shapeI = (shapeI+1) % SHAPES.size
        }
        return chamber.h
    }

    fun simulateFallingBlock(chamber: Chamber, shape: Shape, jetPattern: List<C>, jetI: Int): Int {
        // appear
        val height = chamber.h
        var pos = shape + C(2, height + 3)

        var i = jetI
        while(true) {
            // jet
            val jet = jetPattern[i]
            var targetPos = pos + jet
            if (chamber.fits(targetPos))
                pos = targetPos

            i = (i+1) % jetPattern.size

            // fall
            targetPos = pos + C.UP
            if (chamber.fits(targetPos))
                pos = targetPos
            else break
        }

        chamber.apply(pos)
//        chamber.print()
        return i
    }

    class Shape(val blocks: Set<C>) {
        val w: Int
        val h: Int

        init {
            w = 1 + blocks.maxOf { it.x } - blocks.minOf { it.x }
            h = 1 + blocks.maxOf { it.y } - blocks.minOf { it.y }
        }

        operator fun plus(c: C): Shape {
            return Shape(blocks.map { it + c }.toSet())
        }

        fun heighest(): C {
            return blocks.maxBy { it.y }
        }

        fun print() {
            if (blocks.minOf { it.x } != 0) throw IllegalStateException("Should have a 0 X Co to print")
            if (blocks.minOf { it.y } != 0) throw IllegalStateException("Should have a 0 Y Co to print")

            for (y in 0 until h) {
                for (x in 0 until w) {
                    print(if (blocks.contains(C(x, y))) "#" else ".")
                }
                println()
            }
        }

        companion object {
            @JvmField val MIN = Shape(setOf(C(0, 0), C(1, 0), C(2, 0), C(3, 0)))
            @JvmField val PLUS = Shape(setOf(C(1, 0), C(0, 1), C(1, 1), C(2, 1), C(1, 2)))
            @JvmField val J = Shape(setOf(C(2, 2), C(2, 1), C(0, 0), C(1, 0), C(2, 0)))
            @JvmField val I = Shape(setOf(C(0, 0), C(0, 1), C(0, 2), C(0, 3)))
            @JvmField val SQUARE = Shape(setOf(C(0, 0), C(1, 0), C(0, 1), C(1, 1)))
        }
    }

    class Chamber(val w: Int) {
        val blocks: Array<HashMap<Int, Boolean>>
        var h: Int

        init {
            blocks = Array(w){HashMap()}
            h = 0
        }

        fun apply(shape: Shape) {
            h = max(h, shape.heighest().y + 1)
            shape.blocks.forEach{b -> blocks.get(b.x)[b.y] = true}
        }

        fun fits(shape: Shape): Boolean {
            return !shape.blocks.any { block ->
                if (block.x < 0 || block.x >= w) return false
                if (block.y < 0) return false
                blocks[block.x].get(block.y) != null
            }
        }

        fun print() {
            val heighest = max(3, h)

            for (row in (0 until heighest).reversed()) {
                val blocks = blocks.map { col -> col.getOrElse(row) { false } }.joinToString("") { if (it) "#" else "." }
                println("|$blocks|")
            }
            println("+-------+\n")
        }
    }
}

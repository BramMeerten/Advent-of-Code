package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.max

val NUM_ROWS_FOR_PATTERN = 100

class Day17Test {

    val SHAPES: Array<Shape> = arrayOf(Shape.MIN, Shape.PLUS, Shape.J, Shape.I, Shape.SQUARE)

    @Test
    fun `part 1a`() {
        val pattern = readJetPattern(readSingleLine("day17/exampleInput.txt"))
        Assertions.assertEquals(3068, simulate(2022, chamberWidth = 7, pattern))
    }

    @Test
    fun `part 1b`() {
        val pattern = readJetPattern(readSingleLine("day17/input.txt"))
        Assertions.assertEquals(3224, simulate(2022, chamberWidth = 7, pattern))
    }

    @Test
    fun `part 2a`() {
        val pattern = readJetPattern(readSingleLine("day17/exampleInput.txt"))
        Assertions.assertEquals(1514285714288L, simulate(1000000000000L, chamberWidth = 7, pattern))
    }

    @Test
    fun `part 2b`() {
        val pattern = readJetPattern(readSingleLine("day17/input.txt"))
        Assertions.assertEquals(1595988538691, simulate(1000000000000L, chamberWidth = 7, pattern)) // < 1600571428577
    }

    fun readJetPattern(pattern: String): List<C> {
        return pattern.toCharList().map { c -> if (c == '<') C.LEFT else C.RIGHT }
    }

    fun simulate(rounds: Long, chamberWidth: Int, jetPattern: List<C>): Long {
        val chamber = Chamber(chamberWidth)

        val prevs: HashMap<String, Pair<Int, Long>> = hashMapOf()

        var roundsLeftToSimulate = 0L
        var simulatedGrowth = 0L

        var i =0
        var shapeI = 0
        for (n in 0 until rounds) {
            i = simulateFallingBlock(chamber, SHAPES[shapeI], jetPattern, i)
            shapeI = (shapeI + 1) % SHAPES.size

            if (chamber.h >= NUM_ROWS_FOR_PATTERN) {
                val c = chamber.getCachedState(i, shapeI)
                if (prevs.contains(c)) {
                    val prevHeightAndRound = prevs[c]
                    val growthSinceRepeat = chamber.h - prevHeightAndRound!!.first
                    val roundsSinceRepeat = n - prevHeightAndRound.second
                    val roundsLeft = rounds - n
                    simulatedGrowth = ((roundsLeft / roundsSinceRepeat) * growthSinceRepeat)
                    roundsLeftToSimulate = roundsLeft % roundsSinceRepeat
                    break
                }
                prevs[c] = chamber.h to n
            }
        }

        for (n in 0 until roundsLeftToSimulate-1) {
            i = simulateFallingBlock(chamber, SHAPES[shapeI], jetPattern, i)
            shapeI = (shapeI + 1) % SHAPES.size
        }

        return chamber.h + simulatedGrowth
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
        return i
    }

    class Shape(val blocks: Set<C>) {
        val w: Int = 1 + blocks.maxOf { it.x } - blocks.minOf { it.x }

        operator fun plus(c: C): Shape = Shape(blocks.map { it + c }.toSet())

        fun heighest(): C = blocks.maxBy { it.y }

        companion object {
            @JvmField val MIN = Shape(setOf(C(0, 0), C(1, 0), C(2, 0), C(3, 0)))
            @JvmField val PLUS = Shape(setOf(C(1, 0), C(0, 1), C(1, 1), C(2, 1), C(1, 2)))
            @JvmField val J = Shape(setOf(C(2, 2), C(2, 1), C(0, 0), C(1, 0), C(2, 0)))
            @JvmField val I = Shape(setOf(C(0, 0), C(0, 1), C(0, 2), C(0, 3)))
            @JvmField val SQUARE = Shape(setOf(C(0, 0), C(1, 0), C(0, 1), C(1, 1)))
        }
    }

    class Chamber(val w: Int) {
        val blocks: Array<HashMap<Int, Boolean>> = Array(w){HashMap()}
        var h: Int = 0

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

        fun getCachedState(patternPos: Int, shapePos: Int): String {
            return "$shapePos" + ("$patternPos".padStart(5, '0')) + (1 .. NUM_ROWS_FOR_PATTERN).flatMap { y ->
                (0 until w).map { x ->
                    if (blocks[x][h-y] ?: false) "#" else "."
                }
            }.joinToString("")
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


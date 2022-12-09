package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.sign

class Day9Test {

    @Test
    fun `part 1`() {
        val result = solve("day9/exampleInput.txt", 2, print = true)
        Assertions.assertEquals(13, result)
    }

    @Test
    fun `part 1b`() {
        val result = solve("day9/input.txt", 2)
        Assertions.assertEquals(5883, result)
    }

    @Test
    fun `part 2`() {
        val result = solve("day9/exampleInput2.txt", 10)
        Assertions.assertEquals(36, result)
    }

    @Test
    fun `part 2b`() {
        val result = solve("day9/input.txt", 10)
        Assertions.assertEquals(2367, result)
    }

    fun solve(file: String, snakeLength: Int, print: Boolean = false): Int {
        val map = RopeMap(snakeLength)
        readFile(file)
            .map { extractRegexGroups("(.) (\\d+)", it) }
            .flatMap { (dir, num) ->  dir.repeat(num.toInt()).toCharList() }
            .map { char ->
                when (char) {
                    'U' -> Co(-1, 0)
                    'D' -> Co(1, 0)
                    'R' -> Co(0, 1)
                    else -> Co(0, -1)
                }
            }.forEach { move ->
                map.move(move)
                if (print) map.print()
            }
        return map.visited.size
    }

    class RopeMap(length: Int) {
        private var snake: MutableList<Co>
        val visited: HashSet<Co> = HashSet()

        private var topLeft: Co = Co(-2, -2)
        private var bottomRight: Co = Co(2, 2)

        init {
            snake = generateSequence { Co(0,0) }.take(length).toMutableList()
            visited.add(snake[snake.size-1])
        }

        fun move(co: Co) {
            snake[0] = snake[0] + co
            for (i in 1 until snake.size)
                snake[i] = follow(snake[i-1], snake[i])

            visited.add(snake[snake.size-1])
            topLeft = topLeft.min(snake[0])
            bottomRight = bottomRight.max(snake[0])
        }

        private fun follow(head: Co, tail: Co): Co {
            val colDiff = head.col - tail.col
            val rowDiff = head.row - tail.row

            return if (abs(colDiff) <= 1 && abs(rowDiff) <= 1)
                tail
            else
                Co(tail.row + rowDiff.sign, tail.col + colDiff.sign)
        }

        fun print() {
            for (row in topLeft.row..bottomRight.row) {
                for (col in topLeft.col..bottomRight.col) {
                    if (snake[0] == Co(row, col))
                        print("H ")
                    else if (snake[snake.size-1] == Co(row, col))
                        print("T ")
                    else {
                        val i = snake.indexOf(Co(row, col))
                        if (i != -1)
                            print("" + (i + 1) + " ")
                        else if (visited.contains(Co(row, col)))
                            print("# ")
                        else print(". ")
                    }
                }
                println()
            }
            println("\n")
        }
    }
}
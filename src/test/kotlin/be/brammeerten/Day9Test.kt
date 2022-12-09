package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.abs

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
                if (print) {
                    map.print()
                    println("\n")
                }
            }
        return map.visited.size
    }

    class RopeMap(length: Int) {
        var snake: MutableList<Co>
        val visited: HashSet<Co> = HashSet()

        private var topLeft: Co = Co(-2, -2)
        private var bottomRight: Co = Co(2, 2)

        init {
            snake = generateSequence { Co(0,0) }.take(length).toMutableList()
            visited.add(snake[snake.size-1])
        }

        fun move(co: Co) {
            snake[0] = snake[0].add(co)
            for (i in 1 until snake.size) {
                snake[i] = follow(snake[i-1], snake[i])
            }
            visited.add(snake[snake.size-1])
            topLeft = topLeft.min(snake[0])
            bottomRight = bottomRight.max(snake[0])
        }

        fun follow(head: Co, tail: Co): Co {
            val colDiff = abs(head.col - tail.col)
            val rowDiff = abs(head.row-tail.row)
            if (colDiff <= 1 && rowDiff <= 1)
                return tail

            if (head.row == tail.row)
                return Co(tail.row, tail.col + signI(head.col-tail.col))
            else if (head.col == tail.col)
                return Co(tail.row + signI(head.row-tail.row), tail.col)
            else
                return Co(tail.row + signI(head.row-tail.row), tail.col + signI(head.col-tail.col))
        }

        fun print() {
            for (row in topLeft.row..bottomRight.row) {
                for (col in topLeft.col..bottomRight.col) {
                    var f = true
                    if (snake[0] == Co(row, col)) print("H ")
                    else if (snake[snake.size-1] == Co(row, col)) print("T ")
                    else {
                        f = false
                        for ((index, _) in snake.withIndex()) {
                            if (snake[index] == Co(row, col)) {
                                print("" + (index + 1) + " ")
                                f = true
                                break
                            }
                        }
                    }
                    if (!f && visited.contains(Co(row, col))) {
                        print("# ")
                    } else if (!f) {
                        print(". ")
                    }
                }
                println()
            }
        }
    }
}
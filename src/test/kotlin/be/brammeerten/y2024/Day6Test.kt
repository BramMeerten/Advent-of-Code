package be.brammeerten.y2024

import be.brammeerten.Co
import be.brammeerten.Co.Companion.DOWN
import be.brammeerten.Co.Companion.LEFT
import be.brammeerten.Co.Companion.RIGHT
import be.brammeerten.Co.Companion.UP
import be.brammeerten.readFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class Day6Test {

    private val DIRECTIONS = listOf(UP, RIGHT, DOWN, LEFT)

    @Test
    fun `part 1`() {
        val board = readMap("2024/day6/exampleInput.txt")

        val visited: MutableSet<Co> = mutableSetOf()
        move(board.width, board.height, board.obstacles, board.position, visited)
        assertThat(visited.size).isEqualTo(41);
//        assertThat(sum).isEqualTo(4826);
    }

    @Test
    fun `part 2`() {
        val board = readMap("2024/day6/exampleInput.txt")

        val obstacleCandidates: MutableSet<Co> = mutableSetOf()
        move(board.width, board.height, board.obstacles, board.position, obstacleCandidates)

        val obstacles = obstacleCandidates
            .filter { it != board.position }
            .filter {
                hasLoop(board.width, board.height, board.obstacles + it, board.position);
            }

        assertThat(obstacles.size).isEqualTo(6);
//        assertThat(sum).isEqualTo(1721);
    }

    private fun move(w: Int, h: Int, obstacles: Set<Co>, position: Co, visited: MutableSet<Co>, direction: Co = UP) {
        if (position.col >= 0 && position.col < w && position.row >= 0 && position.row < h) {
            visited.add(position)
        } else {
            return
        }

        val newPos = position + direction
        if (obstacles.contains(newPos)) {
            val index = DIRECTIONS.indexOf(direction)
            move(w, h, obstacles, position, visited, DIRECTIONS[(index+1) % DIRECTIONS.size])
        } else {
            move(w, h, obstacles, newPos, visited, direction)
        }
    }

    private fun hasLoop(w: Int, h: Int, obstacles: Set<Co>, position: Co, visited: MutableSet<Pair<Co, Co>> = mutableSetOf(), direction: Co = UP): Boolean {
        if (position.col >= 0 && position.col < w && position.row >= 0 && position.row < h) {
            if (visited.contains(Pair(position, direction))) {
                return true
            }
            visited.add(Pair(position, direction))
        } else {
            return false
        }

        val newPos = position + direction
        if (obstacles.contains(newPos)) {
            val index = DIRECTIONS.indexOf(direction)
            return hasLoop(w, h, obstacles, position, visited, DIRECTIONS[(index+1) % DIRECTIONS.size])
        } else {
            return hasLoop(w, h, obstacles, newPos, visited, direction)
        }
    }

    private fun readMap(file: String): Board {
        val lines = readFile(file)
            .filter { it.isNotEmpty() }
        var start = Co(-1, -1)
        val obstacles = mutableSetOf<Co>()
        for (row in lines.indices) {
            for (col in lines[row].indices) {
                if (lines[row][col] == '#') {
                    obstacles.add(Co(row, col))
                } else if (lines[row][col] == '^') {
                    start = Co(row, col)
                }
            }
        }

        return Board(obstacles, lines[0].length, lines.size, start)
    }

    data class Board(val obstacles: Set<Co>, val width: Int, val height: Int, val position: Co) {

    }
}

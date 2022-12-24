package be.brammeerten

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class Day24Test {

    val DIRECTIONS = listOf(C(0, 0), C.LEFT, C.RIGHT, C.UP, C.DOWN)

    @Test
    fun `part 1a`() {
        val valley = readValley("day24/exampleInput.txt")
        Assertions.assertThat(solve(valley, start = C(0, -1), stop = C(5, 4))).isEqualTo(18)
    }

    @Test
    fun `part 1b`() {
        val valley = readValley("day24/input.txt")
        Assertions.assertThat(solve(valley, start = C(0, -1), stop = C(99, 35))).isEqualTo(230)
    }

    @Test
    fun `part 2a`() {
        val valley = readValley("day24/exampleInput.txt")
        Assertions.assertThat(solveThereAndBackAgainAndBackAgain(valley, start = C(0, -1), stop = C(5, 4))).isEqualTo(54)
    }

    @Test
    fun `part 2b`() {
        val valley = readValley("day24/input.txt")
        Assertions.assertThat(solveThereAndBackAgainAndBackAgain(valley, start = C(0, -1), stop = C(99, 35))).isEqualTo(713)
    }

    fun solve(valley: Valley, start: C, stop: C): Int {
        val blizzardStates = getBlizzardStates(valley)
        return solve(start, stop, startTime = 0, blizzardStates)
    }

    fun solveThereAndBackAgainAndBackAgain(valley: Valley, start: C, stop: C): Int {
        val blizzardStates = getBlizzardStates(valley)
        var time = solve(start, stop, startTime = 0, blizzardStates)
        time     = solve(stop, start, startTime = time, blizzardStates)
        return     solve(start, stop, startTime = time, blizzardStates)
    }

    fun solve(start: C, stop: C, startTime: Int, blizzardStates: Array<Array<Array<Boolean>>>): Int {
        val queue = LinkedList<Pair<Int, C>>()
        val visited = Array(blizzardStates.size) { HashSet<C>() }

        queue.add(startTime to start)
        visited[startTime % blizzardStates.size].add(start)

        while (!queue.isEmpty()) {
            val node = queue.remove()
            for (option in getOptions(node, blizzardStates, start, stop)) {
                val time = option.first % blizzardStates.size
                val newNode = option.second
                if (!visited[time].contains(newNode)) {
                    queue.add(option)
                    visited[time].add(newNode)
                    if (newNode == stop)
                        return node.first + 1
                }
            }
        }

        throw IllegalStateException("Niet opgelost")
    }

    fun getOptions(cur: Pair<Int, C>, blizzardState: Array<Array<Array<Boolean>>>, start: C, stop: C): List<Pair<Int, C>> {
        val newTime = cur.first + 1
        val newState = blizzardState[newTime % blizzardState.size]
        return DIRECTIONS
            .map { cur.second + it }
            .filter { it == start || it == stop || (it.x >= 0 && it.y >= 0 && it.x < newState[0].size && it.y < newState.size) }
            .filter { it == start || it == stop || !newState[it.y][it.x] }
            .map { newTime to it }
    }

    fun getBlizzardStates(valley: Valley): Array<Array<Array<Boolean>>> {
        val repeatsAfter = (valley.w * valley.h) / gcd(valley.w, valley.h)
        val state = Array(repeatsAfter) { Array(valley.h) { Array(valley.w) { false } } }
        var cur = valley
        for (time in 0 until repeatsAfter) {
            cur.blizzards.keys.forEach { pos -> state[time][pos.y][pos.x] = true }
            cur = cur.step()
        }
        return state
    }

    fun readValley(file: String): Valley {
        val rows = readFile(file).drop(1).dropLast(1)
        val h = rows.size
        val w = rows[0].length - 2

        return Valley(w, h, rows.flatMapIndexed { y, row ->
            row.toCharList().drop(1).dropLast(1).mapIndexedNotNull { x, c ->
                when (c) {
                    '>' -> C(x, y) to listOf(C(1, 0))
                    '<' -> C(x, y) to listOf(C(-1, 0))
                    '^' -> C(x, y) to listOf(C(0, -1))
                    'v' -> C(x, y) to listOf(C(0, 1))
                    else -> null
                }
            }
        }.toMap())
    }

    data class Valley(val w: Int, val h: Int, val blizzards: Map<C, List<C>>) {

        fun step(): Valley {
            val newBlizzards = hashMapOf<C, ArrayList<C>>()
            blizzards
                .flatMap { blizzard -> blizzard.value.map { blizzard.key to it } }
                .forEach { (pos, dir) ->
                    var newPos = pos + dir
                    newPos = C((newPos.x + w) % w, (newPos.y + h) % h)
                    newBlizzards.putIfAbsent(newPos, arrayListOf())
                    newBlizzards[newPos]!!.add(dir)
                }
            return Valley(w, h, newBlizzards)
        }
    }
}
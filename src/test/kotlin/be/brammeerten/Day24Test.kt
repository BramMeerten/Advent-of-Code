package be.brammeerten

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class Day24Test {
//    var START = C(0, -1)
//    var STOP = C(5, 4)
    var START = C(0, -1)
    var STOP = C(99, 35)

    @Test
    fun `part 1a`() {
        val valley = readValley("day24/exampleInput.txt")
        Assertions.assertThat(solve(valley)).isEqualTo(18)
    }

    @Test
    fun `part 1b`() {
        val valley = readValley("day24/input.txt")
        Assertions.assertThat(solve(valley)).isEqualTo(18)
    }

    @Test
    fun `part 2a`() {
        val valley = readValley("day24/exampleInput.txt")
        Assertions.assertThat(solve(valley)).isEqualTo(18)
    }

    @Test
    fun `part 2b`() {
        val valley = readValley("day24/input.txt")
        Assertions.assertThat(solve(valley)).isEqualTo(713)
    }

    fun solve(valley: Valley): Int {
        val blizzardStates = getBlizzardStates(valley)

        val queue = LinkedList<Pair<Int, C>>()
        var visited = Array(blizzardStates.size){HashSet<C>()}
        val prevs = Array<Array<Array<C?>>>(blizzardStates.size){Array(valley.h){Array(valley.w){null} } }
        val startPrevs = Array<C?>(blizzardStates.size){null} // because outside of map
        queue.add(0 to START)
        visited[0].add(START)

        var finished: Pair<Int, C>? = null
        while (!queue.isEmpty() && finished == null) {
            val node = queue.remove()
            for (option in getOptions(node, blizzardStates)) {
                val time = option.first % blizzardStates.size
                val newNode = option.second
                if (!visited[time].contains(newNode)) {
                    queue.add(option)
                    visited[time].add(newNode)
                    if (newNode == STOP) {
                        finished = node
                        break
                    } else if (newNode == START) {
                        startPrevs[time] = node.second
                    } else {
                        prevs[time][newNode.y][newNode.x] = node.second
                    }
                }
            }
            if (queue.isEmpty())
                println("last time tried was: " + node.first)
        }






        val swap = START
        START = STOP
        STOP = swap
        visited = Array(blizzardStates.size){HashSet<C>()}
        queue.clear()
        queue.add(finished!!.first to START)
        visited[finished.first % blizzardStates.size].add(START)
        finished = null
        while (!queue.isEmpty() && finished == null) {
            val node = queue.remove()
            for (option in getOptions(node, blizzardStates)) {
                val time = option.first % blizzardStates.size
                val newNode = option.second
                if (!visited[time].contains(newNode)) {
                    queue.add(option)
                    visited[time].add(newNode)
                    if (newNode == STOP) {
                        finished = node
                        break
                    } else if (newNode == START) {
                        startPrevs[time] = node.second
                    } else {
                        prevs[time][newNode.y][newNode.x] = node.second
                    }
                }
            }
            if (queue.isEmpty())
                println("last time tried was: " + node.first)
        }



        val swap2 = START
        START = STOP
        STOP = swap2
        visited = Array(blizzardStates.size){HashSet<C>()}
        queue.clear()
        queue.add(finished!!.first to START)
        visited[finished.first % blizzardStates.size].add(START)
        finished = null
        while (!queue.isEmpty() && finished == null) {
            val node = queue.remove()
            for (option in getOptions(node, blizzardStates)) {
                val time = option.first % blizzardStates.size
                val newNode = option.second
                if (!visited[time].contains(newNode)) {
                    queue.add(option)
                    visited[time].add(newNode)
                    if (newNode == STOP) {
                        finished = node
                        break
                    } else if (newNode == START) {
                        startPrevs[time] = node.second
                    } else {
                        prevs[time][newNode.y][newNode.x] = node.second
                    }
                }
            }
            if (queue.isEmpty())
                println("last time tried was: " + node.first)
        }


        // get path
        if (finished == null)
            throw IllegalStateException("Niet opgelost")
        else {
            val path = arrayListOf<C>(STOP, finished.second)
            println(finished.first+1)
            for (time in (1 .. finished.first).reversed()) {
                if (path.last() == START)
                    path.add(startPrevs[time % blizzardStates.size]!!)
                else
                    path.add(prevs[time % blizzardStates.size][path.last().y][path.last().x]!!)
            }
            println(path.reversed())
        }

        return finished.first+1
    }

    fun getOptions(cur: Pair<Int, C>, blizzardState: Array<Array<Array<Boolean>>>): List<Pair<Int, C>> {
        val POSITIONS = listOf(C(0, 0), C(-1, 0), C(1, 0), C(0, -1), C(0, 1))


        val newTime = cur.first + 1
        val newState = blizzardState[newTime % blizzardState.size]
        return POSITIONS
            .map { cur.second + it }
            .filter { it == START || it == STOP || (it.x >= 0 && it.y >= 0 && it.x < newState[0].size && it.y < newState.size) }
            .filter { it == START || it == STOP || !newState[it.y][it.x] }
            .map { newTime to it }
    }

    fun getBlizzardStates(valley: Valley): Array<Array<Array<Boolean>>> {
        val repeatsAfter = (valley.w * valley.h) / gcd(valley.w, valley.h)
        val state = Array(repeatsAfter){Array(valley.h){Array(valley.w){false} } }
        var cur = valley
        for (time in 0 until repeatsAfter) {
            cur.blizzards.keys.forEach{pos ->
                state[time][pos.y][pos.x] = true
            }
            cur = cur.step()
        }
        return state
    }

    fun readValley(file: String): Valley {
        val rows = readFile(file).drop(1).dropLast(1)
        val h = rows.size
        val w = rows[0].length-2

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
                .forEach{(pos, dir) ->
                    var newPos = pos + dir
                    newPos = C((newPos.x+w) % w, (newPos.y+h) % h)
                    newBlizzards.putIfAbsent(newPos, arrayListOf())
                    newBlizzards[newPos]!!.add(dir)
                }
            return Valley(w, h, newBlizzards)
        }
    }

    fun printState(state: Array<Array<Boolean>>) {
        for(y in 0 until state.size) {
            for (x in 0 until state[y].size) {
                if (state[y][x]) print("#")
                else print(".")
            }
            println()
        }
        println()
    }

}
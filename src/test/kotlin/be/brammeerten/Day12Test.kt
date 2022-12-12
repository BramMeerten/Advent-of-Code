package be.brammeerten

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.LinkedList

class Day12Test {

    @Test
    fun `part 1a`() {
        val map = readMap("day12/exampleInput.txt")
        val bestPath = map.solveBreadthFirst()
        Assertions.assertEquals(31, bestPath!!.size-1)
    }

    @Test
    fun `part 1b`() {
        val map = readMap("day12/input.txt")
        val bestPath = map.solveBreadthFirst()
        Assertions.assertEquals(330, bestPath!!.size-1)
    }

    @Test
    fun `part 2a`() {
        val map = readMap("day12/exampleInput.txt")
        val shortest = map.findLeastStepsFromLowestPoints()
        Assertions.assertEquals(29, shortest)
    }

    @Test
    fun `part 2b`() {
        val map = readMap("day12/input.txt")
        val shortest = map.findLeastStepsFromLowestPoints()
        Assertions.assertEquals(321, shortest)
    }

    fun readMap(file: String): MMap {
        var start: Co? = null
        var end: Co? = null
        val map = readFile(file).mapIndexed { rowI, row ->
            row.toCharList().mapIndexed { colI, col ->
                when (col) {
                    'S' -> {
                        start = Co(rowI, colI)
                        'a'.toAlphabetIndex()
                    }

                    'E' -> {
                        end = Co(rowI, colI)
                        'z'.toAlphabetIndex()
                    }

                    else -> col.toAlphabetIndex()
                }
            }
        }
        return MMap(map, start!!, end!!)
    }

    data class MMap(val map: List<List<Int>>, val start: Co, val end: Co) {
        val w = map[0].size
        val h = map.size

        fun getBestPath(): List<Co> {
            return getBestPathBrute(start, emptyList())!!
        }

        fun findLeastStepsFromLowestPoints(): Int {
            val x: List<Int> = map.mapIndexed {rowI, row ->
                row.mapIndexed{colI, col ->
                    if (col == 'a'.toAlphabetIndex()) {
                        val solution = solveBreadthFirst(rowI * w + colI)
                        if (solution != null)
                            solution.size - 1
                        else null
                    } else null
                }
            }.flatten().filterNotNull()
            return x.min()
        }

        fun solveBreadthFirst(startI: Int = start.row*w + start.col):  List<Int>? {
            val graph = toGraph()
            val queue = LinkedList<Int>()
            val visited = HashSet<Int>()
            val prevs = Array<Int?>(w*h){null}

            queue.add(startI)
            visited.add(startI)

            while(!queue.isEmpty()) {
                val node = queue.remove()
                for (neighbour in graph.nodes[node].neighbours) {
                    if (!visited.contains(neighbour)) {
                        queue.add(neighbour)
                        visited.add(neighbour)
                        prevs[neighbour] = node
                    }
                }
            }

            // get path
            val endI = end.row*w + end.col
            val path = ArrayList<Int>()
            var i: Int? = endI
            while (i != startI && i != null) {
                path.add(i)
                i = prevs[i]
            }

            if (i == null) return null
            path.add(i)

            return path
        }

        fun toGraph(): Graph {
            val graph = Graph(Array(w*h) { i -> Node(emptyList()) }) // forEaches kunnen eigenlijk hier
            map.forEachIndexed{rowI, row ->
                row.forEachIndexed{colI, col ->
                    val nodeI = rowI * w + colI
                    val node = Node(getNeighbours(Co(rowI, colI)).map { c -> c.row*w+c.col })
                    graph.nodes[nodeI] = node
                }
            }
            return graph
        }

        fun getBestPathBrute(pos: Co, path: List<Co>): List<Co>? {
            val options = getOptions(pos, path)
            if (options.isEmpty() && pos != end)
                return null
            else {
                val newPath = ArrayList(path)
                newPath.add(pos)
                if (pos == end)
                    return newPath

                var bestSolution: List<Co>? = null
                for (option in options) {
                    val solution = getBestPathBrute(option, newPath)
                    if (solution != null && (bestSolution == null || bestSolution.size > solution.size))
                        bestSolution = solution
                }
                return bestSolution
            }
        }

        fun getOptions(pos: Co, path: List<Co>): List<Co> {
            val directions = listOf(Co(-1, 0), Co(1, 0), Co(0, -1), Co(0, 1))
            return directions
                .map { pos + it }
                .filter { it.row >= 0 && it.col >= 0 }
                .filter { it.row < map.size && it.col < map[it.row].size }
                .filter { newCo ->
                    map[newCo.row][newCo.col] - 1 <= map[pos.row][pos.col] && !path.contains(newCo)
                }
        }

        fun getNeighbours(pos: Co): List<Co> {
            val directions = listOf(Co(-1, 0), Co(1, 0), Co(0, -1), Co(0, 1))
            return directions
                .map { pos + it }
                .filter { it.row >= 0 && it.col >= 0 }
                .filter { it.row < map.size && it.col < map[it.row].size }
                .filter { newCo -> map[newCo.row][newCo.col] - 1 <= map[pos.row][pos.col] }
        }

        fun print() {
            map.forEach { row ->
                row.forEach { print((it + 'a'.toByte().toInt()).toChar()) }
                println()
            }
        }
    }

    data class Graph(val nodes: Array<Node>)
    data class Node(val neighbours: List<Int>)

}
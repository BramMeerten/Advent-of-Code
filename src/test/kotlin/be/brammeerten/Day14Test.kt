package be.brammeerten

import be.brammeerten.Tile.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.math.min

class Day14Test {

    @Test
    fun `part 1a`() {
        val map = Cave(scanCave("day14/exampleInput.txt"))
        simulate(map, print = true)
        Assertions.assertEquals(24, map.sandCount())
    }

    @Test
    fun `part 1b`() {
        val map = Cave(scanCave("day14/input.txt"))
        simulate(map)
        Assertions.assertEquals(592, map.sandCount())
    }
}

val DOWN_LEFT = Co.DOWN + Co.LEFT
val DOWN_RIGHT = Co.DOWN + Co.RIGHT

fun simulate(cave: Cave, print: Boolean = false) {
    var direction = Co.DOWN
    var sand = cave.source

    // while
    while (cave.isInBounds(sand)) {
        sand += direction
        val tryDirections = listOf(Co.DOWN, DOWN_LEFT, DOWN_RIGHT)
        var newSand: Co? = null
        for (dir in tryDirections) {
            if (!cave.isInBounds(sand + dir) || cave[sand + dir] == AIR) {
                direction = dir
                newSand = sand + dir
                break
            }
        }

        if (newSand == null) {
            cave[sand] = SAND
            sand = cave.source
            direction = Co.DOWN
            if (print) cave.print()
        }
    }
}

fun scanCave(file: String): List<List<Pair<Int, Int>>> {
    return readFile(file)
        .map { it.split(" -> ") }
        .map { cos -> cos.map { it.split(",")[0].toInt() to it.split(",")[1].toInt() } }
}

class Cave(scans: List<List<Pair<Int, Int>>>) {
    val w: Int
    val h: Int
    val source: Co = Co(0, 500)
    val map: Array<Tile>
    val start: Co

    init {
        var topLeft = source
        var bottomRight = source
        val rocks = scans.flatMap {
            it
                .windowed(2)
                .flatMap { (p1, p2) ->
                    if (p1.first == p2.first)
                        (min(p1.second, p2.second)..max(p1.second, p2.second))
                            .map { sec -> p1.first to sec }
                    else
                        (min(p1.first, p2.first)..max(p1.first, p2.first))
                            .map { first -> first to p1.second }
                }
        }.map { (col, row) -> Co(row, col) }

        rocks.forEach { co ->
            topLeft = topLeft.min(co)
            bottomRight = bottomRight.max(co)
        }

        start = topLeft
        w = bottomRight.col - topLeft.col + 1
        h = bottomRight.row - topLeft.row + 1
        map = Array(w*h) { AIR }
        rocks.forEach { rock -> set(rock, ROCK) }
    }

    operator fun set(co: Co, value: Tile) {
        val c = co - start
        val i = c.row * w + c.col
        map[i] = value
    }

    operator fun get(co: Co): Tile {
        val c = co - start
        return get(c.row, c.col)
    }

    fun get(row: Int, col: Int): Tile {
        return map[row * w + col]
    }

    fun isInBounds(co: Co): Boolean {
        val c = co - start
        return c.col >= 0 && c.row >= 0 && c.col < w && c.row < h
    }

    fun sandCount(): Int {
        return map.toList().count { it == SAND }
    }

    fun print() {
        for (row in 0 until h) {
            for (col in 0 until h) {
                print(when(get(row, col)) {
                    ROCK -> "#"
                    AIR -> "."
                    SAND -> "O"
                })
            }
            println()
        }
        println()
    }
}

enum class Tile {
    ROCK, AIR, SAND
}
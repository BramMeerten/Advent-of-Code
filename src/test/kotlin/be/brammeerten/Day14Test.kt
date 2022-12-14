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

    @Test
    fun `part 2a`() {
        val map = Cave(scanCave("day14/exampleInput.txt"), infiniteFloor = true)
        simulate(map, print = true)
        Assertions.assertEquals(93, map.sandCount())
    }

    @Test
    fun `part 2b`() {
        val map = Cave(scanCave("day14/input.txt"), infiniteFloor = true)
        simulate(map)
        Assertions.assertEquals(30367, map.sandCount())
    }
}

val DOWN_LEFT = Co.DOWN + Co.LEFT
val DOWN_RIGHT = Co.DOWN + Co.RIGHT

fun simulate(cave: Cave, print: Boolean = false) {
    var sand = cave.source

    while (cave.isInBounds(sand)) {
        val tryDirections = listOf(Co.DOWN, DOWN_LEFT, DOWN_RIGHT)
        var direction: Co? = null
        for (dir in tryDirections) {
            if (!cave.isInBounds(sand + dir) || cave[sand + dir] == AIR) {
                direction = dir
                break
            }
        }

        if (direction == null) {
            cave[sand] = SAND
            if (print) cave.print()
            if (sand == cave.source) break
            else sand = cave.source
        } else {
           sand += direction
        }
    }
}

fun scanCave(file: String): List<List<Pair<Int, Int>>> {
    return readFile(file)
        .map { it.split(" -> ") }
        .map { cos -> cos.map { it.split(",")[0].toInt() to it.split(",")[1].toInt() } }
}

class Cave(scans: List<List<Pair<Int, Int>>>, val infiniteFloor: Boolean = false) {
    val source: Co = Co(0, 500)
    val map: HashMap<Co, Tile> = HashMap()
    val bottomFloor: Int

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
            map[co] = ROCK
        }

        bottomFloor = bottomRight.row - topLeft.row + (if (infiniteFloor) 2 else 0)
    }

    operator fun set(co: Co, value: Tile) {
        map.put(co, value)
    }

    operator fun get(co: Co): Tile {
        if (infiniteFloor && co.row == bottomFloor) return ROCK
        return map[co] ?: AIR
    }

    fun isInBounds(co: Co): Boolean {
        return co.row <= bottomFloor
    }

    fun sandCount(): Int {
        return map.filter { (_, value) -> value == SAND }.count()
    }

    fun print() {
        for (row in 0 .. bottomFloor) {
            for (col in source.col-15 until source.col+15) {
                print(when(get(Co(row, col))) {
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
package be.brammeerten

import com.sun.org.apache.xpath.internal.operations.Bool
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day23Test {

    @Test
    fun `part 1a`() {
        val map = readMap("day23/exampleInput.txt")
        map.doRounds(10, print = true)
        assertThat(map.countEmptyTiles()).isEqualTo(110)
    }

    @Test
    fun `part 1b`() {
        val map = readMap("day23/input.txt")
        map.doRounds(10)
        assertThat(map.countEmptyTiles()).isEqualTo(4208)
    }

    @Test
    fun `part 2a`() {
        val map = readMap("day23/exampleInput.txt")
        assertThat(map.findRoundNoMovement()).isEqualTo(20)
    }

    @Test
    fun `part 2b`() {
        val map = readMap("day23/input.txt")
        assertThat(map.findRoundNoMovement()).isEqualTo(1016)
    }

    fun readMap(file: String): MMap {
        return MMap(readFile(file)
            .flatMapIndexed { y, row -> row.toCharList()
                .mapIndexed{x, value -> C(x, y) to value }
                .filter{it.second == '#'}
                .map { it.first }
            }.toSet())
    }

    class MMap(var map: Set<Elf>) {
        var min: C = map.reduce{a, b -> a.min(b)}
        var max: C = map.reduce{a, b -> a.max(b)}
        val POSITIONS = (-1..1).flatMap { y -> (-1..1).map { x -> C(x,y) } }.filter { it != C(0, 0) }
        val directions: ArrayList<Pair<C, List<C>>> = arrayListOf(
            C(0, -1) to listOf(C(-1, -1), C(0, -1), C(1, -1)),
            C(0, 1) to listOf(C(-1, 1), C(0, 1), C(1, 1)),
            C(-1, 0) to listOf(C(-1, -1), C(-1, 0), C(-1, 1)),
            C(1, 0) to listOf(C(1, -1), C(1, 0), C(1, 1)),
        )

        fun doRounds(n: Int = 10, print: Boolean = false) = repeat(n) { doRound(it + 1, print) }

        fun findRoundNoMovement(): Int {
            var n = 1
            while(doRound(n, false)) {
                n++
            }
            return n
        }

        fun doRound(n: Int, print: Boolean): Boolean {
            // part 1
            val targets: HashMap<Elf, C> = hashMapOf()
            val stay = hashSetOf<Elf>()
            for (elf in map) {
                val freePositions = getFreePositions(elf)
                if (freePositions.size == 8) {
                    stay.add(elf)
                } else {
                    for (option in directions) {
                        if (option.second.map { it + elf }.count { map.contains(it) } == 0) {
                            targets[elf] = elf + option.first
                            break
                        }
                    }
                    if (targets[elf] == null) stay.add(elf)
                }
            }

            // part 2
            val newMap: HashSet<Elf> = hashSetOf()
            var count = targets.size
            for (elf in map) {
                if (targets[elf] != null) {
                    if (targets.values.count { it == targets[elf] } > 1) {
                        newMap.add(elf)
                        count--
                    } else {
                        newMap.add(targets[elf]!!)
                    }
                }
            }
            newMap.addAll(stay)
            map = newMap
            min = map.reduce{a, b -> a.min(b)}
            max = map.reduce{a, b -> a.max(b)}

            // rotate options
            val first = directions.removeFirst()
            directions.add(first)

            if (print) {
                println("Round $n:")
                print()
            }

            return count > 0
        }

        fun getFreePositions(elf: Elf): List<C> {
            return POSITIONS.filter { p -> !map.contains(elf + p) }
        }

        fun countEmptyTiles(): Int {
            return (min.y..max.y).sumOf { y ->
                (min.x..max.x).count { x -> !map.contains(C(x, y)) }
            }
        }

        fun print() {
            (min.y..max.y).forEach { y ->
                (min.x..max.x).forEach { x -> print(if (map.contains(C(x, y))) "#" else ".") }
                println()
            }
            println()
        }

    }

}

typealias Elf = C
